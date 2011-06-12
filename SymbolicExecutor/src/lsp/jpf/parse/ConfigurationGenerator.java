package lsp.jpf.parse;

import java.io.RandomAccessFile;

public class ConfigurationGenerator {
	public final static String HEADER = "#!/bin/bash";
	public final static String JPF = "../../jpf-mercurial/jpf-core/bin/jpf \\\n";
	public final static String BASEDIR = "+jpf.basedir=../SymbolicExecutor \\\n";
	public final static String SYMBC = "+vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \\\n";
	public final static String OTHER_PARAMS = "+search.multiple_errors=true +jpf.report.console.finished= \\\n";

	public final static String SYM = "sym";
	public final static String PARAM_DELIM = "#";

	/**
	 * Converts a method with params into a string format
	 * understand by the JPF.
	 *   foo() => foo()
	 *   foo(x) => foo(sym)
	 *   foo(x, y) => foo(sym#sym)
	 */
	public static String toSymbcFormat(String method, int params) {
		if (params == 0)
			return method + "()";

		StringBuffer strbuf = new StringBuffer();
		strbuf.append(SYM);
		for (int i = 0; i < params - 1; i++)
			strbuf.append(PARAM_DELIM + SYM);
		return method + "(" + strbuf.toString() + ")";
	}

	public static void generateConfig(String filename, String myClass,
			String methodName, int params) {
		try {
			RandomAccessFile fout = new RandomAccessFile(filename, "rw");
			fout.writeBytes(HEADER);
			fout.writeBytes("\n");
			fout.writeBytes(JPF);
			fout.writeBytes("    " + BASEDIR);
			fout.writeBytes("    " + SYMBC);
			fout.writeBytes("    \'+symbolic.method="
					+ toSymbcFormat(methodName, params) + "\' \\\n");
			fout.writeBytes("    " + OTHER_PARAMS);
			fout.writeBytes("    " + myClass);
			fout.close();

			// XXX: make the script executable (not portable)
			ProcessUtil.execAndWait("chmod +x " + filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
