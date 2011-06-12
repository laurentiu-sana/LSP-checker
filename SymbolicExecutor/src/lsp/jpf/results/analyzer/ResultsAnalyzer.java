package lsp.jpf.results.analyzer;

import java.io.File;

import lsp.jpf.parse.Constants;

public class ResultsAnalyzer {
	public static void main(String[] args) {
		Constants.readConfigurationFile(false);

		// Constant methods analyzer => warnings
		File resultsFolder = new File(Constants.OUTPUT_FOLDER);
		for (File resultFile : resultsFolder.listFiles()) {
			String resultFileName = resultFile.getName();
			if (resultFileName.equals("..") || resultFileName.equals(".")
					|| resultFileName.equals(".svn")
					|| !resultFileName.endsWith(".main.txt"))
				continue;
			//if (!resultFileName.contains("JDK"))
			//	continue;
			TestCaseAnalyzer.analyze(resultFile.getAbsolutePath());
		}
	}
}
