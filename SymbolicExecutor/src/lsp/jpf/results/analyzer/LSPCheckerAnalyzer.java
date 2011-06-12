package lsp.jpf.results.analyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import lsp.jpf.parse.Constants;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

/** Analyzer for the LSP checker */
public class LSPCheckerAnalyzer {
	private static LSPCheckerAnalyzer instance = null;

	/** (class, list of constant methods) */
	private HashMap<String, Set<String>> m_info;

	private LSPCheckerAnalyzer() {
		m_info = new HashMap<String, Set<String>>();
	}

	public void add(String className, String methodName) {
		Set<String> set = m_info.get(className);
		if (set == null) {
			set = new HashSet<String>();
			set.add(methodName);

			/** Loading the class into the scene */
			Scene.v().loadClassAndSupport(className);

			m_info.put(className, set);
		} else
			set.add(methodName);
	}

	private static SootMethod getMethodByFullName(SootClass mClass,
			String methodName) {
		/** FIXME: This is broken */
		for (SootMethod method : mClass.getMethods()) {
			if (methodName.startsWith(method.getName()))
				return method;
		}
		return null;
	}

	private static boolean containsMethod(Set<String> methods, SootMethod method) {
		for (String str : methods) {
			str = str.substring(str.lastIndexOf('.') + 1);
			if (str.startsWith(method.getName()))
				return true;
		}
		return false;
	}

	private void analyzeClasses(SootClass superClass, Set<String> superMethods,
			SootClass subClass, Set<String> subMethods, Set<String> warns) {
		/**
		 * For each constant method in the super class
		 *   1. check if it is overridden in the subclass
		 *   2. if not, continue the execution
		 *   3. if true, check if the method is constant in the subclass too
		 *     3.1. if not, report the warning
		 *     3.2. if yes, continue the execution
		 */
		for (String ctMethod : superMethods) {
			ctMethod = ctMethod.substring(ctMethod.lastIndexOf('.') + 1);
			SootMethod superMethod = getMethodByFullName(superClass, ctMethod);
			SootMethod subMethod = getMethodByFullName(subClass, ctMethod);

			// The method is not overridden
			if (superMethod == subMethod || subMethod == null)
				continue;

			if (!containsMethod(subMethods, subMethod))
				warns.add(superMethod + " constant, but the overridden"
						+ subMethod + " is not");
		}
	}

	private void constraintsAnalyzer(SootClass superClass, SootClass subClass,
			HashMap<String, MethodResults> info, Set<String> warns,
			IConstraintsComparator[] comparators) {
		List<SootMethod> superMethods = superClass.getMethods();
		List<SootMethod> subMethods = subClass.getMethods();
		
		for (SootMethod superMethod : superMethods) {
			for (SootMethod subMethod : subMethods) {
				if (superMethod.getName().equals(subMethod.getName())
						&& superMethod != subMethod) {
					try {
						MethodResults superResults = info.get(Constants
								.getJPFSignatuare(superClass, superMethod));
						MethodResults subResults = info.get(Constants
								.getJPFSignatuare(subClass, subMethod));
						
						if  (superResults == null)
							superResults = info.get(superClass.getName() + "." + superMethod.getName());
						
						if  (subResults == null)
							subResults = info.get(subClass.getName() + "." + subMethod.getName());
						
						if (superResults != null && subResults != null) {
							for (IConstraintsComparator comparator : comparators)
								comparator.compare(superResults, subResults,
										warns);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void constantAnalyzer(SootClass superClass, SootClass subClass,
			Set<String> warns) {
		String s_superClass = superClass.getName();
		String s_subClass = subClass.getName();

		Set<String> superMethods = m_info.get(s_superClass);
		Set<String> subMethods = m_info.get(s_subClass);

		// If I know nothing about these methods, then I continue executing
		if (superMethods == null && subMethods == null)
			return;

		if (superMethods == null)
			superMethods = new HashSet<String>();

		if (subMethods == null)
			subMethods = new HashSet<String>();

		analyzeClasses(superClass, superMethods, subClass, subMethods, warns);
	}

	public Set<String> analyze(HashMap<String, MethodResults> info) {
		Set<String> warns = new HashSet<String>();
		Vector<SootClass> sutClasses = new Vector<SootClass>();
		for (SootClass sootClass : Scene.v().getClasses())
			sutClasses.add(sootClass);
		
		for (SootClass superClass : sutClasses) {
			for (SootClass subClass : sutClasses) {
				if (!subClass.hasSuperclass())
					continue;				
				if (subClass.getSuperclass() == superClass) {
					constraintsAnalyzer(superClass, subClass, info, warns,
							new IConstraintsComparator[] {
									new PreconditionConstraintsComparator(),
									new PostconditionConstraintsComparator() });
					constantAnalyzer(superClass, subClass, warns);
				}
			}
		}
		return warns;
	}

	@Override
	public String toString() {
		StringBuffer strbuf = new StringBuffer();
		Iterator<String> it = m_info.keySet().iterator();

		while (it.hasNext()) {
			String className = it.next();
			strbuf.append(className);
			strbuf.append("\n");

			Set<String> methods = m_info.get(className);
			for (String methodName : methods) {
				strbuf.append("  ");
				strbuf.append(methodName);
				strbuf.append("\n");
			}
		}

		return strbuf.toString();
	}

	public static void init() {
		if (instance == null)
			instance = new LSPCheckerAnalyzer();
	}

	public static void push(String className, String methodName) {
		if (instance != null)
			instance.add(className, methodName);
	}

	public static Set<String> doAnalyze(HashMap<String, MethodResults> info) {
		if (instance != null)
			return instance.analyze(info);
		return null;
	}

	public static void debug() {
		System.err.println(instance);
	}
}
