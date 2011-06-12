package lsp.jpf.launchers;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lsp.jpf.parse.Constants;
import lsp.jpf.parse.TestCaseInfo;

import org.apache.log4j.Logger;

public class LSPCheckerLauncher {
	private final static Logger LOGGER = Logger
			.getLogger(LSPCheckerLauncher.class);
	private List<TestSuiteLauncher> scripts;

	public LSPCheckerLauncher() {
		scripts = new LinkedList<TestSuiteLauncher>();
	}

	public void doWork() throws Exception {
		Iterator<String> it = Constants.testSuites.keySet().iterator();
		while (it.hasNext()) {
			String testSuite = it.next();
			List<TestCaseInfo> testCases = Constants.testSuites.get(testSuite);
			scripts.add(new TestSuiteLauncher(testSuite, testCases));
		}

		for (TestSuiteLauncher tsl : scripts)
			tsl.launch();
	}

	public static void dumpResult(InputStream stdout) throws Exception {
		StringBuffer strbuf = null;
		if (!Constants.IGNORE_OUTPUT)
			strbuf = new StringBuffer();
		byte[] buffer = new byte[4096];
		int size;

		while (true) {
			size = stdout.read(buffer);
			if (size <= 0)
				break;
			if (!Constants.IGNORE_OUTPUT)
				strbuf.append(new String(buffer));
		}
		if (!Constants.IGNORE_OUTPUT)
			LOGGER.info(strbuf.toString());
	}

	public static void executeCommand(String cmd) throws Exception {
		LOGGER.info("Executing command |" + cmd + "|");
		Process proc = Runtime.getRuntime().exec(cmd);
		int result = -1;
		result = proc.waitFor();
		LOGGER.info("   rezultatul este: " + result);
	}

	public static void executeCommand(String cmd, boolean dump)
			throws Exception {
		if (dump == false)
			executeCommand(cmd);
		else {
			LOGGER.debug("Executing in debug mode: " + cmd);
			Process proc = Runtime.getRuntime().exec(cmd);
			dumpResult(proc.getInputStream());
			dumpResult(proc.getErrorStream());
			proc.waitFor();
		}
	}

	public static void main(String[] args) {
		try {
			if (args.length > 0 && !args[0].equals("${config.file}"))
				Constants.INPUT_XML_FILE = args[0];

			Constants.readConfigurationFile();

			// [DEPRECATED] 1. Delete the old SUT
			//executeCommand("rm -rf ../javapathfinder-trunk-svn/build/test/lsp/*");

			// [DEPRECATED] 2. Copy the new SUT into the JPF
			//executeCommand("cp -r build/classes/lsp ../javapathfinder-trunk-svn/build/test/");

			// Pornesc scriptul pentru dezvoltare
			(new LSPCheckerLauncher()).doWork();
			LOGGER.info("Run OK!! Check <" + Constants.OUTPUT_FOLDER
					+ "> for the results!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
