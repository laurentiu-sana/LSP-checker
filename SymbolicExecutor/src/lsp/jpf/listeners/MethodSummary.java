package lsp.jpf.listeners;

import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.util.Pair;

import java.util.Vector;

@SuppressWarnings("unchecked")
public class MethodSummary {
	private MethodInfo mi;
	private String className = "";
	private String methodName = "";
	private String argTypes = "";
	private String argValues = "";
	private String symValues = "";
	private Vector<Pair> pathConditions;

	public MethodSummary() {
		pathConditions = new Vector<Pair>();
	}

	public void setClassName(String cName) {
		this.className = cName;
	}
	
	public String getClassName() {
		return this.className;
	}

	public void setMethodName(String mName) {
		this.methodName = mName;
	}

	public String getMethodName() {
		return this.methodName;
	}
	
	public String getMethodFullName() {
		return this.className + "." + this.methodName;
	}

	public void setArgTypes(String args) {
		this.argTypes = args;
	}

	public String getArgTypes() {
		return this.argTypes;
	}

	public void setArgValues(String vals) {
		this.argValues = vals;
	}

	public String getArgValues() {
		return this.argValues;
	}

	public void setSymValues(String sym) {
		this.symValues = sym;
	}

	public String getSymValues() {
		return this.symValues;
	}

	public void addPathCondition(Pair pc) {
		pathConditions.add(pc);
	}

	public Vector<Pair> getPathConditions() {
		return this.pathConditions;
	}
	
	public void setMethodInfo(MethodInfo mi) {
		this.mi = mi;
	}
	
	public MethodInfo getMethodInfo() {
		return mi;
	}

	public void update(MethodSummary methodSummary) {
		/** Just take the old path conditions */
		Vector<Pair> pathConditions = methodSummary.getPathConditions();
		for (Pair pair : pathConditions) {
			if (!this.pathConditions.contains(pair))
				this.pathConditions.add(pair);
		}
	}
}
