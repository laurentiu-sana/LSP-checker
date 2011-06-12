package lsp.jpf.listeners;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.DynamicElementInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.ARETURN;
import gov.nasa.jpf.jvm.bytecode.ATHROW;
import gov.nasa.jpf.jvm.bytecode.INVOKEINTERFACE;
import gov.nasa.jpf.jvm.bytecode.IRETURN;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.report.PublisherExtension;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.bytecode.BytecodeUtils;
import gov.nasa.jpf.symbc.bytecode.INVOKESPECIAL;
import gov.nasa.jpf.symbc.bytecode.INVOKESTATIC;
import gov.nasa.jpf.symbc.bytecode.INVOKEVIRTUAL;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.string.DerivedStringExpression;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lsp.jpf.analyzer.ConstantAnalyser;
import lsp.jpf.parse.Constants;

import org.apache.log4j.Logger;

/**
 * Based on gov.nasa.jpf.symbc.SymbolicListener
 */
@SuppressWarnings( { "unchecked", "unused" })
public class LSPCheckerListener extends PropertyListenerAdapter implements
		PublisherExtension {
	private final static Logger LOGGER = Logger
			.getLogger(LSPCheckerListener.class);

	private boolean retainVal = false;
	private boolean forcedVal = false;

	private Map<String, MethodSummary> allSummaries;
	private String testName = "unknown";

	public LSPCheckerListener(Config conf, JPF jpf) {
		Constants.readConfigurationFile();
		Constants.GLOBAL_CONFIGURATION = conf;
		jpf.addPublisherExtension(ConsolePublisher.class, this);
		allSummaries = new HashMap<String, MethodSummary>();
	}

	public static boolean isSymbolic(Config conf, String className,
			MethodInfo mi, String methodName, int numberOfArgs) {
		try {
			if (!BytecodeUtils.isClassSymbolic(conf, className, mi, methodName)) {
				LOGGER.debug("[isSymbolic] method " + className + "."
						+ methodName + "[" + numberOfArgs
						+ "] not symbolic cause of the class");
				return false;
			}
			if (!BytecodeUtils.isMethodSymbolic(conf, methodName, numberOfArgs,
					null)) {
				LOGGER.debug("[isSymbolic] method " + className + "."
						+ methodName + "[" + numberOfArgs
						+ "] not symbolic cause of the method");
				return false;
			}
			LOGGER.debug("[isSymbolic] method " + className + "." + methodName
					+ "[" + numberOfArgs + "] symbolic");
			return true;
		} catch (Exception e) {
			LOGGER.debug("[isSymbolic] exception thrown " + e.getMessage());
			return false;
		}
	}

	private void addExtraSymbolicClasses(Instruction insn, Config conf) {
		/** INVOKESTATIC, INVOKEVIRTUAL, INVOKEINTERFACE, INVOKESPECIAL */
		InvokeInstruction md = (InvokeInstruction) insn;
		String m_methodName = null;
		int m_methodArgsSize = -1;
		MethodInfo m_mi = null;
		ClassInfo m_ci = null;

		/** Symbolic class added at runtime */
		m_mi = md.getInvokedMethod();
		if (m_mi != null)
			m_ci = m_mi.getClassInfo();
		m_methodName = md.getInvokedMethodName();
		m_methodArgsSize = md.getArgSize();

		if (!m_mi.isStatic())
			m_methodArgsSize--;

		if (m_ci != null && m_mi != null) {
			if (isSymbolic(conf, m_ci.getName(), m_mi, m_methodName,
					m_methodArgsSize))
				ConstantAnalyser.addSymbolicClass(m_mi.getClassInfo());
		}
	}
	
	private List<String> callStack = new LinkedList<String>();
	private Map<String, String> callReference = new HashMap<String, String>();

	private void handleInvokeInstruction(JVM vm, Instruction insn,
			ThreadInfo ti, SystemState ss, Config conf) {
		InvokeInstruction md = (InvokeInstruction) insn;
		String methodName = md.getInvokedMethodName();

		int numberOfArgs = -1;
		try {
			numberOfArgs = md.getArgumentValues(ti).length;
		} catch (Exception e) {
			LOGGER.error("Could not analyze " + md);
			return;
		}

		MethodInfo mi = md.getInvokedMethod();
		
		/** Method not loaded? */
		if (mi == null)
			return;
		ClassInfo ci = mi.getClassInfo();
		String className = ci.getName();
		ChoiceGenerator cg = vm.getChoiceGenerator();

		LOGGER.debug("[invoke] " + insn);

		if (isSymbolic(conf, className, mi, methodName, numberOfArgs)) {
			/** Hook for the constant methods */
			ConstantAnalyser.addSymbolicClass(ci);

			/** Other symbolic classes are added at runtime */
			addExtraSymbolicClasses(insn, conf);

			/** Preconditions hook */
			PreconditionsHook.setCallLink(ti.getMethod(), mi);

			if (ti.getStackDepth() > 1 && insn instanceof INVOKEVIRTUAL
					|| insn instanceof INVOKEINTERFACE || insn instanceof INVOKESPECIAL
					|| insn instanceof INVOKESTATIC) {
				if (!(cg instanceof PCChoiceGenerator)) {
					ChoiceGenerator<?> prev_cg = cg
							.getPreviousChoiceGenerator();
					while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
						prev_cg = prev_cg.getPreviousChoiceGenerator();
					}
					cg = prev_cg;
				}
				if ((cg instanceof PCChoiceGenerator)
						&& ((PCChoiceGenerator) cg).getCurrentPC() != null) {
					PathCondition pc = ((PCChoiceGenerator) cg).getCurrentPC();
					PreconditionsHook.addVirtualInvocation(insn, pc);
					((PCChoiceGenerator) cg).setCurrentPC(pc);
				}
			} else if (ti.getStackDepth() > 1) {
				System.err.println(insn);
				System.err.println("FIX THIS INVK !!!");
				System.exit(0);
			}
			retainVal = ss.getRetainAttributes();
			forcedVal = ss.isForced();
			ss.setForced(true);
			ss.retainAttributes(true);

			/** 
			 * If the current depth is 1 then reset the path condition.
			 * Current depth 1 means that we are in SUT.main(String[] args).
			 */
			if (Constants.NEW_PC || ti.getStackDepth() == 1) {
				if ((cg instanceof PCChoiceGenerator)
						&& ((PCChoiceGenerator) cg).getCurrentPC() != null) {
					PathCondition pc = new PathCondition();
					((PCChoiceGenerator) cg).setCurrentPC(pc);					
				}				
			}

			MethodSummary methodSummary = new MethodSummary();
			String shortName = methodName;
			String longName = mi.getFullName();
			if (methodName.contains("("))
				shortName = methodName.substring(0, methodName.indexOf("("));
			methodSummary.setClassName(className);
			methodSummary.setMethodName(shortName);
			Object[] args = md.getArgumentValues(ti);
			String argValues = "";
			for (int i = 0; i < args.length; i++) {
				argValues = argValues + args[i];
				if ((i + 1) < args.length)
					argValues = argValues + ",";
			}
			methodSummary.setArgValues(argValues);
			String[] argTypeNames = md.getInvokedMethod(ti)
					.getArgumentTypeNames();
			String argTypes = "";
			for (int j = 0; j < argTypeNames.length; j++) {
				argTypes = argTypes + argTypeNames[j];
				if ((j + 1) < argTypeNames.length)
					argTypes = argTypes + ",";
			}
			methodSummary.setArgTypes(argTypes);

			StackFrame sf = ti.getTopFrame();
			String symValues = "";
			String symVarName = "";

			String[] names = mi.getLocalVariableNames();

			int sfIndex;

			if (names == null) {
				if (Constants.REPORT_JPF_ERRORS)
					throw new RuntimeException(
							"ERROR: you need to turn debug option on");
				/** If no debug symbols available for the method, then we ignore its invocation */
				numberOfArgs = 0;
				names = new String[] {};
			}

			if (md instanceof INVOKESTATIC)
				sfIndex = 0;
			else
				sfIndex = 1;

			for (int i = 0; i < numberOfArgs; i++) {
				Expression expLocal = (Expression) sf.getLocalAttr(sfIndex);
				if (expLocal != null) {
					symVarName = expLocal.toString();					
					symValues = symValues + symVarName + ",";
				} else
					symVarName = names[sfIndex] + "_CONCRETE" + ",";

				if (argTypeNames[i].equals("double")
						|| argTypeNames[i].equals("long"))
					sfIndex = sfIndex + 2;

				else
					sfIndex++;
			}

			if (symValues.endsWith(","))
				symValues = symValues.substring(0, symValues.length() - 1);
			methodSummary.setSymValues(symValues);
			
			if (insn instanceof INVOKEVIRTUAL)
				callStack.add(((INVOKEVIRTUAL) insn).getInvokedMethod().getClassName());
			else if (insn instanceof INVOKEINTERFACE)
				callStack.add(((INVOKEINTERFACE) insn).getInvokedMethod().getClassName());
			else if (insn instanceof INVOKESPECIAL)
				callStack.add(((INVOKESPECIAL) insn).getInvokedMethod().getClassName());
			else if (insn instanceof INVOKESTATIC)
				callStack.add(((INVOKESTATIC) insn).getInvokedMethod().getClassName());
			
			if (callStack.size() > 1)
				callReference.put(callStack.get(callStack.size() - 2), callStack.get(callStack.size() - 1));
			
			InvariantsHook.hook(ci);
			methodSummary.setMethodInfo(mi);
			
			String key = mi.isPublic() + " " + longName;
			
			if (allSummaries.containsKey(key))
				methodSummary.update(allSummaries.get(key));
			allSummaries.put(key, methodSummary);
		}
	}

	private void handleReturnInstruction(JVM vm, Instruction insn,
			ThreadInfo ti, SystemState ss, Config conf) {
		LOGGER.debug("[return] " + insn);

		MethodInfo mi = insn.getMethodInfo();
		ClassInfo ci = mi.getClassInfo();

		if (null != ci) {
			String className = ci.getName();
			String methodName = mi.getName();
			String longName = mi.getFullName();
			int numberOfArgs = mi.getNumberOfArguments();

			if (isSymbolic(conf, className, mi, methodName, numberOfArgs)) {
				ss.retainAttributes(retainVal);
				ss.setForced(forcedVal);
				ChoiceGenerator<?> cg = vm.getChoiceGenerator();
				if (!(cg instanceof PCChoiceGenerator)) {
					ChoiceGenerator<?> prev_cg = cg
							.getPreviousChoiceGenerator();
					while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
						prev_cg = prev_cg.getPreviousChoiceGenerator();
					}
					cg = prev_cg;
				}
				if ((cg instanceof PCChoiceGenerator)
						&& ((PCChoiceGenerator) cg).getCurrentPC() != null) {
					PathCondition pc = ((PCChoiceGenerator) cg).getCurrentPC();
					pc.solve();
					PathCondition result = new PathCondition();

					IntegerExpression sym_result = new SymbolicInteger("RETURN");
					String pcString = pc.stringPC();
					Pair<String, String> pcPair = null;

					String instanceofCond = "";
					String returnString = "";
					
					if (insn instanceof IRETURN) {
						IRETURN ireturn = (IRETURN) insn;
						int returnValue = ireturn.getReturnValue();
						
						instanceofCond = ireturn.getMethodInfo().getClassName();						
						IntegerExpression returnAttr = (IntegerExpression) ireturn
								.getReturnAttr(ti);
						if (returnAttr != null) {
							returnString = String
									.valueOf(returnAttr.toString());
						} else
							returnString = String.valueOf(returnValue);
						if (returnAttr != null) {
							result._addDet(Comparator.EQ, sym_result,
									returnAttr);
						} else {
							result._addDet(Comparator.EQ, sym_result,
									new IntegerConstant(returnValue));
						}
					} else if (insn instanceof ARETURN) {
						ARETURN areturn = (ARETURN) insn;
						
						if (areturn.getReturnAttr(ti) == null) {

						} else if (areturn.getReturnAttr(ti) instanceof IntegerExpression) {
							IntegerExpression returnAttr = (IntegerExpression) areturn
									.getReturnAttr(ti);
							if (returnAttr != null) {
								try {
									returnString = String.valueOf(returnAttr
											.solution());
								} catch (Exception e) {
									returnString = returnAttr.toString();
								}
							} else {
								Object val = areturn.getReturnValue(ti);
								returnString = String.valueOf(val.toString());
							}
							if (returnAttr != null) {
								result._addDet(Comparator.EQ, sym_result,
										returnAttr);
							} else {
								DynamicElementInfo val = (DynamicElementInfo) areturn
										.getReturnValue(ti);
								String tmp = val.toString();
								tmp = tmp.substring(tmp.lastIndexOf('.') + 1);
								result._addDet(Comparator.EQ, sym_result,
										new SymbolicInteger(tmp));
							}
						} else if (areturn.getReturnAttr(ti) instanceof DerivedStringExpression)
							returnString = ((DerivedStringExpression) areturn
									.getReturnAttr(ti)).getName();
						else if (areturn.getReturnAttr(ti) instanceof StringSymbolic) {
							returnString = ((StringSymbolic) areturn
									.getReturnAttr(ti)).getName();
						} else {
							// Unknown instruction type
						}
					} else {
						// Nothing, because it's a simple return statement
					}

					if (callStack.size() > 0)
						callStack.remove(callStack.size() - 1);
					
					pc.solve();
					pcString = pc.toString();
					
					MethodSummary methodSummary = allSummaries.get(mi
							.isPublic()
							+ " " + longName);
					
					if (methodSummary == null) {
						
					} else {
						if (hasSymbolicArguments(conf, mi)) {
							/** We use this hack only when the current method has a symbolic parameter */
							String invokedClass = callReference.get(className);
							if (invokedClass != null) {
								if (pcString.length() > 0)
									pcString = "(and parameter instanceof " + invokedClass + " " + pcString + " )";
								else
									pcString = "(and parameter instanceof " + invokedClass + " )";
							}
						}
						
						pcPair = new Pair<String, String>(pcString, returnString);
						
						Vector<Pair> pcs = methodSummary.getPathConditions();
						if ((!pcs.contains(pcPair)))
							methodSummary.addPathCondition(pcPair);
						methodSummary.setMethodInfo(mi);
						allSummaries.put(mi.isPublic() + " " + longName, methodSummary);
						if (Constants.INSANE_DEBUG) {
							System.out.println("PC " + pc.toString());
							System.out.println("Return is  " + returnString);
						}
					}
				}
			}
		}
	}

	private boolean hasSymbolicArguments(Config conf, MethodInfo mi) {
		String[] args = mi.getArgumentTypeNames();
		for (String arg : args) {
			if (BytecodeUtils.isClassSymbolic(conf, mi.getClassName(), mi, mi.getName()))
				return true;
		}
		return false;
	}

	private void handleThrowInstruction(JVM vm, Instruction insn,
			ThreadInfo ti, SystemState ss, Config conf) {
		MethodInfo mi = insn.getMethodInfo();
		ChoiceGenerator<?> cg = vm.getChoiceGenerator();
		if (!(cg instanceof PCChoiceGenerator)) {
			ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGenerator();
			while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
				prev_cg = prev_cg.getPreviousChoiceGenerator();
			}
			cg = prev_cg;
		}
		if ((cg instanceof PCChoiceGenerator)
				&& ((PCChoiceGenerator) cg).getCurrentPC() != null) {
			PathCondition pc = ((PCChoiceGenerator) cg).getCurrentPC();
			pc.solve();
			pc.setThrowContext(Constants.THROWS_EXCEPTION);
			PreconditionsHook.hook(mi, pc);
		}
	}

	private void debugPC(JVM vm, Instruction insn) {
		MethodInfo mi = insn.getMethodInfo();
		ChoiceGenerator<?> cg = vm.getChoiceGenerator();
		if (!(cg instanceof PCChoiceGenerator)) {
			ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGenerator();
			while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
				prev_cg = prev_cg.getPreviousChoiceGenerator();
			}
			cg = prev_cg;
		}
		if ((cg instanceof PCChoiceGenerator) && ((PCChoiceGenerator) cg).getCurrentPC() != null) {
			PathCondition pc = ((PCChoiceGenerator) cg).getCurrentPC();
			pc.solve();
			System.err.println(pc);
		}
	}
	
	@Override
	public void instructionExecuted(JVM vm) {
		if (!vm.getSystemState().isIgnored()) {
			Instruction insn = vm.getLastInstruction();
			SystemState ss = vm.getSystemState();
			ThreadInfo ti = vm.getLastThreadInfo();
			Config conf = vm.getConfig();

			MethodInfo mi = insn.getMethodInfo();
			
			if (mi.getName().equals("main"))
				testName = mi.getClassInfo().getName() + "." + mi.getName();

			if (insn instanceof InvokeInstruction) {
				try {
					handleInvokeInstruction(vm, insn, ti, ss, conf);
				} catch (Exception e) {
					LOGGER.info("[LSP checker] exception while invoking " + insn);
					LOGGER.debug("    message: " + e.getMessage());
					e.printStackTrace();
					System.exit(0);
				}
			} else if (insn instanceof ReturnInstruction)
				handleReturnInstruction(vm, insn, ti, ss, conf);
			else if (insn instanceof ATHROW)
				handleThrowInstruction(vm, insn, ti, ss, conf);
		}
	}

	@Override
	public void stateBacktracked(Search search) {
		JVM vm = search.getVM();
		Config conf = vm.getConfig();
		Instruction insn = vm.getChoiceGenerator().getInsn();
		SystemState ss = vm.getSystemState();
		ThreadInfo ti = vm.getChoiceGenerator().getThreadInfo();
		MethodInfo mi = insn.getMethodInfo();
		String className = mi.getClassName();
		String methodName = mi.getName();
		int numberOfArgs = mi.getNumberOfArguments();

		if ((BytecodeUtils.isClassSymbolic(conf, className, mi, methodName))
				|| BytecodeUtils.isMethodSymbolic(conf, methodName,
						numberOfArgs, null)) {
			retainVal = ss.getRetainAttributes();
			forcedVal = ss.isForced();
			ss.setForced(true);
			ss.retainAttributes(true);
		}
	}

	@Override
	public void stateRestored(Search search) {
	}

	// Writes the method summaries to a file for use in another application
	private void writeTable(String filename) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			Iterator it = allSummaries.entrySet().iterator();
			String line = "";
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				String methodName = (String) me.getKey();
				MethodSummary ms = (MethodSummary) me.getValue();
				line = "METHOD: " + methodName + "," + ms.getMethodName() + "("
						+ ms.getArgValues() + ")," + ms.getMethodName() + "("
						+ ms.getSymValues() + ")";
				out.write(line);
				out.newLine();
				Vector<Pair> pathConditions = ms.getPathConditions();
				if (pathConditions.size() > 0) {
					Iterator it2 = pathConditions.iterator();
					while (it2.hasNext()) {
						Pair pcPair = (Pair) it2.next();
						String pc = (String) pcPair.a;
						String errorMessage = (String) pcPair.b;
						line = pc;
						if (!errorMessage.equalsIgnoreCase(""))
							line = line + "$" + errorMessage;
						out.write(line);
						out.newLine();
					}
				}
			}
			out.close();
		} catch (Exception e) {
		}
	}

	@Override
	public void searchFinished(Search search) {
		if (Constants.WRITE_TABLE)
			writeTable("LSP_checker.txt");
	}

	private void printMethodSummary(PrintWriter pw, MethodSummary methodSummary) {
		TextPublisher.publish(pw, methodSummary);
	}

	private void printMethodSummaryHTMLStart(PrintWriter pw) {
		HTMLPublisher.start(pw);
	}

	private void printMethodSummaryHTML(PrintWriter pw,
			MethodSummary methodSummary) {
		HTMLPublisher.publish(pw, methodSummary);
	}

	private void printMethodSummaryHTMLFinish(PrintWriter pw) {
		HTMLPublisher.finish(pw);
	}

	/**
	 * @brief Publishes the results in TEXT and HTML format
	 */
	@Override
	public void publishFinished(Publisher publisher) {
		PrintWriter pw;
		String testFileName = Constants.OUTPUT_FOLDER + "/" + testName + ".txt";
		String tmpFileName = Constants.TMP_FILE;

		if (testName.equals("unknown")) {
			LOGGER.fatal("!!! UNKNOWN MAIN METHOD; I DROP THIS TEST !!!");
			return;
		}

		LOGGER.info("Saving information for the test: " + testName);

		/** Finish the hook of the constant methods */
		ConstantAnalyser.finish();

		if (Constants.GENERATE_REPORTS) {
			try {
				pw = new PrintWriter(tmpFileName);
			} catch (Exception e) {
				pw = publisher.getOut();
				e.printStackTrace();
			}
		} else
			pw = publisher.getOut();

		pw.println("Generating report for the entry point " + testName);
		pw.println();

		if (!Constants.GENERATE_REPORTS)
			publisher.publishTopicStart("Method Summaries");
		Iterator it = allSummaries.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry me = (Map.Entry) it.next();
			MethodSummary methodSummary = (MethodSummary) me.getValue();
			printMethodSummary(pw, methodSummary);
		}
		TextPublisher.publishConstantMethodsHook(pw);
		pw.flush();
		pw.close();

		/** 
		 *   Aggregates data into one file: file += temporary results.
		 * Also, it destroys the temporary file to minimize the risk
		 * of interfering with other test cases 
		 */
		Constants.moveData(testFileName, Constants.TMP_FILE, true);

		if (Constants.GENERATE_HTML) {
			if (Constants.GENERATE_REPORTS) {
				try {
					pw.close();
					pw = new PrintWriter(Constants.OUTPUT_FOLDER + "/"
							+ testName + ".html");
				} catch (Exception e) {
					pw = publisher.getOut();
					e.printStackTrace();
				}
			}

			if (!Constants.GENERATE_REPORTS)
				publisher.publishTopicStart("Method Summaries (HTML)");
			it = allSummaries.entrySet().iterator();
			printMethodSummaryHTMLStart(pw);
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				String method = (String) me.getKey();

				// Do not write information about private methods
				if (method.startsWith("false"))
					continue;

				MethodSummary methodSummary = (MethodSummary) me.getValue();
				printMethodSummaryHTML(pw, methodSummary);
			}
			printMethodSummaryHTMLFinish(pw);
			pw.flush();
			pw.close();
		}
	}

	@Override
	public void propertyViolated(Search search) {
		JVM vm = search.getVM();
		Config conf = vm.getConfig();
		Instruction insn = vm.getChoiceGenerator().getInsn();
		SystemState ss = vm.getSystemState();
		ThreadInfo ti = vm.getChoiceGenerator().getThreadInfo();
		MethodInfo mi = insn.getMethodInfo();
		String className = mi.getClassName();
		String methodName = mi.getName();
		int numberOfArgs = mi.getNumberOfArguments();
		ChoiceGenerator cg = vm.getChoiceGenerator();

		if (!(cg instanceof PCChoiceGenerator)) {
			ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGenerator();
			while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
				prev_cg = prev_cg.getPreviousChoiceGenerator();
			}
			cg = prev_cg;
		}
		if ((cg instanceof PCChoiceGenerator)
				&& ((PCChoiceGenerator) cg).getCurrentPC() != null) {
			PathCondition pc = ((PCChoiceGenerator) cg).getCurrentPC();
			pc.solve();
			pc.setThrowContext(Constants.ASSERT_EXCEPTION);
			PreconditionsHook.hook(mi, pc);
		}
		stateBacktracked(search);
	}
}
