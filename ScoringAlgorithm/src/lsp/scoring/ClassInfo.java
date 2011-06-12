package lsp.scoring;

import java.util.HashSet;

import soot.SootClass;

/**
 * Contains the information about a class:
 *  - reference to a SootClass instance
 *  - subclasses on this class
 */
public class ClassInfo {
	private SootClass m_class;
	private HashSet<SootClass> subClasses;

	public ClassInfo(SootClass sClass) {
		m_class = sClass;
		subClasses = new HashSet<SootClass>();
	}

	public SootClass getSootClass() {
		return m_class;
	}

	public void addSubClass(SootClass sClass) {
		subClasses.add(sClass);
	}

	public HashSet<SootClass> getSubClasses() {
		return subClasses;
	}

	@Override
	public String toString() {
		return "Current class " + m_class + " " + subClasses.toString();
	}
}
