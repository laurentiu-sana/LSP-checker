package lsp.jpf.launchers;

import java.util.LinkedList;
import java.util.List;

import lsp.jpf.parse.TestCaseInfo;

public class TestSuiteLauncher {
	private List<TestCaseLauncher> testCases;

	public TestSuiteLauncher(String name, List<TestCaseInfo> testCases) {
		this.testCases = new LinkedList<TestCaseLauncher>();
		for (TestCaseInfo tci : testCases)
			this.testCases.add(new TestCaseLauncher(name, tci));
	}

	public void launch() {
		for (TestCaseLauncher tcl : testCases)
			tcl.launch();
	}
}
