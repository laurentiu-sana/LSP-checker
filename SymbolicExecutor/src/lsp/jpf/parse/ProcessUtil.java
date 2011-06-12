package lsp.jpf.parse;


public class ProcessUtil {
	public static int execAndWait(String cmd) {
		try {
			return Runtime.getRuntime().exec(cmd).waitFor();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
