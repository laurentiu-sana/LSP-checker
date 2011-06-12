package lsp.jpf.results.analyzer;

import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lsp.jpf.parse.Constants;

public class TestCaseAnalyzer {
	/** Input file with aggregated data */
	private String inputFile;

	/** Output file in HTML format */
	private String outputFile;

	/** Java code relevant to the reported errors/warnings */
	private String generatedTestCases;

	/** Here we write the warnings */
	private String warnFile;
	private RandomAccessFile m_warnFile;

	/** Preconditons, postconditions and test cases per method */
	private HashMap<String, MethodResults> m_results;

	private TestCaseAnalyzer(String resultsFile) {
		m_results = new HashMap<String, MethodResults>();
		inputFile = resultsFile;
		outputFile = inputFile.replace(".main.txt", ".html");
		generatedTestCases = inputFile.replace(".main.txt", ".java");
		warnFile = inputFile.replace(".main.txt", ".warnings.txt");

		try {
			m_warnFile = new RandomAccessFile(warnFile, "rw");
			m_warnFile.setLength(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void finish() {
		try {
			System.err.println("Finished to process " + inputFile);
			m_warnFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void constantMethods(RandomAccessFile input) throws Exception {
		String line = null;
		String currentClass = null, currentMethod = null;

		while ((line = input.readLine()) != null) {
			if (line.contains("(")) {
				// Method name
				currentMethod = line;
				LSPCheckerAnalyzer.push(currentClass, currentMethod);
			} else if (line
					.startsWith("Generating report for the entry point ")
					|| line.equals(""))
				break;
			else {
				// Class name
				currentClass = line;
			}
		}
	}

	private void processList(List<String> list) {
		if (list.size() == 0)
			return;

		MethodResults result = null;
		Iterator<String> it = list.iterator();
		String line = it.next();
		String key = line.substring(line.indexOf('[') + 1,
				line.lastIndexOf(']'));
		
		if (key.contains("("))
			key = key.substring(0, key.indexOf("("));

		int index = key.lastIndexOf('.');

		String className = null;
		String methodName = null;

		if (index > 0) {
			className = key.substring(0, index);
			methodName = key.substring(index + 1);
			LSPCheckerAnalyzer.push(className, methodName);
		} else {
			className = "";
			methodName = key;
		}

		/**
		 * Check we gather info about the method If true, then use that info to
		 * aggregate data If false, create a new MethodResults and store the
		 * results right there
		 */
		if (m_results.containsKey(key))
			result = m_results.get(key);
		else {
			result = new MethodResults(className, methodName);
			m_results.put(key, result);
		}

		while (it.hasNext()) {
			line = it.next();

			if (line.contains("Symbolic return: ")) {
				index = line.indexOf("Symbolic return: ");
				line = line.substring(index + "Symbolic return: ".length());
				result.setReturnZ3Type(Constants.signatureToType(line));
			}

			if (line.contains("Symbolic values: ")) {
				index = line.indexOf("Symbolic values: ");
				line = line.substring(index + "Symbolic values: ".length());
				result.setSymVars(line);
			}

			if (line.contains(" Preconditions")) {
				while (it.hasNext()) {
					line = it.next();

					if (line.contains(".1 Preconditions from virtual methods")) {

					} else if (line.contains(". Postconditions"))
						break;
					else {
						index = line.indexOf("||||");
						String precond = line.substring(0, index);
						String effect = line.substring(index + 4);
						result.addPrecondition(precond, effect);
					}
				}
			}

			if (line.contains(" Postconditions")) {
				if (it.hasNext())
					it.next();

				while (it.hasNext()) {
					line = it.next();

					if (line.contains("Generated test cases"))
						break;

					index = line.indexOf("||||");
					if (index == -1)
						result.addPostcondition(line, "");
					else {
						String s_conditions = line.substring(0, index);
						String s_results = line.substring(index + 4);
						result.addPostcondition(s_conditions, s_results);
					}
				}
			}

			if (line.contains("Generated test cases")) {
				while (it.hasNext()) {
					line = it.next();
					index = line.indexOf(')');
					result.addTestCase(line.substring(0, index + 1));
				}
			}
		}

		list.clear();

		if (Constants.DEBUG) {
			System.err.println(className + " " + methodName);
			System.err.println(result.getPreconditions());
			System.err.println(result.getPostconditions());
			System.err.println(result.getTestCases());
			Constants.pause();
		}
	}

	public void readData() {
		try {
			RandomAccessFile input = new RandomAccessFile(inputFile, "r");
			String line = null;
			List<String> list = new LinkedList<String>();

			while ((line = input.readLine()) != null) {
				if (line.startsWith("Generating report for the entry point")) {
					while (true) {
						line = input.readLine();
						if (line.length() > 0)
							break;
					}
				}

				if (line.equals(Constants.HEADER + "Constant Methods")) {
					processList(list);
					constantMethods(input);
				} else if (line.startsWith(Constants.HEADER)) {
					processList(list);
					if (line.length() > 0)
						list.add(line);
				} else {
					if (line.length() > 0)
						list.add(line);
				}
			}

			// ConstantMethodsAnalyzer.debug();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkResults() {
		try {
			Set<String> warns = LSPCheckerAnalyzer.doAnalyze(m_results);
			for (String warn : warns) {
				m_warnFile.writeBytes("[WARNING] " + warn + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void analyze(String resultsFile) {
		LSPCheckerAnalyzer.init();

		System.err.println("Analyzing " + resultsFile);
		TestCaseAnalyzer analyzer = new TestCaseAnalyzer(resultsFile);
		Constants.DEBUG = false;
		analyzer.readData();
		Constants.DEBUG = true;

		analyzer.checkResults();
		analyzer.finish();
	}
}
