package lsp.jpf.listeners;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.util.Pair;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import lsp.jpf.analyzer.ConstantAnalyser;
import lsp.jpf.analyzer.ConstantMethod;
import lsp.jpf.parse.Constants;

@SuppressWarnings("unchecked")
public class TextPublisher {
	private static int counter = 0;
	private static HashSet<String> methodsInfo = new HashSet<String>();
	private static HashSet<String> writtenPreconditions = new HashSet<String>();

	public static void publish(PrintWriter pw, MethodSummary methodSummary) {
		pw.println(Constants.HEADER + ++counter + ". Method summary ["
				+ methodSummary.getMethodFullName() + "("
				+ methodSummary.getArgTypes() + ")" + "]");
		pw.println(counter + ".0. Symbolic return: "
				+ methodSummary.getMethodInfo().getReturnType());
		pw.println(counter + ".1. Symbolic values: "
				+ methodSummary.getSymValues());

		methodsInfo.add(methodSummary.getMethodFullName());
		publishPreconditionsHook(pw, methodSummary.getMethodFullName(), true,
				"");
		publishVirtualPreconditions(
				methodSummary.getMethodInfo().getFullName(), pw);
		publishPostconditionsHook(pw, methodSummary);
	}

	private static void publishVirtualPreconditions(String method,
			PrintWriter pw) {
		HashMap<MethodInfo, HashMap<PathCondition, HashSet<MethodInfo>>> map = PreconditionsHook
				.getInstance().getVirtualCalledMethods();
		writtenPreconditions.clear();
		Iterator<MethodInfo> it = map.keySet().iterator();
		pw.println(counter + ".2.1 Preconditions from virtual methods");
		while (it.hasNext()) {
			MethodInfo mi = it.next();
			if (mi.getFullName().equals(method)) {
				HashMap<PathCondition, HashSet<MethodInfo>> info = map.get(mi);
				Iterator<PathCondition> itP = info.keySet().iterator();
				while (itP.hasNext()) {
					PathCondition pc = itP.next();
					HashSet<MethodInfo> possibleMethods = info.get(pc);
					for (MethodInfo pmi : possibleMethods)
						publishPreconditionsHook(pw, pmi.getFullName(), false,
								getSymbolicReference(pmi)
										+ "_instanceof_"
										+ pmi.getClassInfo().getName()
												.replaceAll("\\.", "_"));
				}
			}
		}
	}

	private static String getSymbolicReference(MethodInfo pmi) {
		return "obj";
	}

	private static void publishPreconditionsHook(PrintWriter pw,
			String methodSummary, boolean printHeader,
			String instanceofCondition) {
		boolean result = false;
		if (printHeader)
			pw.println(counter + ".2. Preconditions");
		/** Any preconditions? */
		if (PreconditionsHook.getInstance() == null)
			return;

		/**
		 * Those are the raw preconditions; we process them to obtain full
		 * overview of the system
		 */
		HashMap<ClassInfo, HashMap<MethodInfo, List<PathCondition>>> exceptionsPC = PreconditionsHook
				.getInstance().getExceptionsPC();
		HashMap<MethodInfo, HashSet<MethodInfo>> inheritedPreconds = PreconditionsHook
				.getInstance().getCalledMethods();

		Iterator<ClassInfo> it1 = exceptionsPC.keySet().iterator();

		while (it1.hasNext()) {
			ClassInfo ci = it1.next();
			HashMap<MethodInfo, List<PathCondition>> methods = exceptionsPC
					.get(ci);
			Iterator<MethodInfo> it2 = methods.keySet().iterator();

			while (it2.hasNext()) {
				MethodInfo mi = it2.next();
				if (mi.getFullName().startsWith(methodSummary))
					result |= writePreconditions(exceptionsPC,
							inheritedPreconds, methodSummary, ci, mi, pw, true,
							result, "", instanceofCondition);
			}
		}
	}

	private static boolean writePreconditions(
			HashMap<ClassInfo, HashMap<MethodInfo, List<PathCondition>>> exceptionsPC,
			HashMap<MethodInfo, HashSet<MethodInfo>> inheritedPreconds,
			String methodSummary, ClassInfo ci, MethodInfo mi, PrintWriter pw,
			boolean firstTime, boolean superResult, String context,
			String instanceofCondition) {
		boolean result = superResult;
		HashMap<MethodInfo, List<PathCondition>> methods = exceptionsPC.get(ci);

		if (methods == null)
			return result;

		if (firstTime && (!mi.getFullName().startsWith(methodSummary)))
			return result;

		List<PathCondition> pcs = methods.get(mi);
		if (pcs != null) {
			for (PathCondition pc : pcs) {
				pc.solve();

				String str = "(and " + instanceofCondition + " "
						+ UtilsPublisher.refactorString(pc.toString()) + ")" + "||||"
						+ pc.getThrowContext() + context;
				if (!writtenPreconditions.contains(str)) {
					writtenPreconditions.add(str);
					pw.println(str);
				}
				result = true;
			}
		}

		HashSet<MethodInfo> preconds = inheritedPreconds.get(mi);
		if (preconds == null)
			return result;
		for (MethodInfo cmi : preconds)
			result |= writePreconditions(exceptionsPC, inheritedPreconds,
					methodSummary, cmi.getClassInfo(), cmi, pw, false, result,
					"[inherited]", instanceofCondition);
		return result;
	}

	private static void publishPostconditionsHook(PrintWriter pw,
			MethodSummary methodSummary) {
		pw.println(counter + ".3. Postconditions");
		Vector<Pair> pathConditions = methodSummary.getPathConditions();
		if (pathConditions.size() > 0) {
			Iterator it = pathConditions.iterator();
			String allTestCases = "";
			StringBuffer allPathConditions = new StringBuffer();
			
			while (it.hasNext()) {
				String testCase = methodSummary.getMethodName() + "(";
				Pair pcPair = (Pair) it.next();
				String pc = (String) pcPair.a;
				String errorMessage = (String) pcPair.b;
				String symValues = methodSummary.getSymValues();
				String argValues = methodSummary.getArgValues();
				String argTypes = methodSummary.getArgTypes();
				String pathCondition = null;

				StringTokenizer st = new StringTokenizer(symValues, ",");
				StringTokenizer st2 = new StringTokenizer(argValues, ",");
				StringTokenizer st3 = new StringTokenizer(argTypes, ",");

				/** Add path conditions + result */
				pathCondition = UtilsPublisher.refactorString(pc);

				if (!errorMessage.equalsIgnoreCase(""))
					pathCondition += "||||"
							+ UtilsPublisher.refactorString(errorMessage);
				
				if (allPathConditions.indexOf(pathCondition + "\n") == -1) {
					allPathConditions.append(pathCondition);
					allPathConditions.append("\n");
				}

				try {
					while (st2.hasMoreTokens()) {
						String token = "";
						String actualValue = st2.nextToken();
						String actualType = st3.nextToken();
						if (st.hasMoreTokens())
							token = st.nextToken();
						if (pc.contains(token)) {
							String temp = pc.substring(pc.indexOf(token));
							String val = temp.substring(temp.indexOf("[") + 1,
									temp.indexOf("]"));
							if (actualType.equalsIgnoreCase("int")
									|| actualType.equalsIgnoreCase("float")
									|| actualType.equalsIgnoreCase("long")
									|| actualType.equalsIgnoreCase("double"))
								testCase = testCase + val + ",";
							else {
								if (val.equalsIgnoreCase("0"))
									testCase = testCase + "false" + ",";
								else
									testCase = testCase + "true" + ",";
							}
						} else {
							if (token.contains("CONCRETE"))
								testCase += actualValue + ",";
							else
								testCase += "don't care,";
						}
					}
				} catch (Exception e) {

				}

				/** Add new test case */
				if (testCase.endsWith(","))
					testCase = testCase.substring(0, testCase.length() - 1);
				testCase = testCase + ")";
				testCase = testCase + " "
						+ UtilsPublisher.refactorString(errorMessage) + "\n";
				if (!allTestCases.contains(testCase))
					allTestCases += testCase + "\n";
			}
			pw.println(counter + ".3.1 Path conditions");

			if (allPathConditions.length() == 0)
				pw.println("");
			else
				pw.println(allPathConditions.toString());

			pw.println(counter + ".3.2 Generated test cases");
			if (allTestCases.length() == 0)
				pw.println("N/A N/A");
			else
				pw.println(allTestCases);
		}
	}

	public static void publishConstantMethodsHook(PrintWriter pw) {
		pw.println(Constants.HEADER + "Constant Methods");
		List<ClassInfo> ctClasses = ConstantAnalyser.getSymbolicClasses();
		for (ClassInfo ci : ctClasses) {
			List<ConstantMethod> ctMethods = ConstantAnalyser
					.getConstantMethods(ci);
			pw.println(ci.getName());
			if (ctMethods == null)
				pw.println("NULL");
			else {
				for (ConstantMethod cm : ctMethods) {
					if (cm.isInvariantSubset())
						pw.println(cm);
				}
			}
		}
	}

	/**
	 * Deprecated
	 */
	public static void publishInvariantsHook(PrintWriter pw) {
		pw.println();
		pw.println("<invariants>");
		/** Any invariants? */
		if (InvariantsHook.getInstance() == null) {
			pw.println("<invariants>");
			return;
		}

		HashMap<ClassInfo, LinkedList<MethodInfo>> map = (HashMap<ClassInfo, LinkedList<MethodInfo>>) InvariantsHook
				.getInstance().getNoSideEffects().clone();
		while (map.size() > 0) {
			ClassInfo ci = map.keySet().iterator().next();
			while (true) {
				if (map.containsKey(ci.getSuperClass()))
					ci = ci.getSuperClass();
				else {
					pw.println("Class: " + ci.getName());
					LinkedList<MethodInfo> methods = map.get(ci);
					for (MethodInfo method : methods)
						pw.println("    " + method.getBaseName());
					map.remove(ci);
					break;
				}
			}
		}
		pw.println("</invariants>");
	}
}
