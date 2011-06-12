package lsp.jpf.listeners;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.util.Pair;

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
public class HTMLPublisher {
	private static int counter = 0;
	private static HashSet<String> methodsInfo = new HashSet<String>();

	public static void publish(PrintWriter pw, MethodSummary methodSummary) {
		pw.println("<h3>" + ++counter + ". Method summary <b>"
				+ methodSummary.getMethodFullName() + "["
				+ methodSummary.getArgTypes() + "]" + "</b></h3>");
		pw.println("<p>"
				+ counter
				+ ".1. Symbolic values: <b>"
				+ UtilsPublisher
						.refactorSymValues(methodSummary.getSymValues())
				+ "</b></p>");

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
		pw.println("<p>" + counter + ".2.1 Preconditions from virtual methods"
				+ "</p>");
		pw.println("<table border=1>");
		generateTableHeader(pw, new String[] { "Input", "Output" });
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
								getSymbolicReference(pmi) + " instanceof "
										+ pmi.getClassInfo().getName() + " && ");
				}
			}
		}
		pw.println("</table>");
		pw.println("<BR/>");
	}

	private static String getSymbolicReference(MethodInfo pmi) {
		return "obj";
	}

	private static void publishPreconditionsHook(PrintWriter pw,
			String methodSummary, boolean printHeader,
			String instanceofCondition) {
		boolean result = false;
		if (printHeader)
			pw.println("<p>" + counter + ".2. Preconditions" + "</p>");
		/** Any preconditions? */
		if (PreconditionsHook.getInstance() == null)
			return;

		if (printHeader) {
			pw.println("<table border=1>");
			generateTableHeader(pw, new String[] { "Input", "Output" });
		}

		/** Those are the raw preconditions; we process them to obtain full overview of the system */
		HashMap<ClassInfo, HashMap<MethodInfo, List<PathCondition>>> exceptionsPC = PreconditionsHook
				.getInstance().getExceptionsPC();
		HashMap<MethodInfo, HashSet<MethodInfo>> inheritedPreconds = PreconditionsHook
				.getInstance().getCalledMethods();

		pw.println();

		Iterator<ClassInfo> it1 = exceptionsPC.keySet().iterator();

		while (it1.hasNext()) {
			ClassInfo ci = it1.next();
			HashMap<MethodInfo, List<PathCondition>> methods = exceptionsPC
					.get(ci);
			Iterator<MethodInfo> it2 = methods.keySet().iterator();

			while (it2.hasNext()) {
				MethodInfo mi = it2.next();
				if (mi.getFullName().equals(methodSummary))
					result |= writePreconditions(exceptionsPC,
							inheritedPreconds, methodSummary, ci, mi, pw, true,
							result, "", instanceofCondition);
			}
		}
		if (printHeader) {
			if (!result)
				pw.println("<tr><td>N/A</td><td>N/A</td></tr>");
			pw.println("</table>");
			pw.println("<BR/>");
		}
	}

	private static HashSet<String> writtenPreconditions = new HashSet<String>();

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

		if (firstTime && (!mi.getFullName().equals(methodSummary)))
			return result;

		List<PathCondition> pcs = methods.get(mi);
		if (pcs != null) {
			for (PathCondition pc : pcs) {
				pc.solve();

				String str = instanceofCondition
						+ UtilsPublisher.refactorString(pc.toString()) + "||||"
						+ pc.getThrowContext() + context;
				if (!writtenPreconditions.contains(str)) {
					writtenPreconditions.add(str);
					pw.println("<tr>");
					pw.println("<td>" + instanceofCondition
							+ UtilsPublisher.refactorString(pc.toString())
							+ "</td>");
					pw.println("<td>" + pc.getThrowContext() + context
							+ "</td>");
					pw.println("</tr>");
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
		pw.println("<p>" + counter + ".3. Postconditions" + "</p>");
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
				pathCondition = "<td>" + UtilsPublisher.refactorString(pc)
						+ "</td>";

				if (!errorMessage.equalsIgnoreCase(""))
					pathCondition += "<td>"
							+ UtilsPublisher.refactorString(errorMessage)
							+ "</td>";
				allPathConditions.append("<tr>" + pathCondition + "</tr>");

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

				/** Add new test case*/
				if (testCase.endsWith(","))
					testCase = testCase.substring(0, testCase.length() - 1);
				testCase = testCase + ")";
				testCase = "<tr><td>" + testCase + "</td><td>"
						+ UtilsPublisher.refactorString(errorMessage)
						+ "</td></tr>";
				if (!allTestCases.contains(testCase)) {
					allTestCases += "<tr>" + testCase + "</tr>";
				}
			}
			pw.println("<p>" + counter + ".3.1 Path conditions</p>");
			pw.println("<table border=\"1\">");
			generateTableHeader(pw, new String[] { "Input", "Output" });

			if (allPathConditions.length() == 0)
				pw.println("<tr><td>N/A</td><td>N/A</td></tr>");
			else
				pw.println(allPathConditions.toString());
			pw.println("</table>");

			pw.println("<p>" + counter + ".3.2 Generated test cases</p>");
			pw.println("<table border=\"1\">");
			generateTableHeader(pw, new String[] { "Input", "Output" });
			if (allTestCases.length() == 0)
				pw.println("<tr><td>N/A</td><td>N/A</td></tr>");
			else
				pw.println(allTestCases);
			pw.println("</table>");
			pw.println();
		}
	}

	public static void start(PrintWriter pw) {
		pw.println("<html>");
		pw.println("<head>");
		pw.println("<title>" + Constants.PROJECT_NAME + "</title>");
		pw.println("</head>");
		pw.println("<body>");
	}

	private static void generateTableHeader(PrintWriter pw, String[] headers) {
		pw.println("<thead>");
		pw.println("<tr>");
		for (String header : headers)
			pw.println("<th>" + header + "</th>");
		pw.println("</tr>");
		pw.println("</thead>");
	}

	public static void publishConstantMethodsHook(PrintWriter pw) {
		pw.println("<b>Constant Methods</b>");
		List<ClassInfo> ctClasses = ConstantAnalyser.getSymbolicClasses();
		pw.println("<table border=\"1\">");
		generateTableHeader(pw, new String[] { "Class", "Method" });
		for (ClassInfo ci : ctClasses) {
			pw.println("<tr>");
			List<ConstantMethod> ctMethods = ConstantAnalyser
					.getConstantMethods(ci);
			pw.println("<td>" + ci.getName() + "</td>");
			pw.println("<td><table border=\"1\">");
			if (ctMethods == null)
				pw.println("<tr><td>NULL</td></tr>");
			else {
				for (ConstantMethod cm : ctMethods) {
					if (cm.isInvariantSubset())
						pw.println("<tr><td>" + cm + "</td></tr>");
				}
			}
			pw.println("</table>");
			pw.println("</tr>");
		}
		pw.println("<table></td>");
		pw.println();

	}

	public static void finish(PrintWriter pw) {
		publishConstantMethodsHook(pw);
		pw.println("</body>");
		pw.println("</html>");
	}

	/** Deprecated */
	public static void publishInvariantsHook(PrintWriter pw) {
		HashMap<ClassInfo, LinkedList<MethodInfo>> map = (HashMap<ClassInfo, LinkedList<MethodInfo>>) InvariantsHook
				.getInstance().getNoSideEffects().clone();

		pw.println("<p><b>Invariants</b></p>");

		if (InvariantsHook.getInstance() == null)
			return;

		pw.println("<table border=1>");
		while (map.size() > 0) {
			ClassInfo ci = map.keySet().iterator().next();
			while (true) {
				if (map.containsKey(ci.getSuperClass()))
					ci = ci.getSuperClass();
				else {
					LinkedList<MethodInfo> methods = map.get(ci);
					for (MethodInfo method : methods)
						pw.println("<tr><td>" + method.getBaseName()
								+ "</td></tr>");
					map.remove(ci);
					break;
				}
			}
		}
		pw.println("</table>");
	}
}
