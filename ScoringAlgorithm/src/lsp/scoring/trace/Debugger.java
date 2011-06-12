package lsp.scoring.trace;

import java.util.HashMap;
import java.util.Iterator;

import lsp.scoring.ClassInfo;
import lsp.scoring.Constants;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.util.Chain;

public class Debugger {
	private static boolean m_xml = false;
	private static XMLExporter m_export;

	public static void dumpToXML(boolean op) {
		m_xml = op;
	}

	public static void initXML(String testPackage) {
		m_export = new XMLExporter(Constants.DEBUG_FOLDER + "/" + testPackage
				+ ".xml");
	}

	public static void finishXML() {
		if (m_xml)
			m_export.close();
	}

	public static void dumpField(SootField field) {
		Tracer.log("  " + field.getName());
		Tracer.log("    modifiers: " + Modifier.toString(field.getModifiers()));
		Tracer.log("    type: " + field.getType());
	}

	public static void dumpMethod(SootMethod method) {
		Tracer.log("  " + method.getName());
		Tracer
				.log("    modifiers: "
						+ Modifier.toString(method.getModifiers()));
		Tracer.log("    return type: " + method.getReturnType());
		Tracer.log("    parameter types: " + method.getParameterTypes());

		try {
			Tracer.log(method.getActiveBody().toString());
		} catch (Exception e) {
		}

		Tracer.log("    " + method.getDavaDeclaration());
	}

	public static void dumpClass(SootClass sootClass) {
		Tracer.log("{" + sootClass.getName() + ", superclass "
				+ sootClass.getSuperclass().getName() + "} "
				+ sootClass.getFieldCount() + " fields, "
				+ sootClass.getMethodCount() + " methods");
		for (SootField field : sootClass.getFields())
			dumpField(field);

		for (SootMethod method : sootClass.getMethods())
			dumpMethod(method);
	}

	public static void dumpClass(SootClass sootClass,
			HashMap<SootClass, Double> scores,
			HashMap<SootClass, ClassInfo> info) {
		m_export.down("class");
		m_export.write("class", "name", sootClass.getName());

		if (scores.containsKey(sootClass))
			m_export.write("score", "value", scores.get(sootClass) + "");
		else
			m_export.write("score", "value", "0");

		String superClassName = "[nothing]";
		try {
			superClassName = sootClass.getSuperclass().getName();
		} catch (Exception e) {

		}
		m_export.write("superclass", "name", superClassName);

		ClassInfo classInfo = info.get(sootClass);

		m_export.down("subclasses");
		if (classInfo != null) {
			Iterator<SootClass> it = classInfo.getSubClasses().iterator();

			while (it.hasNext())
				m_export.write("class", "name", it.next().getName());
		}
		m_export.up();
		m_export.up();
	}

	/**
	 * Dumps the classes that matches given regex
	 */
	public static void dumpClasses(String regex,
			HashMap<SootClass, Double> scores,
			HashMap<SootClass, ClassInfo> info) {
		Chain<SootClass> classes = Scene.v().getClasses();
		Iterator<SootClass> it = classes.iterator();

		while (it.hasNext()) {
			SootClass currentClass = it.next();
			boolean ok = false;

			/**
			 * Check if current class's name matches regex.
			 */
			if (regex == null)
				ok = true;
			else
				ok = currentClass.getName().matches(regex);

			if (ok) {
				if (m_xml)
					dumpClass(currentClass, scores, info);
				else
					dumpClass(currentClass);
			}
		}
	}

	public static void dumpClassesNumber(int size) {
		if (m_xml) {
			m_export.write("enviroment", "classes", size + "");
		} else
			Tracer.log("Loaded " + size + " classes");
	}
}
