package lsp.scoring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import lsp.scoring.trace.Tracer;

import org.apache.log4j.Logger;

import soot.Scene;
import soot.SootClass;

/**
 * Traverses a class hierarchy and computes the initial
 * score for each loaded class:
 *   score(C) = 1 + max{score(S) where S is a subclass of C},
 *              and 1 if there is no such S
 * For implementation details, see HierarchyTraverse.computeSubClassScore()
 */
public class HierarchyTraverse {
	private final static Logger LOGGER = Logger
			.getLogger(HierarchyTraverse.class);

	private HashSet<SootClass> m_classes;
	private HashMap<SootClass, ClassInfo> m_info;
	private HashMap<SootClass, Double> m_scores;

	private void addInfo(SootClass superClass, SootClass currentClass) {
		if (m_info.containsKey(superClass)) {
			ClassInfo cInfo = m_info.get(superClass);
			cInfo.addSubClass(currentClass);
		} else {
			ClassInfo cInfo = new ClassInfo(superClass);
			cInfo.addSubClass(currentClass);
			m_info.put(superClass, cInfo);
		}
	}

	public HierarchyTraverse(String regex) {
		LOGGER.info("Starting hierarchy traverse with regex: <" + regex + ">");
		m_info = new HashMap<SootClass, ClassInfo>();
		m_classes = new HashSet<SootClass>();
		m_scores = new HashMap<SootClass, Double>();

		/**
		 * Normalize information
		 */
		for (SootClass currentClass : Scene.v().getClasses()) {
			/**
			 * If the current class has no super class or it is not an interface, we ignore it
			 */
			if (!currentClass.hasSuperclass() && !currentClass.isInterface())
				continue;

			/**
			 * Drop the classes/interfaces from other packages then those given by the user
			 */
			if (!currentClass.getName().matches(regex))
				continue;

			SootClass superClass = currentClass.getSuperclass();
			m_classes.add(currentClass);

			/**
			 * Add the link: superclass <--> subclass
			 */
			addInfo(superClass, currentClass);

			/**
			 * Add the links: subclass <--> interfaces
			 */
			for (SootClass interfaceClass : currentClass.getInterfaces())
				addInfo(interfaceClass, currentClass);

			/**
			 * Add the current class to m_info
			 */
			if (!m_info.containsKey(currentClass))
				m_info.put(currentClass, new ClassInfo(currentClass));
		}
	}

	public void computeScores() {
		Iterator<SootClass> it = m_classes.iterator();

		while (it.hasNext()) {
			SootClass sClass = it.next();

			if (!m_classes.contains(sClass.getSuperclass()))
				computeSubClassScore(sClass);
		}
	}

	public HashMap<SootClass, Double> getScores() {
		return m_scores;
	}

	private Double computeSubClassScore(SootClass currentClass) {
		ClassInfo currentInfo = m_info.get(currentClass);

		if (currentInfo.getSubClasses().size() == 0) {
			LOGGER.info("[basic scoring] class " + currentClass
					+ " has no subclasses and scores 1.0");
			m_scores.put(currentClass, 1.0);
			return 1.0;
		}

		Double max = 0.0, sum = 1.0, aux;

		Iterator<SootClass> it = currentInfo.getSubClasses().iterator();
		while (it.hasNext()) {
			SootClass itClass = it.next();
			aux = computeSubClassScore(itClass);
			sum += aux;
			if (aux > max) {
				// Save the parent (the class with maximum score)
				max = aux;
			}
		}

		boolean add = false;
		if (m_scores.containsKey(currentClass)) {
			// New score is higher
			if (m_scores.get(currentClass) <= max)
				add = true;
		} else
			add = true;

		max += 1.0;
		if (add) {
			LOGGER.info("[basic scoring] class " + currentClass + " scores "
					+ max);
			m_scores.put(currentClass, max);
		}

		return max;
	}

	public void debug() {
		Iterator<SootClass> it = m_classes.iterator();

		while (it.hasNext()) {
			SootClass sClass = it.next();
			ClassInfo sInfo = m_info.get(sClass);

			Tracer.log(sClass.getName());

			Tracer.log("    Score: " + m_scores.get(sClass));
			if (sInfo == null)
				continue;

			Iterator<SootClass> sIt = sInfo.getSubClasses().iterator();
			while (sIt.hasNext()) {
				Tracer.log("    " + sIt.next().toString());
			}
		}
	}

	public HashMap<SootClass, ClassInfo> getInfo() {
		return m_info;
	}
}
