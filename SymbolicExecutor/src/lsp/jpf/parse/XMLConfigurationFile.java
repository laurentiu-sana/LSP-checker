package lsp.jpf.parse;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.StringTokenizer;

public class XMLConfigurationFile {
	private boolean fileExists = false;
	private RandomAccessFile m_file = null;
	private String m_testSuite = null;
	private StringBuffer classes;

	public XMLConfigurationFile(String fileName, String testSuite)
			throws Exception {
		/**
		 * We generate XML configuration file if it doesn't exist
		 */
		fileExists = (new File(fileName)).exists();
		if (fileExists)
			throw new Exception("File " + fileName + " already exists");

		try {
			m_file = new RandomAccessFile(fileName, "rw");
			m_file.setLength(0);
			m_testSuite = testSuite;
			classes = new StringBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addClass(String className, List<String> symbolicMethods) {
		classes.append("<class name=\"" + className + "\">");
		for (String method : symbolicMethods) {
			StringTokenizer strtok = new StringTokenizer(method, "(),");
			String methodName = strtok.nextToken();
			int params = 0;

			while (strtok.hasMoreElements()) {
				strtok.nextToken();
				params++;
			}
			classes.append("<method name=\""
					+ ConfigurationGenerator.toSymbcFormat(methodName, params)
							.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
					+ "\"/>");
		}
		classes.append("</class>");
	}

	public void flush(List<String> mainClasses) {
		try {
			m_file.writeBytes("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			m_file.writeBytes("<sut>");
			m_file.writeBytes("<testsuite name=\"" + m_testSuite + "\">");

			for (String testCase : mainClasses) {
				m_file.writeBytes("<testcase name=\"" + testCase + "\">");
				m_file.writeBytes("<classes>");
				m_file.writeBytes(classes.toString());
				m_file.writeBytes("</classes>");
				m_file.writeBytes("<mainclass name=\"" + testCase + "\"/>");
				m_file.writeBytes("</testcase>");
			}

			m_file.writeBytes("</testsuite>");
			m_file.writeBytes("</sut>");
			m_file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
