package lsp.jpf.results.analyzer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Z3Wrapper {

	public static String executeScript(String z3Script) {
		StringBuffer strbuf = new StringBuffer();
		try {
			Process z3 = Runtime.getRuntime().exec("wine smt-solver/z3/z3.exe -nw -smt -m " + z3Script);
			z3.waitFor();
			
			String line;
			BufferedReader stdout = new BufferedReader(new InputStreamReader(
					z3.getInputStream()));
			while ((line = stdout.readLine()) != null) {
				strbuf.append(line);
				strbuf.append("\n");
				System.err.println(line);
			}
			stdout.close();
			
			BufferedReader stderr = new BufferedReader(new InputStreamReader(
					z3.getErrorStream()));
			while ((line = stderr.readLine()) != null) {
				strbuf.append("[stderr] ");
				strbuf.append(line);
				strbuf.append("\n");
				System.err.println(line);
			}
			stderr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strbuf.toString();
	}

}
