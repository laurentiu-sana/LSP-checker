package lsp.scoring;

import java.io.File;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.StringTokenizer;

import lsp.scoring.test.TestUnits;
import lsp.scoring.trace.Debugger;
import soot.Scene;

public class ScoringAlgorithmMain {
	private static boolean __init = false;
	private static String[] __data = null;

	private static String[] loadUserDefinedProperties(String fileName) {
		String[] defaultValues = loadUserOverridenProperties(Constants.CONFIG_FILE);
		if (fileName != null)
			return loadUserOverridenProperties(fileName);
		return defaultValues;
	}

	private static int getIntegerValue(String value, String var) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			System.err.println("Invalid input value for " + var + ": |" + value
					+ "|");
			e.printStackTrace();
			System.err.println("Using the default value");
		}
		return 0;
	}

	private static String[] loadUserOverridenProperties(String fileName) {
		try {
			RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
			String m_regex = null, m_package = null;
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

				/**
				 * XXX: remove this hardcoded piece of code
				 */
				if (key.equals("DEBUG"))
					Constants.DEBUG = Boolean.parseBoolean(value);
				else if (key.equals("DEBUG_FOLDER"))
					Constants.DEBUG_FOLDER = value;
				else if (key.equals("TESTS_CLASSPATH"))
					Constants.TESTS_CLASSPATH = value;
				else if (key.equals("OUTPUT_FOLDER"))
					Constants.OUTPUT_FOLDER = value;
				else if (key.equals("REGEX"))
					m_regex = value;
				else if (key.equals("PACKAGE"))
					m_package = value;
				else if (key.equals("THRESHOLD_SCORE"))
					Constants.THRESHOLD_SCORE = Double.parseDouble(value);
				else if (key.equals("GENERATED_TESTS_FOLDER")) {
					Constants.GENERATED_TESTS_FOLDER = value;
					Constants.execAndWait("mkdir -p " + value);
				} else if (key.equals("TESTS_PACKAGE")) {
					Constants.TESTS_PACKAGE = value;
					Constants.execAndWait("mkdir -p "
							+ Constants.GENERATED_TESTS_FOLDER + "/" + value);
				} else if (key.equals("TEST_NAME"))
					Constants.TEST_NAME = value;
				else if (key.equals("MAX_METHODS_NUMBER"))
					Constants.MAX_METHODS_NUMBER = getIntegerValue(value,
							"MAX_METHODS_NUMBER");
				else if (key.equals("MAX_METHODS_TESTS"))
					Constants.MAX_METHODS_TESTS = getIntegerValue(value,
							"MAX_METHODS_TESTS");
			}
			raf.close();

			if (Constants.DEBUG) {
				// Change the stdout stream only if DEBUG is true
				System.setOut(new PrintStream(Constants.DEBUG_FOLDER
						+ "/debug.txt"));
			}
			return new String[] { m_regex, m_package };
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static synchronized String[] init(String fileName) {
		if (__init)
			return __data;

		__init = true;
		__data = loadUserDefinedProperties(fileName);

		// Load basic support for Object
		Scene.v().loadClassAndSupport("java.lang.Object");
		Scene.v().loadClassAndSupport("java.lang.Double");
		Scene.v().loadClassAndSupport("java.util.Date");
		Scene.v().loadClassAndSupport("java.lang.Boolean");
		Scene.v().loadClassAndSupport("java.lang.Deprecated");
		Scene.v().loadClassAndSupport("java.io.Reader");
		Scene.v().loadClassAndSupport("java.lang.Long");
		Scene.v().loadClassAndSupport("java.lang.LinkageError");
		Scene.v().loadClassAndSupport("java.lang.Thread");
		Scene.v().loadClassAndSupport("java.lang.OutOfMemoryError");
		Scene.v().loadClassAndSupport("java.io.FileNotFoundException");
		Scene.v().loadClassAndSupport("java.util.Formatter");
		Scene.v().loadClassAndSupport("java.lang.Float");
		Scene.v().loadClassAndSupport("java.lang.IndexOutOfBoundsException");
		Scene.v()
				.loadClassAndSupport("java.lang.UnsupportedOperationException");
		Scene.v().loadClassAndSupport("java.lang.reflect.Array");
		Scene.v().loadClassAndSupport("java.lang.ArrayStoreException");
		Scene.v().loadClassAndSupport("java.lang.ClassCastException");
		Scene.v().loadClassAndSupport("java.io.BufferedReader");
		Scene.v().loadClassAndSupport("java.lang.ref.Reference$Lock");
		Scene.v().loadClassAndSupport("java.lang.InternalError");
		Scene.v().loadClassAndSupport("java.net.URLStreamHandler");
		Scene.v().loadClassAndSupport("java.net.URLStreamHandlerFactory");
		Scene.v().loadClassAndSupport("java.net.URISyntaxException");
		Scene.v().loadClassAndSupport("java.net.URLConnection");
		Scene.v().loadClassAndSupport("java.util.NoSuchElementException");
		Scene.v().loadClassAndSupport("java.io.InputStreamReader");
		Scene.v().loadClassAndSupport("java.lang.ref.ReferenceQueue$Lock");
		Scene.v().loadClassAndSupport("org.w3c.dom.Document");
		Scene.v().loadClassAndSupport("org.xml.sax.SAXException");
		Scene.v().loadClassAndSupport("org.w3c.dom.Element");
		Scene.v().loadClassAndSupport("java.net.JarURLConnection");
		Scene.v().loadClassAndSupport("java.util.jar.JarEntry");
		Scene.v().loadClassAndSupport("java.io.NotSerializableException");
		Scene.v().loadClassAndSupport("java.lang.AssertionError");
		Scene.v().loadClassAndSupport("java.util.concurrent.atomic.AtomicLong");
		Scene.v().loadClassAndSupport(
				"java.util.concurrent.ConcurrentHashMap$Segment");
		Scene.v().loadClassAndSupport("org.xml.sax.EntityResolver");
		Scene.v().loadClassAndSupport("org.xml.sax.InputSource");
		Scene.v().loadClassAndSupport("org.xml.sax.ErrorHandler");
		Scene.v().loadClassAndSupport("org.xml.sax.SAXParseException");
		Scene.v().loadClassAndSupport("java.security.SecureRandom");

		// Set soot_classpath: java_class_path + tests
		String java_classpath = System.getProperties().getProperty(
				"java.class.path", "");

		/**
		 * JAVA_CLASSPATH must contain reference to JRE System Library;
		 * on my machine I have:
		 *   "/usr/lib/jvm/java-6-sun-1.6.0.15/jre/lib"
		 *   "/usr/lib/jvm/java-6-sun-1.6.0.15/jre/lib/ext"
		 */
		Scene.v().setSootClassPath(
				java_classpath + ":" + Constants.TESTS_CLASSPATH);

		return __data;
	}

	private static void generateJavaTests(ClassHierarchy ch,
			String packageName, String className) {
		try {
			String filename = Constants.GENERATED_TESTS_FOLDER + "/"
				+ Constants.TESTS_PACKAGE + "/" + className
				+ ".java";
			
			/** File already exists and maybe was hacked by the user */
			if (new File(filename).exists())
				return;
			
			RandomAccessFile fout = new RandomAccessFile(filename, "rw");
			fout.setLength(0);
			//RandomAccessFile tmp = new RandomAccessFile(Constants.TMP_FILE,
			//		"rw");
			HashSet<String> testCases = new HashSet<String>();

			if (Constants.TESTS_PACKAGE != null
					&& Constants.TESTS_PACKAGE.length() > 0)
				fout.writeBytes("package " + packageName + ";\n\n");

			fout.writeBytes(Constants.TEST_HEADER);
			fout
					.writeBytes("@SuppressWarnings({\"unchecked\", \"deprecation\"})\n");
			fout.writeBytes("public class " + className + "\n");
			fout.writeBytes("{\n");

			fout.writeBytes("    public static void main(String[] args)\n");
			fout.writeBytes("    {\n");

			// main(String[]) body
			for (MethodScore it : ch.getMethodsScore()) {
				StringBuffer method = it.generateTestCases(ch);
				if (method != null && method.length() > 1) {
					StringTokenizer testCaseBlock = new StringTokenizer(method
							.toString(), "\n");
					while (testCaseBlock.hasMoreTokens()) {
						String testCase = testCaseBlock.nextToken();
						if (testCase.length() > 0
								&& !testCases.contains(testCase))
							testCases.add(testCase);
					}

					// After some successfully write, we end the process
					if (Constants.MAX_METHODS_NUMBER > 0) {
						if (testCases.size() > Constants.MAX_METHODS_NUMBER)
							break;
					}
				}
			}

			//for (String classImport : ch.getImports()) {
			//	if (!classImport.startsWith("java.lang."))
			//		fout.writeBytes("import " + classImport + ";\n");
			//}
			for (String testCase : testCases) {
				fout.writeBytes(testCase);
				fout.writeBytes("\n");
			}
			fout.writeBytes("    }\n");
			fout.writeBytes("}\n\n");
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void compileJavaTests() {
		Constants.execAndWaitWithInput("mkdir -p " + Constants.OUTPUT_FOLDER
				+ "/build", null);
		//	if (Constants.INSANE_DEBUG)
		System.err.println("ant	compile-tests -Dg.tests.dir="
				+ Constants.OUTPUT_FOLDER + " -Dg.results.dir="
				+ Constants.OUTPUT_FOLDER + "/build");
		Constants.execAndWaitWithInput("ant	compile-tests -Dg.tests.dir="
				+ Constants.OUTPUT_FOLDER + " -Dg.results.dir="
				+ Constants.OUTPUT_FOLDER + "/build", null);
	}

	private static void dumpContent(ClassHierarchy ch, String filename,
			String className) {
		try {
			String packageName = null;
			if (Constants.TESTS_PACKAGE != null)
				packageName = Constants.TESTS_PACKAGE.replace('/', '.');
			generateJavaTests(ch, packageName, className);
			compileJavaTests();

			RandomAccessFile raf = new RandomAccessFile(filename, "rw");
			raf.setLength(0);
			if (packageName != null) {
				raf.writeBytes(packageName);
				raf.writeBytes(".");
			}
			raf.writeBytes(className);

			for (String cl : ch.getClassesWithMain())
				raf.writeBytes("," + cl);
			raf.writeBytes("\n");
			for (MethodScore it : ch.getMethodsScore())
				raf.writeBytes(it.toString() + "\n");
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The test suite is calling this method directly, because it does not
	 * override the default configuration
	 */
	public static void main(String testRegex, String testPackage,
			String fileName) {
		/** 
		 * Test units give valid testRegex and testPackage, but they still
		 * load the configuration file
		 */

		// Init is called only once
		String[] info = init(fileName);
		if ((testRegex == null) && (testPackage == null)) {
			if ((info == null) || (info.length != 2) || (info[0] == null)
					|| (info[1] == null)) {
				System.err
						.println("No test regex and package given, using default!");
				testRegex = TestUnits.DUMMY_REGEX;
				testPackage = TestUnits.DUMMY_PACKAGE;
			} else {
				testRegex = info[0];
				testPackage = info[1];
			}
		}

		ClassHierarchy ch = new ClassHierarchy(testRegex);
		//ch.extractJars();
		ch.loadClasses(testPackage);
		ch.computeClassScores();
		ch.computeMethodScores();
		if (testPackage == null || testPackage.equals(""))
			testPackage = "lsp.checker";

/*
		dumpContent(ch, Constants.OUTPUT_FOLDER + "/" + testPackage
				+ ".lsp.sort", "LSPCheckerGeneratedTest"
				+ Constants.TEST_NAME.toUpperCase()
				+ System.currentTimeMillis());
*/
		dumpContent(ch, Constants.OUTPUT_FOLDER + "/" + testPackage
				+ ".lsp.sort", "LSPCheckerGeneratedTest"
				+ Constants.TEST_NAME.toUpperCase());

		if (Constants.DEBUG) {
			Debugger.dumpToXML(true);
			Debugger.initXML(testPackage);
			Debugger.dumpClassesNumber(Scene.v().getClasses().size());
			Debugger.dumpClasses(testRegex, ch.getFinalScores(), ch.getInfo());
			Debugger.finishXML();
		}
	}

	public static void main(String testRegex, String testPackage) {
		main(testRegex, testPackage, null);
	}

	public static void main(String[] args) {
		String fileName = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--config") && ((i + 1) < args.length))
				fileName = args[i + 1];
		}
		if (fileName != null) {
			if (fileName.equals("null") || fileName.equals(""))
				fileName = null;

			/** Drop the this test :-) */
			if (fileName != null && fileName.equals("log4j.properties"))
				return;
		}

		main(null, null, fileName);
	}
}
