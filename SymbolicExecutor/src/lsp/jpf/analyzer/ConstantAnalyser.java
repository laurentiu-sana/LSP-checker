package lsp.jpf.analyzer;

import gov.nasa.jpf.jvm.ClassInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import lsp.jpf.parse.Constants;

import org.apache.log4j.Logger;

/**
 * Hook for loading the symbolic classes and methods, in order to
 * find out the constant methods of a class
 */
public class ConstantAnalyser {
	private final static Logger LOGGER = Logger
			.getLogger(ConstantAnalyser.class);

	private static ConstantAnalyser m_analyser = null;

	/** Members*/
	private HashSet<SymbolicClass> classesInfo;

	/** Shadowing support */
	private HashSet<SymbolicClass> shadowClassesInfo;
	private boolean useShadow;

	private HashMap<ClassInfo, List<ConstantMethod>> methodsInfo;

	public static ConstantAnalyser getInstance() {
		if (m_analyser == null)
			m_analyser = new ConstantAnalyser();
		return m_analyser;
	}

	public static void addSymbolicClass(ClassInfo ci) {
		getInstance().addSymbolicClassImpl(ci);
	}

	public static void finish() {
		getInstance().finishImpl();
	}

	public static void addSymbolicMethod(ConstantMethod cm) {
		getInstance().addSymbolicMethodImpl(cm);
	}

	public static List<ClassInfo> getSymbolicClasses() {
		return getInstance().getSymbolicClassesImpl();
	}

	public static List<ConstantMethod> getConstantMethods(ClassInfo ci) {
		return getInstance().getConstantMethodsImpl(ci);
	}

	private ConstantAnalyser() {
		classesInfo = new HashSet<SymbolicClass>();
		shadowClassesInfo = new HashSet<SymbolicClass>();
		useShadow = false;
		methodsInfo = new HashMap<ClassInfo, List<ConstantMethod>>();
	}

	private boolean addSymbolicClass(SymbolicClass sc, boolean useShadow) {
		boolean add = true;
		for (SymbolicClass it : classesInfo)
			if (it.getClassInfo().toString().equals(sc.toString())) {
				add = false;
				break;
			}
		if (add) {
			LOGGER.info("[addSymbolicClass] class info: " + sc);
			if (useShadow)
				shadowClassesInfo.add(sc);
			else
				classesInfo.add(sc);
		}

		return add;
	}

	public void addSymbolicClassImpl(ClassInfo ci) {
		SymbolicClass sc = new SymbolicClass(ci);

		if (Constants.FIXED_JPF) {
			if (!classesInfo.contains(sc)) {
				if (useShadow)
					shadowClassesInfo.add(sc);
				else
					classesInfo.add(sc);
			}
		} else
			addSymbolicClass(sc, useShadow);
	}

	public void finishImpl() {
		if (!Constants.ENABLE_CONSTANT_METHODS_PHASE)
			return;

		LOGGER.info("[finish] " + classesInfo);
		boolean add;

		do {
			useShadow = true;
			add = false;
			shadowClassesInfo.clear();

			for (SymbolicClass sc : classesInfo)
				methodsInfo.put(sc.getClassInfo(), sc.computeConstantMethods(sc
						.getClassInfo()));
			for (SymbolicClass sc : shadowClassesInfo)
				add |= addSymbolicClass(sc, false);
		} while (add);
		useShadow = false;
	}

	public void addSymbolicMethodImpl(ConstantMethod cm) {
		ClassInfo ci = cm.getClassInfo();
		List<ConstantMethod> list = null;
		boolean add = true;

		if (methodsInfo.containsKey(ci))
			list = methodsInfo.get(ci);
		else {
			list = new LinkedList<ConstantMethod>();
			methodsInfo.put(ci, list);
		}

		for (ConstantMethod cMethod : list) {
			if (cMethod.getMethodInfo().toString().equals(
					cm.getMethodInfo().toString())) {
				add = false;
				break;
			}
		}

		if (add)
			list.add(cm);
	}

	public List<ClassInfo> getSymbolicClassesImpl() {
		List<ClassInfo> classes = new LinkedList<ClassInfo>();
		for (SymbolicClass cm : classesInfo)
			classes.add(cm.getClassInfo());
		return classes;
	}

	public List<ConstantMethod> getConstantMethodsImpl(ClassInfo ci) {
		LOGGER.info("[getConstantMethods] class info: " + ci);
		/** debug(); */
		return methodsInfo.get(ci);
	}

	@SuppressWarnings("unused")
	private void debug() {
		LOGGER.info("<debug>");
		for (SymbolicClass sc : classesInfo) {
			LOGGER.info("[debug] class info: " + sc);
			List<ConstantMethod> list = methodsInfo.get(sc.getClassInfo());
			if (list != null) {
				for (ConstantMethod cm : list)
					LOGGER.info("[debug] constant method: " + cm);
			}
		}
		LOGGER.info("</debug>");
	}
}
