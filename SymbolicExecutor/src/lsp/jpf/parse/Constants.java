package lsp.jpf.parse;

import gov.nasa.jpf.Config;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lsp.jpf.results.analyzer.MethodResults;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.ShortType;
import soot.SootClass;
import soot.SootMethod;
import soot.JastAddJ.PreIncExpr;

public class Constants {
	private static Logger LOGGER = null;

	public static final String HEADER = "###### ";

	private final static String TESTSUITE_STR = "testsuite";
	private final static String TESTCASE_STR = "testcase";
	private final static String NAME_STR = "name";
	private final static String CLASSES_STR = "classes";
	private final static String METHOD_STR = "method";
	private final static String FIELD_STR = "field";
	private final static String MAINCLASS_STR = "mainclass";

	public static String REPORTS_FOLDER = "reports";
	public static String CONFIG_FOLDER = "config";

	public final static String PROJECT_NAME = "LSP Checker";
	public final static String CONFIG_FILE = "lsp.properties";
	public static final boolean NEW_PC = false;
	public static final boolean INSANE_DEBUG = false;
	public static Boolean DUMP_OUTPUT = true;
	public static Boolean IGNORE_OUTPUT = true;
	public static Boolean DEBUG = true;
	public static Boolean GENERATE_REPORTS = true;

	public static String INPUT_SORT_FILE = null;
	public static String INPUT_XML_FILE = null;

	public static String OUTPUT_FOLDER = "results";
	public static String SCRIPTS_FOLDER = "generated_scripts";
	public static Integer TIMEOUT = 0;

	public static HashMap<String, List<TestCaseInfo>> testSuites;

	public static String JPF_LISTENER = "gov.nasa.jpf.symbc.SymbolicListener";

	public static Config GLOBAL_CONFIGURATION = null;

	public static final boolean TEST_INVARIANTS = false;
	public static final boolean TEST_PRECONDITIONS = true;

	/** JPF has some problems (e.g. no equals for ClassInfo etc.) */
	public static final boolean FIXED_JPF = false;
	public static final int MAX_DEPTH = 128;
	public static final boolean WRITE_TABLE = false;
	private static final Integer DEFAULT_TIMEOUT = 90;
	public static final boolean GENERATE_PER_METHODS = false;

	public static final String THROWS_EXCEPTION = "Throws unhandled exception";
	public static final String ASSERT_EXCEPTION = "Assert failed";

	public static final boolean REPORT_JPF_ERRORS = false;

	public static final boolean GENERATE_HTML = false;

	public static final String TMP_FILE = "/tmp/lsp_checker_output";

	public static final String JAVA_LANG = "java.lang.";
	public static final String JAVA_UTIL = "java.util.";

	/** XXX: Change this to true if reflection is used in the SUT */
	public static boolean ALLOW_CLASS_LOAD = false;

	/** FIXME: set to TRUE after finishing the development */
	public static boolean ENABLE_CONSTANT_METHODS_PHASE = true;

	/** Z3 configuration */
	private final static String Z3_FOLDER = "smt-solver";

	/** How to launch Z3, relative to Z3_FOLDER */
	private final static String Z3_LAUNCHER = "z3.sh";

	/** Where to store the generated z3 formulas, relative to Z3_FOLDER */
	private final static String Z3_TESTCASES_FOLDER = "lsp-checker";

	public static void readConfigurationFile() {
		readConfigurationFile(true);
	}

	public static void readConfigurationFile(boolean loadAll) {
		LOGGER = Logger.getLogger(Constants.class);
		try {
			RandomAccessFile raf = new RandomAccessFile(Constants.CONFIG_FILE,
					"rw");
			String line;

			while ((line = raf.readLine()) != null) {
				// Ignore comments
				if (line.startsWith("#"))
					continue;
				int index = line.indexOf('=');

				if (index < 0)
					continue;

				String key, value;
				key = line.substring(0, index);
				value = line.substring(index + 1);

				if (key.equals("DEBUG"))
					DEBUG = value.equals("false") ? false : true;
				else if (key.equals("TIMEOUT")) {
					try {
						TIMEOUT = Integer.parseInt(value);
					} catch (Exception e) {
						/** Set the timeout to a default value */
						TIMEOUT = DEFAULT_TIMEOUT;
					}
				} else if (key.equals("INPUT_SORT_FILE"))
					INPUT_SORT_FILE = value;
				else if (key.equals("INPUT_XML_FILE")) {
					if (INPUT_XML_FILE == null)
						INPUT_XML_FILE = value;
				} else if (key.equals("SCRIPTS_FOLDER")) {
					SCRIPTS_FOLDER = value;
					createFolders(SCRIPTS_FOLDER);
				} else if (key.equals("OUTPUT_FOLDER")) {
					OUTPUT_FOLDER = value;
					createFolders(OUTPUT_FOLDER);
				} else if (key.equals("JPF_LISTENER"))
					JPF_LISTENER = value;
				else if (key.equals("REPORTS_FOLDER"))
					REPORTS_FOLDER = value;
				else if (key.equals("CONFIG_FOLDER"))
					CONFIG_FOLDER = value;
			}
			raf.close();
			if (loadAll) {
				loadSortedMethods();
				loadSUTConfiguration();
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public static void createFolders(String folder) {
		try {
			File output = new File(folder);
			if (!output.exists())
				output.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadSortedMethods() {
		/** Not interested at the moment */
	}

	/** Loads the SUT (system under test) configuration file */
	private static void loadSUTConfiguration() throws Exception {
		File file = new File(INPUT_XML_FILE);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();

		testSuites = new HashMap<String, List<TestCaseInfo>>();
		Element root = doc.getDocumentElement();
		NodeList list = root.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node instanceof Element) {
				Element element = (Element) node;
				if (element.getTagName().equals(TESTSUITE_STR))
					loadTestSuite(element);
			}
		}
		if (DEBUG)
			debug();
	}

	private static void loadTestSuite(Element root) {
		LOGGER.debug("[loadTestSuite] Root " + root);
		List<TestCaseInfo> testSuite = new LinkedList<TestCaseInfo>();
		String testSuiteName = root.getAttribute(NAME_STR);
		NodeList list = root.getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node instanceof Element) {
				Element element = (Element) node;
				if (element.getTagName().equals(TESTCASE_STR))
					testSuite.add(loadTestCase(element.getAttribute(NAME_STR),
							element));
			}
		}

		testSuites.put(testSuiteName, testSuite);
	}

	private static TestCaseInfo loadTestCase(String testName, Element root) {
		LOGGER.debug("[loadTestCase] test name " + testName + ", root " + root);
		TestCaseInfo tci = new TestCaseInfo(testName, null);

		NodeList list = root.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node instanceof Element) {
				Element element = (Element) node;
				if (element.getTagName().equals(CLASSES_STR))
					parseClasses(tci, element);
				else if (element.getTagName().equals(MAINCLASS_STR))
					tci.setMainClass(element.getAttribute(NAME_STR));
			}
		}

		return tci;
	}

	private static void parseClass(TestCaseInfo tci, Element element) {
		NodeList members = element.getChildNodes();
		tci.addSymbolicClass(element.getAttribute(NAME_STR));
		for (int i = 0; i < members.getLength(); i++) {
			Node member = members.item(i);
			if (member instanceof Element) {
				Element info = (Element) member;
				if (info.getTagName().equals(METHOD_STR))
					tci.addSymbolicMethod(info.getAttribute(NAME_STR));
				else if (info.getTagName().equals(FIELD_STR))
					tci.addSymbolicField(info.getAttribute(NAME_STR));
			}
		}
	}

	private static void parseClasses(TestCaseInfo tci, Element element) {
		NodeList classes = element.getChildNodes();
		for (int j = 0; j < classes.getLength(); j++) {
			Node nClass = classes.item(j);
			if (nClass instanceof Element)
				parseClass(tci, (Element) nClass);
		}
	}

	private static void debug() {
		Iterator<String> it = testSuites.keySet().iterator();

		LOGGER.debug("Printing the available test suites");
		while (it.hasNext()) {
			String testSuite = it.next();
			List<TestCaseInfo> testCases = testSuites.get(testSuite);
			LOGGER.debug("Test suite: " + testSuite);
			for (TestCaseInfo testCase : testCases)
				LOGGER.debug("    Test case: " + testCase);
		}
	}

	public static void pause() {
		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void moveData(String destFileName, String fromFileName,
			boolean append) {
		try {
			RandomAccessFile dest = new RandomAccessFile(destFileName, "rw");
			RandomAccessFile from = new RandomAccessFile(fromFileName, "rw");

			// Append means seeking to the end of file
			if (append)
				dest.seek(dest.length());

			String line = null;
			from.seek(0);
			while ((line = from.readLine()) != null) {
				dest.writeBytes(line);
				dest.writeBytes("\n");
			}
			dest.close();
			from.setLength(0);
			from.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static String getJPFSignatuare(SootClass mClass, SootMethod mMethod) {
		StringBuffer str = new StringBuffer();
		if (mClass.getName().length() > 0) {
			str.append(mClass.getName());
			str.append(".");
			str.append(mMethod.getName());
		} else
			str.append(mMethod.getName());

		str.append("(");

		List<Object> params = (List<Object>) mMethod.getParameterTypes();
		int i = 0;
		int n = params.size();

		for (Object param : params) {
			if (param instanceof IntType)
				str.append("int");
			else if (param instanceof BooleanType)
				str.append("boolean");
			else if (param instanceof ByteType)
				str.append("byte");
			else if (param instanceof ShortType)
				str.append("short");
			else if (param instanceof CharType)
				str.append("char");
			else if (param instanceof LongType)
				str.append("long");
			else if (param instanceof FloatType)
				str.append("float");
			else if (param instanceof DoubleType)
				str.append("double");
			else if (param instanceof RefType)
				str.append(((RefType) param).getSootClass().getName());
			else if (param instanceof ArrayType) {
				ArrayType type = (ArrayType) param;
				str.append(type.baseType);
				for (int j = 0; j < type.numDimensions; j++)
					str.append("[]");
			} else {
				System.err.println("UNKNOWN TYPE " + param.getClass());
				System.exit(0);
			}

			if (++i < n)
				str.append(",");
		}

		str.append(")");

		return str.toString();
	}

	public static void reportWarning(String header, MethodResults superResults,
			MethodResults subResults, Map<String, String> superConstraints,
			Map<String, String> subConstraints, Set<String> warns,
			String z3ScriptOutput) {
		StringBuffer strbuf = new StringBuffer(header);
		strbuf.append("    ");
		strbuf.append(superResults.getClassName());
		strbuf.append(" vs ");
		strbuf.append(subResults.getClassName());
		strbuf.append(" :: ");
		strbuf.append(superResults.getMethodName());
		strbuf.append("\n");

		Iterator<String> it = null;

		strbuf.append("    SUPERCLASS: \n");
		it = superConstraints.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = superConstraints.get(key);
			strbuf.append(key + "||||" + value + "\n");
		}
		// strbuf.append(superConstraints);
		strbuf.append("\n");

		strbuf.append("    SUBCLASS: \n");
		it = subConstraints.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = subConstraints.get(key);
			strbuf.append(key + "||||" + value + "\n");
		}
		// strbuf.append(subConstraints);

		strbuf.append("    Z3: \n");
		strbuf.append(z3ScriptOutput);
		warns.add(strbuf.toString());
	}

	public static String computeTypesHeader(Map<String, String> symVars) {
		StringBuffer strbuf = new StringBuffer();

		Iterator<String> it = symVars.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = symVars.get(key);

			strbuf.append(" ( " + key + " " + value + " ) ");
		}

		return strbuf.toString();
	}

	private static String hackFormula(String formula) {
		/** Replaces ( != f g ) with (not ( = f g ) ) */
		while (formula.contains("!=")) {
			int index = formula.indexOf("!=");
			String left = formula.substring(0, index);
			String right = formula.substring(index + "!=".length());

			int indexParan = right.indexOf(")");
			String center = right.substring(0, indexParan);
			right = right.substring(indexParan);

			center = "(not ( = " + center + " ) )";

			formula = left + center + right;
		}

		while (formula.contains("&&")) {
			int index = formula.indexOf("&&");
			String left = formula.substring(0, index).trim();
			String right = formula.substring(index + "&&".length());

			int indexParan = left.lastIndexOf(" ");
			String center = left.substring(indexParan + " ".length());
			left = left.substring(0, indexParan + 1);

			formula = left + "and " + center + " " + right + " ";
		}

		while (formula.contains("(and  )"))
			formula = formula.replace("(and  )", "true");
		return formula;
	}

	private static Map<String, String> extractPrivateAttributes(String formula) {
		Map<String, String> vars = new HashMap<String, String>();

		while (formula.contains(".")) {
			int index = formula.indexOf(".");
			String left = formula.substring(0, index);
			String right = formula.substring(index + ".".length());

			int indexSpace = left.lastIndexOf(" ");
			String token1 = left.substring(indexSpace + " ".length());

			indexSpace = right.indexOf(" ");
			String token2 = right.substring(0, indexSpace);
			vars.put(token1 + "." + token2, "Int");

			formula = left.substring(0, token1.length()) + " "
					+ right.substring(token2.length());
		}

		return vars;
	}

	private static Map<String, String> extractInstanceOfPredicates(
			String formula) {
		String inst = "_instanceof_";
		String replace = "_notinstanceof_";
		Map<String, String> vars = new HashMap<String, String>();

		while (formula.contains(inst)) {
			int index = formula.indexOf(inst);
			String left = formula.substring(0, index);
			String right = formula.substring(index + inst.length());

			left = left.substring(left.lastIndexOf(" ") + 1);

			if (right.contains(" "))
				right = right.substring(0, right.indexOf(" "));
			else if (right.contains(")"))
				right = right.substring(0, right.indexOf(")"));
			else {
				System.err
						.println("Something went wrong with the predicates extraction!");
				System.exit(0);
			}

			vars.put(left + inst + right, "Bool");
			formula = formula.replace(left + inst + right, replace);
		}

		return vars;
	}

	private static Map<String, String> extractUnknownVariables(String formula,
			Map<String, String> symVars, Map<String, String> privateAttrs,
			Map<String, String> privateInstanceOfPredicates) {
		Map<String, String> vars = new HashMap<String, String>();

		StringTokenizer strtok = new StringTokenizer(formula, " ()<>=");

		while (strtok.hasMoreTokens()) {
			String token = strtok.nextToken();
			
			if (token.equals("or") || token.equals("and") || token.equals("not") || token.equals("implies"))
				continue;

			if (token.equals("true") || token.equals("false") || token.equals("assertion_disabled") || token.equals("!"))
				continue;

			try {
				/** Continue if this succeeds */
				Double.parseDouble(token);
				continue;
			} catch(Exception e) {
				
			}

			if (symVars.containsKey(token) || privateAttrs.containsKey(token) || privateInstanceOfPredicates.containsKey(token))
				continue;

			/** OK, first time I saw it */
			vars.put(token, "Int");
		}
		return vars;
	}

	private static String decorateFormulaWithInstanceConstraints(
			String formula, Map<String, String> privateInstanceOfPredicates) {
		/** If we have multiple cases of:
		 * obj_instanceof_lsp_test_savadvanced_MyDummyImplementation1
		 * obj_instanceof_lsp_test_savadvanced_MyDummyImplementation2
		 * 
		 * =>
		 * (or
		 *   (and obj_instanceof_lsp_test_savadvanced_MyDummyImplementation1 (not obj_instanceof_lsp_test_savadvanced_MyDummyImplementation2))
		 *   (and (not obj_instanceof_lsp_test_savadvanced_MyDummyImplementation1) obj_instanceof_lsp_test_savadvanced_MyDummyImplementation2)
		 * )
		 */
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("(or ");
		Iterator<String> it1 = privateInstanceOfPredicates.keySet().iterator();
		while (it1.hasNext()) {
			String key1 = it1.next();
			
			Iterator<String> it2 = privateInstanceOfPredicates.keySet().iterator();
			strbuf.append(" (and ");
			while(it2.hasNext()) {
				String key2 = it2.next();
				
				strbuf.append(" ");
				if (key1 == key2)
					strbuf.append(key2);
				else
					strbuf.append("(not " + key2 + ")");
				strbuf.append(" ");
			}
			strbuf.append(" ) ");
		}
		strbuf.append(") ");
		
		return "(and " + strbuf.toString() + " " + formula + ")";
	}

	public static String generateZ3VerificationCode(MethodResults method,
			String formula, String header, Map<String, String> symVars) {
		String className = method.getClassName();
		String methodName = method.getMethodName();

		Map<String, String> privateAttrs = extractPrivateAttributes(formula);
		Map<String, String> privateInstanceOfPredicates = extractInstanceOfPredicates(formula);
		Map<String, String> unknownVars = extractUnknownVariables(formula,
				symVars, privateAttrs, privateInstanceOfPredicates);
		
		if (privateInstanceOfPredicates.size() > 0)
			formula = decorateFormulaWithInstanceConstraints(formula, privateInstanceOfPredicates);

		String fileName = Z3_FOLDER + "/" + Z3_TESTCASES_FOLDER + "/" + header
				+ "_" + className + "_" + methodName + ".smt";

		try {
			System.err.println(fileName);
			RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
			raf.setLength(0);
			raf.writeBytes("(benchmark sav11\n" + "  :logic AUFNIRA\n"
					+ "  :status unknown\n" + "  :extrasorts ( Ref )\n" +

					/** Assertions variable */
					"  :extrafuns ( (assertion_disabled Int) )\n" +

					/** Variables received as parameters */
					"  :extrafuns ( " + computeTypesHeader(symVars) + " )\n" +

					/** Private attributes of the class */
					"  :extrafuns ( " + computeTypesHeader(privateAttrs)
					+ " )\n" +

					/** instanceof predicates */
					"  :extrafuns ( "
					+ computeTypesHeader(privateInstanceOfPredicates) + " )\n" +

					/**
					 * Unknown variables generated by JPF, with Int type by
					 * default
					 */
					"  :extrafuns ( " + computeTypesHeader(unknownVars)
					+ " )\n" +

					"  :formula\n" + "    " + hackFormula(formula) + "\n)\n");
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return fileName;
	}

	public static String generateDefaultValueForZ3Type(String type) {
		if (type.equals("Int") || type.equals("Bool") || type.equals("Void"))
			return "0";
		System.err.println("UNKNOWN TYPE Z3 " + type);
		System.exit(0);
		return null;
	}

	public static String signatureToType(String line) {
		String type = null;

		if (line.length() == 1) {
			switch (line.charAt(0)) {
			case 'S':
			case 'B':
			case 'C':
			case 'I':
			case 'J':
				type = "Int";
				break;

			case 'D':
			case 'F':
				type = "Real";
				break;

			case 'Z':
				type = "Bool";
				break;

			case 'L':
			case '[':
				type = "Ref";
				break;

			case 'V':
				type = "Void";
				break;
			}

		} else
			type = "Ref";

		return type;
	}
}
