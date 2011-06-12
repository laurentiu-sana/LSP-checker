package lsp.jpf.parse;

import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class XMLConfigurationGenerator {
	private String outputFile = null;
	private RandomAccessFile file = null;
	private List<String> testCases;
	private HashMap<String, List<String>> config;

	public XMLConfigurationGenerator(String fileName) {
		try {
			file = new RandomAccessFile(fileName, "r");
			outputFile = fileName.replaceAll("\\.sort$", ".xml").replaceAll(
					Constants.REPORTS_FOLDER, Constants.CONFIG_FOLDER);
			config = new HashMap<String, List<String>>();
			testCases = new LinkedList<String>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateConfiguration() {
		if (file == null)
			return;
		String line = null;
		XMLConfigurationFile configFile = null;

		/** Generate XML configuration file */
		try {
			String testSuite = outputFile.replaceAll("\\.xml", "");
			int index = testSuite.lastIndexOf('/');
			if (index >= 0)
				testSuite = testSuite.substring(index + 1);

			configFile = new XMLConfigurationFile(outputFile, testSuite);
		} catch (Exception e) {
			/** File already exists */
			return;
		}

		try {
			line = file.readLine();
			StringTokenizer strtok = new StringTokenizer(line, " ,");
			while (strtok.hasMoreTokens())
				testCases.add(strtok.nextToken());

			while ((line = file.readLine()) != null) {
				// Format: <lsp.test.accounts.TestAccount: void test(lsp.test.accounts.IAccount)> scored 3.0
				strtok = new StringTokenizer(line, " ");
				String className = strtok.nextToken();
				className = className.replaceAll("<", "").replaceAll(">", "")
						.replaceAll(":", "");

				// ignore method's type
				strtok.nextToken();

				String method = strtok.nextToken();
				if (method.endsWith(">"))
					method = method.substring(0, method.length() - 1);

				// ignore <scored>
				//strtok.nextToken();
				//double score = Double.parseDouble(strtok.nextToken());
				
				takeIntoAccount(className, method);
			}

			Iterator<String> it = config.keySet().iterator();
			while (it.hasNext()) {
				String className = it.next();
				List<String> symbolicMethods = config.get(className);
				configFile.addClass(className, symbolicMethods);
			}

			configFile.flush(testCases);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void takeIntoAccount(String className, String method) {
		List<String> symbolicMethods = null;

		if (config.containsKey(className))
			symbolicMethods = config.get(className);
		else {
			symbolicMethods = new LinkedList<String>();
			config.put(className, symbolicMethods);
		}

		symbolicMethods.add(method);
	}

	public static void main(String[] args) {
		Constants.readConfigurationFile(false);
		if (args.length == 0)
			System.err.println("Expected ${report.file}");
		else if (args[0].equals("${report.file}"))
			/** variable wasn't set in the shell */
			System.err.println("Expected ${report.file}");
		else
			(new XMLConfigurationGenerator(args[0])).generateConfiguration();
	}
}
