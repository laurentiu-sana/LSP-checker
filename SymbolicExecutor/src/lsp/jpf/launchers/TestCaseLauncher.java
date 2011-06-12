package lsp.jpf.launchers;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import lsp.jpf.parse.Constants;
import lsp.jpf.parse.TestCaseInfo;

public class TestCaseLauncher {
	private final static Logger LOGGER = Logger
			.getLogger(TestCaseLauncher.class);
	private List<String> scripts;
	private String basePath;
	private TestCaseInfo tci;

	public TestCaseLauncher(String testSuite, TestCaseInfo tci) {
		this.tci = tci;
		this.basePath = Constants.SCRIPTS_FOLDER + "/" + testSuite;
		prepare();
	}

	private String normalize(List<?> list) {
		StringBuffer strbuf = new StringBuffer();
		int size = list.size();

		for (Object o : list) {
			strbuf.append(o.toString());
			size--;
			if (size > 0)
				strbuf.append(",");
		}

		return strbuf.toString();
	}

	private void prepare() {
		try {
			Constants.createFolders(basePath);
			scripts = new LinkedList<String>();
			if (Constants.GENERATE_PER_METHODS)
				generateScripts();
			else
				generateScript();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateScript() throws Exception {
		String script = basePath + "/" + tci.getTestName() + ".sh";
		scripts.add(script);
		if ((new File(script)).exists())
			LOGGER.debug("File " + script + " already exists");
		else {
			generateScriptFile(script, tci.getMainClass(), normalize(tci
					.getSymbolicClass()), normalize(tci.getSymbolicMethods()),
					normalize(tci.getSymbolicFields()), "");
			LSPCheckerLauncher.executeCommand("chmod +x " + script);
		}
	}

	private void generateScripts() throws Exception {
		for (String method : tci.getSymbolicMethods()) {
			/** method(sym#sym) => method */
			int index = method.indexOf('(');
			if (index >= 0)
				method = method.substring(0, index);

			String script = basePath + "/" + tci.getTestName() + "." + method
					+ ".sh";
			scripts.add(script);

			if ((new File(script)).exists())
				LOGGER.debug("File " + script + " already exists");
			else {
				generateScriptFile(script, tci.getMainClass(), normalize(tci
						.getSymbolicClass()), normalize(tci
						.getSymbolicMethods()), normalize(tci
						.getSymbolicFields()), method);
				LSPCheckerLauncher.executeCommand("chmod +x " + script);
			}
		}
	}

	private void generateScriptFile(String scriptName, String mainClassName,
			String sClasses, String sMethods, String sFields,
			String mainClassParams) throws Exception {
		RandomAccessFile raf = new RandomAccessFile(scriptName, "rw");
		raf.setLength(0);
		raf.writeBytes("#!/bin/bash\n");
		raf.writeBytes("\n");
		raf.writeBytes("../../jpf-mercurial/jpf-symbc/bin/jpf \\\n");
		raf.writeBytes("    +jpf.basedir=../SymbolicExecutor \\\n");
		raf
				.writeBytes("    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \\\n");
		raf.writeBytes("    +listener=" + Constants.JPF_LISTENER + " \\\n");
		raf.writeBytes("    \'+symbolic.class=" + sClasses + "\' \\\n");
		raf.writeBytes("    \'+symbolic.method=" + sMethods + "\' \\\n");
		raf.writeBytes("    \'+symbolic.fields=" + sFields + "\' \\\n");
		raf
				.writeBytes("    +search.multiple_errors=true +jpf.report.console.finished= \\\n");
		raf.writeBytes("    " + mainClassName + " " + mainClassParams);
		raf.writeBytes("\n");
		raf.close();
	}

	public void launch() {
		try {
			for (String script : scripts)
				LSPCheckerLauncher.executeCommand("./" + script,
						Constants.DUMP_OUTPUT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
