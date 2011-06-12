package lsp.jpf.parse;

import java.io.RandomAccessFile;
import java.util.StringTokenizer;

public class ReportsLoader {
	private static void generateJPFScript(String str) {
		/** 
		 * <lsp.test.numeric.NumericTest:
		 *    lsp.test.numeric.MyInteger[][][]
		 *          allocateArray(java.lang.Class,int,int,int)> scored 8.518
		 */
		StringTokenizer strtok = new StringTokenizer(str, " <>:");
		String aux;
		String className, methodName;
		int params = 0;

		className = strtok.nextToken();

		// Ignore return type
		strtok.nextToken();
		aux = strtok.nextToken();

		strtok = new StringTokenizer(aux, "(),");
		methodName = strtok.nextToken();
		while (strtok.hasMoreElements()) {
			strtok.nextToken();
			params++;
		}

		ConfigurationGenerator.generateConfig(Constants.SCRIPTS_FOLDER
				+ "/run_" + className + "." + methodName + ".sh", className,
				methodName, params);
	}

	public static void main(String[] args) {
		Constants.readConfigurationFile();

		try {
			String line;
			RandomAccessFile raf = new RandomAccessFile(
					Constants.INPUT_SORT_FILE, "r");
			while ((line = raf.readLine()) != null) {
				generateJPFScript(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
