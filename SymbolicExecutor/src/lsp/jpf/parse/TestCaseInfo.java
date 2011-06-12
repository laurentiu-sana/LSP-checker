package lsp.jpf.parse;

import java.util.LinkedList;
import java.util.List;

public class TestCaseInfo {
	private String m_testName;
	private String m_mainClass;
	private List<String> m_classes;
	private List<String> m_methods;
	private List<String> m_fields;

	public TestCaseInfo(String testName, String mainClass) {
		m_testName = testName;
		m_mainClass = mainClass;
		m_classes = new LinkedList<String>();
		m_methods = new LinkedList<String>();
		m_fields = new LinkedList<String>();
	}

	public void addSymbolicClass(String className) {
		if (!m_classes.contains(className))
			m_classes.add(className);
	}

	public void addSymbolicMethod(String method) {
		// Drop initializers
		if (method.equals("put(sym#sym)")
				|| method.equals("recalculateWordsInUse()")
				|| method.equals("toString()")
				|| method.equals("toString(sym)")
				|| method.equals("removeEntryForKey(sym)")
				|| method.equals("swap(sym#sym#sym)")
				|| method.equals("vecswap(sym#sym#sym#sym)")
				|| method.equals("save(sym#sym#sym#sym)")
				|| method.equals("capacity(sym)"))
			return;

		if (method.contains("<init>") || method.contains("<clinit>")
				|| method.toLowerCase().contains("hash")
				|| method.equals("put(sym#sym)"))
			return;
		if (!m_methods.contains(method))
			m_methods.add(method);
	}

	public void addSymbolicField(String field) {
		if (!m_fields.contains(field))
			m_fields.add(field);
	}

	public List<String> getSymbolicMethods() {
		return m_methods;
	}

	public List<String> getSymbolicFields() {
		/** We ignore use symbolic fields and consider all symbolic fields */
		m_fields.clear();
		m_fields.add("instance");
		m_fields.add("static");
		return m_fields;
	}

	public List<String> getSymbolicClass() {
		return m_classes;
	}

	public String getTestName() {
		return m_testName;
	}

	public String getMainClass() {
		return m_mainClass;
	}

	@Override
	public String toString() {
		return m_testName + " [ " + m_mainClass + " ] " + " { " + m_classes
				+ ", " + m_fields + ", " + m_methods + " }";
	}

	public void setMainClass(String attribute) {
		m_mainClass = attribute;
	}
}
