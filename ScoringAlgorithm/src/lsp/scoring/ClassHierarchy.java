package lsp.scoring;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import soot.ArrayType;
import soot.Body;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.util.Chain;

public class ClassHierarchy {
	private final static Logger LOGGER = Logger.getLogger(ClassHierarchy.class);

	private String m_classRegex;
	private HierarchyTraverse ht;
	private List<MethodScore> methodsScore;
	private List<String> classesWithMain;
	private double m_max;
	private volatile int jarCounter;

	//private HashSet<String> imports;

	public ClassHierarchy(String classRegex) {
		m_classRegex = classRegex;
		methodsScore = new LinkedList<MethodScore>();
		classesWithMain = new LinkedList<String>();
		//imports = new HashSet<String>();
		ht = null;
		m_max = 0;
	}

	public HashMap<SootClass, Double> getFinalScores() {
		return ht.getScores();
	}

	public HashMap<SootClass, ClassInfo> getInfo() {
		return ht.getInfo();
	}

	private void loadFile(File file, String packageName) {
		String name = file.getName();

		try {
			if (name.endsWith(".class")) {
				name = name.substring(0, name.length() - 6);
				if (packageName == null || packageName.equals(""))
					Scene.v().loadClassAndSupport(name);
				else
					Scene.v().loadClassAndSupport(packageName + "." + name);
			}
		} catch (Exception e) {
			/**
			 * Ignore class loading errors, because the scoring algorithms
			 * uses heuristics to approximate the polymorphism of methods.
			 */
			if (Constants.INSANE_DEBUG) {
				e.printStackTrace();
				try {
					System.in.read();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void loadDirectory(File dir, String packageName) {
		if (packageName == null || packageName.equals(""))
			packageName = dir.getName();
		else
			packageName += "." + dir.getName();

		for (File file : dir.listFiles()) {
			if (file.isDirectory())
				loadDirectory(file, packageName);
			else
				loadFile(file, packageName);
		}
	}

	public void loadClasses(String packageName) {
		String folderName = Constants.TESTS_CLASSPATH + "/"
				+ packageName.replace('.', '/');
		File folder = new File(folderName);

		if (folder.listFiles() == null) {
			System.err.println("Could not load classes from the folder: "
					+ folderName);
			System.exit(-1);
		}

		for (File file : folder.listFiles()) {
			String name = file.getName();

			// Ignore several files
			if (name.equals(".") || name.equals("..") || name.equals(".svn"))
				continue;

			if (file.isDirectory())
				loadDirectory(file, packageName);
			else
				loadFile(file, packageName);
		}
	}

	/**
	 * Add to the score the influence of the superclasses (the implemented interfaces)
	 * For example:
	 *   class A {}
	 *   interface I1 {}, interface I2 {}
	 *   class B extends A implements I1, I2 {}
	 * 
	 *  For B we should have [score(A) + score(I1) + score(I2)] - max{score(A), score(I1), score(I2)}
	 * It is important to know that B can behave as A, I1 and I2
	 */
	private void addInterfacesScore(HashMap<SootClass, Double> scores,
			SootClass currentClass) {
		int counter = 0;
		if (currentClass.hasSuperclass()
				&& scores.containsKey(currentClass.getSuperclass()))
			counter++;
		counter += currentClass.getInterfaceCount();
		Double[] data = new Double[counter];
		Double max = 0.0, sum = 0.0, score;

		if (currentClass.hasSuperclass()
				&& scores.containsKey(currentClass.getSuperclass())) {
			score = scores.get(currentClass.getSuperclass());
			LOGGER.info("[influence scoring]     extends "
					+ currentClass.getSuperclass() + " {score " + score + "}");
			data[0] = score;
		}
		for (SootClass currentInterface : currentClass.getInterfaces()) {
			// User --counter to avoid using another temporary variable
			if (scores.containsKey(currentClass)) {
				score = scores.get(currentInterface);
				LOGGER.info("[influence scoring]     implements "
						+ currentInterface + " {score " + score + "}");
				if (score == null)
					score = 0.0;
				data[--counter] = score;
			} else
				data[--counter] = 0.0;
		}

		for (Double value : data) {
			sum += value;
			if (max < value)
				max = value;
		}

		sum -= max;
		LOGGER.info("[influence scoring]   interfaces scores additional " + sum
				* Constants.INTERFACE_POLYMORPHIC
				+ " {max interface/class influence removed}");

		scores.put(currentClass, scores.get(currentClass) + sum
				* Constants.INTERFACE_POLYMORPHIC);
	}

	private void addFieldsScore(HashMap<SootClass, Double> scores,
			SootClass currentClass) {
		if (currentClass.getFieldCount() > 0) {
			Double sum = 0.0;
			for (SootField currentField : currentClass.getFields()) {
				Double score = getTypeScore(scores, currentField.getType());
				LOGGER.info("[influence scoring]   field of type "
						+ currentField.getType() + " {score " + score + "}");
				sum += score;
			}
			LOGGER.info("[influence scoring]   => the fields score additional "
					+ sum * Constants.FIELD_POLYMORPHIC);

			scores.put(currentClass, scores.get(currentClass) + sum
					* Constants.FIELD_POLYMORPHIC);
		}
	}

	private double maxScore() {
		return m_max;
	}

	private double getTypeScore(HashMap<SootClass, Double> classScores, Type it) {
		/**
		 * Given the ArrayType, it's score is proportional with the depth of the
		 * declaration(number of []):
		 *   score(MyClass[][][]) = ((1 + Constants.METHOD_ARRAY_FIELD) ^ depth) *
		 *     score(MyClass) 
		 */
		if (it instanceof ArrayType) {
			ArrayType array = (ArrayType) it;
			LOGGER
					.debug("[get type's score] class " + array + ", type "
							+ array.baseType + ", numDimensions "
							+ array.numDimensions);
			return Math.pow(1 + Constants.METHOD_ARRAY_FIELD,
					array.numDimensions)
					* getTypeScore(classScores, array.baseType);
		} else if (it instanceof RefType) {
			RefType ref = (RefType) it;
			if (classScores.containsKey(ref.getSootClass()))
				return classScores.get(ref.getSootClass());
			/*
			 * FIXME: What about Java generics?
			 * In bytecode we don't have the reference to
			 * the BaseClass in the following declaration:
			 *   Class<T extends BaseClass>
			 * Yep, we see only Class.
			 */

			/**
			 *  If current reference type is <Class> or <Object>,
			 *  then we consider it's score as being the maximum
			 *  value of the initial scores
			 */
			String refName = ref.getSootClass().getName();
			if (refName.equals("java.lang.Class")
					|| refName.equals("java.lang.Object"))
				return maxScore();
		}
		return 0.0;
	}

	private double addCallingSuperClassScore(
			HashMap<SootClass, Double> classScores, SootClass currentClass,
			SootMethod currentMethod) {
		if (!currentClass.hasSuperclass())
			return 0.0;
		try {
			Body body = currentMethod.retrieveActiveBody();
			Chain<Unit> units = body.getUnits();
			Iterator<Unit> unitIt = units.iterator();
			Unit currentStmt = null;
			String str = null;

			while (unitIt.hasNext()) {
				currentStmt = unitIt.next();
				str = currentStmt.toString();

				/**
				 * From the JVM specification, specialinvoke is used to invoke
				 * methods from another class. In particular, we are
				 * interested of methods invoked in the superclass.
				 * 
				 * The called method should not be a constructor. If we would
				 * consider that calling a constructor increases the polymorphic
				 * score, then we would give higher scores to the subclasses. 
				 */
				if (str.contains("specialinvoke")
						&& str.contains(currentClass.getSuperclass().getName())
						&& !str.contains("<init>")) {
					LOGGER.info("[influence scoring]     in method "
							+ currentMethod.getName()
							+ " found a call to the superclass's method: "
							+ str);
					Double value = classScores
							.get(currentClass.getSuperclass());
					if (value == null)
						return 0.0;
					return value;
				}
			}

		} catch (Exception e) {
		}
		return 0.0;
	}

	private void addMethodsScore(HashMap<SootClass, Double> scores,
			SootClass currentClass) {
		if (currentClass.getMethodCount() > 0) {
			Double sum = 0.0, aux;

			for (SootMethod currentMethod : currentClass.getMethods()) {
				// Take into account only concrete and public methods
				if (!currentMethod.isConcrete() || !currentClass.isPublic())
					continue;
				LOGGER
						.info("[influence scoring]   adding the influence of the method {"
								+ currentMethod + "}");
				aux = addCallingSuperClassScore(scores, currentClass,
						currentMethod);
				if (aux > 0)
					LOGGER
							.info("[influence scoring]     for calling methods from the superclass, this method scores "
									+ aux);
				sum += aux;

				Double score = 0.0;
				for (Object currentType : currentMethod.getParameterTypes()) {
					/**
					 * FIXME: this is a hook(should be read as hack) in soot's
					 * type system; I count only the references and arrays of
					 * references to the loaded classes. We have to trust that
					 * the external code is perfect ;-)
					 */
					if (currentType instanceof Type) {
						score = getTypeScore(scores, (Type) currentType);
						LOGGER
								.info("[influence scoring]     parameter with type "
										+ currentType
										+ " {score "
										+ score
										+ "}");
						sum += score;
					}
				}

				score = getTypeScore(scores, currentMethod.getReturnType());
				LOGGER.info("[influence scoring]     return type "
						+ currentMethod.getReturnType() + " {score " + score
						+ "}");
				sum += score;
			}

			LOGGER
					.info("[influence scoring]   => the methods score additional "
							+ sum * Constants.METHOD_POLYMORPHIC);
			scores.put(currentClass, scores.get(currentClass) + sum
					* Constants.METHOD_POLYMORPHIC);
		}

	}

	private void computeMax(HashMap<SootClass, Double> initialScores) {
		Double value;

		for (Iterator<Double> it = initialScores.values().iterator(); it
				.hasNext();) {
			value = it.next();
			if (m_max < value)
				m_max = value;
		}
	}

	public void computeClassScores() {
		if (ht == null) {
			ht = new HierarchyTraverse(m_classRegex);
			ht.computeScores();

			LOGGER
					.info("[influence scoring] adding the influence of the implemented interfaces, with a normalization of "
							+ Constants.INTERFACE_POLYMORPHIC);
			LOGGER
					.info("[influence scoring] adding the influence of the contained fields, with a normalization of "
							+ Constants.FIELD_POLYMORPHIC);

			LOGGER
					.info("[influence scoring] adding the influence of the contained methods, with a normalization of "
							+ Constants.METHOD_POLYMORPHIC);

			HashMap<SootClass, Double> classScores = ht.getScores();
			computeMax(classScores);
			Iterator<SootClass> it = classScores.keySet().iterator();
			while (it.hasNext()) {
				SootClass currentClass = it.next();
				LOGGER.info("[influence scoring] the current class is {"
						+ currentClass + "}");
				addInterfacesScore(classScores, currentClass);
				addFieldsScore(classScores, currentClass);
				addMethodsScore(classScores, currentClass);
			}
		}
		if (Constants.DEBUG)
			ht.debug();
	}

	public void computeMethodScores() {
		HashMap<SootClass, Double> classScores = ht.getScores();

		LOGGER
				.info("[methods scoring] ordering the methods by the polymorphic score");
		for (Iterator<SootClass> itC = classScores.keySet().iterator(); itC
				.hasNext();) {
			SootClass currentClass = itC.next();

			LOGGER.info("[methods scoring] the current class is "
					+ currentClass);

			for (Iterator<SootMethod> itM = currentClass.methodIterator(); itM
					.hasNext();) {
				SootMethod currentMethod = itM.next();
				Double sum = 0.0, score = 0.0;

				if (currentMethod.getName().equals("main")
						&& currentMethod.isStatic()
						&& currentMethod.isPublic()
						&& currentMethod.getParameterCount() == 1
						&& currentMethod.getParameterType(0).toString().equals(
								"java.lang.String[]")) {
					String className = currentClass.getName();
					if (!classesWithMain.contains(className))
						classesWithMain.add(className);
				}

				LOGGER.info("[methods scoring]   the current method is "
						+ currentMethod);

				for (Object it : currentMethod.getParameterTypes()) {
					if (it instanceof Type) {
						score = getTypeScore(classScores, (Type) it);
						LOGGER
								.info("[methods scoring]     adding field of type "
										+ it + " {score " + score + "}");
						sum += score;
					}
				}
				score = getTypeScore(classScores, currentMethod.getReturnType());
				LOGGER.info("[methods scoring]     adding the return type "
						+ currentMethod.getReturnType() + " {score " + score
						+ "}");
				sum += score;
				LOGGER.info("[methods scoring]   total score: " + sum);
				methodsScore.add(new MethodScore(currentMethod, sum));
			}
		}
		Collections.sort(methodsScore);
	}

	public List<MethodScore> getMethodsScore() {
		return methodsScore;
	}

	public List<String> getClassesWithMain() {
		return classesWithMain;
	}

	/**
	 * Recursive extracts the jars at the current iteration
	 */
	private void extractJarsRecursive(File root) {
		for (File file : root.listFiles()) {
			if (file.isDirectory())
				extractJarsRecursive(file);
			else if (file.getName().endsWith(".jar")) {
				/**
				 * echo A | unzip file.jar -d directory
				 * 
				 * If the file is already extracted then unzip
				 * asks if it can replace the contained files;
				 * by default, our answer is yes.
				 */
				String cmd = "unzip " + file.getAbsolutePath() + " -d "
						+ Constants.TESTS_CLASSPATH;
				LOGGER.info(cmd);
				Constants.execAndWaitWithInput(cmd, "A");
			}
		}
	}

	/**
	 * Unjar all the jar archives from the SUT's folder
	 */
	public void extractJars() {
		int oldJars = 0, newJars = 0;

		while (true) {
			newJars = countJars();

			/**
			 * Stop if no new jars were extracted
			 */
			if (newJars == oldJars)
				break;
			extractJarsRecursive(new File(Constants.TESTS_CLASSPATH));
			oldJars = newJars;
		}
		System.exit(0);
	}

	private void countJarsRecursive(File root) {
		for (File file : root.listFiles()) {
			if (file.isDirectory())
				countJarsRecursive(file);
			else if (file.getName().endsWith(".jar"))
				jarCounter++;
		}
	}

	public int countJars() {
		jarCounter = 0;
		countJarsRecursive(new File(Constants.TESTS_CLASSPATH));
		return jarCounter;
	}
/*
	public void addImportClass(String className) {
		imports.add(className);
	}

	public HashSet<String> getImports() {
		return imports;
	}
*/	
}
