package lsp.jpf.results.analyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/** Intermediate representation for the results of a method */
public class MethodResults {
	private String className;
	private String methodName;

	/** Conditions for assert failure or throw new Exception */
	private Map<String, String> preconditions;

	/** Conditions of the input and information about the results */
	private Map<String, String> postconditions;

	/** Test cases generated by the SymbolicExecutor, using the JPF */
	private Set<String> testCases;

	/** Maps of variable name and type */
	private Map<String, String> symVars;

	private String returnType;

	public MethodResults(String className, String methodName) {
		this.className = className;
		this.methodName = methodName;
		this.symVars = new HashMap<String, String>();
		this.returnType = null;

		preconditions = new HashMap<String, String>();
		postconditions = new HashMap<String, String>();
		testCases = new HashSet<String>();
	}

	public void addPrecondition(String precond, String effect) {
		boolean add = true;

		/** Hack for nested preconditions */
		for (String my_precond : preconditions.keySet()) {
			my_precond = my_precond.substring(1, my_precond.length() - 1);
			if (precond.replaceAll(" ", "").contains(my_precond.replaceAll(" ", ""))) {
				add = false;
				break;
			}
		}
		
		if (add) {
			/** Hack #2
			 * Sometimes it creates two preconditions
			 *  "(and  ( < input.member 0 ))"
			 *  "(and  ( < x 0 ))"
			 * which are the same
			 */
			if (precond.contains(" x ")) {
				precond = precond.replaceAll(" x ", " input.member ");
				if (preconditions.containsKey(precond))
					add = false;
			}
		}

		if (add)
			preconditions.put(precond, effect);
	}

	public void addPostcondition(String postcond, String result) {
		postconditions.put(postcond, result);
	}

	public void addTestCase(String testCase) {
		testCases.add(testCase);
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName.replace("(", "_").replace(")", "");
	}

	public Map<String, String> getPreconditions() {
		return preconditions;
	}

	public Map<String, String> getPostconditions() {
		return postconditions;
	}

	public Set<String> getTestCases() {
		return testCases;
	}

	public void setSymVars(String line) {
		StringTokenizer strtok = new StringTokenizer(line, ",");

		while (strtok.hasMoreTokens()) {
			String token = strtok.nextToken();

			String type = null;

			if (token.endsWith("_SYMINT")) {
				type = "Int";
				token = token.substring(0, token.indexOf("_SYMINT"));
			} else if (token.endsWith("_SYMREAL")) {
				type = "Real";
				token = token.substring(0, token.indexOf("_SYMREAL"));
			} else if (token.contains(".")) {
				/** Assume that the field is integer */
				type = "Int";
				token = token.substring(token.lastIndexOf(' ') + 1).trim();
				symVars.put(token, type);
				continue;
			} else {
				/** By default, the type is reference */
				type = "Ref";
				token = token.substring(0, token.lastIndexOf("_"));
			}

			/** Remove the variable counter */
			token = token.substring(0, token.lastIndexOf("_"));
			symVars.put(token, type);
		}
	}

	public Map<String, String> getSymVars() {
		return symVars;
	}

	public void setReturnZ3Type(String type) {
		returnType = type;
	}
	
	public String getReturnZ3Type() {
		return returnType;
	}
}