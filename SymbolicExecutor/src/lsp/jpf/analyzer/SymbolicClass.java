package lsp.jpf.analyzer;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.MethodInfo;

import java.util.LinkedList;
import java.util.List;

import lsp.jpf.parse.Constants;

import org.apache.log4j.Logger;

public class SymbolicClass {
	private final static Logger LOGGER = Logger.getLogger(SymbolicClass.class);

	private ClassInfo ci;
	private List<ConstantMethod> list;

	public SymbolicClass(ClassInfo ci) {
		this.ci = ci;
		list = new LinkedList<ConstantMethod>();
	}

	public List<ConstantMethod> computeConstantMethods() {
		return computeConstantMethods(null);
	}

	private static void addToList(List<ConstantMethod> cMethods,
			ConstantMethod cm, String action) {
		boolean add = true;
		for (ConstantMethod cMethod : cMethods) {
			if (cMethod.toString().equals(cm.toString())) {
				add = false;
				break;
			}
		}
		if (add) {
			LOGGER.debug("[iteration] method " + cm + " " + action);
			cMethods.add(cm);
		}
	}

	/** Return the constant methods visible from given ClassInfo. 
	 * If null parameter passed, then it returns all the visible ConstantMethods.
	 */
	public List<ConstantMethod> computeConstantMethods(ClassInfo ci) {
		/** Banned methods */
		List<ConstantMethod> fbMethods = new LinkedList<ConstantMethod>();

		/** Constant methods */
		List<ConstantMethod> ctMethods = new LinkedList<ConstantMethod>();

		LOGGER.debug("[computeConstantMethods] class " + ci);

		for (MethodInfo mi : ci.getDeclaredMethodInfos()) {
			ConstantMethod cm = new ConstantMethod(mi);
			if (cm.isConstant()) {
				LOGGER.debug("[computeConstantMethods] method " + cm
						+ " initially constant");
				list.add(cm);
			} else {
				LOGGER.debug("[computeConstantMethods] method " + cm
						+ " banned");
				fbMethods.add(cm);
			}
		}

		/** Checking methods in iterations */
		int i = 0, oldLength;
		List<ConstantMethod> lazyCtMethods = new LinkedList<ConstantMethod>();
		List<ConstantMethod> lazyFbMethods = new LinkedList<ConstantMethod>();

		while (i < Constants.MAX_DEPTH) {
			LOGGER.debug("[iteration] " + i);
			oldLength = fbMethods.size();
			for (ConstantMethod cm : list) {
				/** Check basic invoke functionality */
				if (cm
						.isConstant(list, fbMethods, lazyCtMethods,
								lazyFbMethods)) {
					/** Check dispatching possibilities */
					if (cm.isOOPConstant(list, fbMethods, lazyCtMethods,
							lazyFbMethods))
						addToList(ctMethods, cm, "constant");
					else
						addToList(fbMethods, cm, "OOP banned");
				} else
					addToList(fbMethods, cm, "banned");
			}

			ctMethods.addAll(lazyCtMethods);
			fbMethods.addAll(lazyFbMethods);

			/** Finish the iterations if nothing else added in the current step */
			if (oldLength >= fbMethods.size())
				break;

			/** Put the methods into the new list */
			list.clear();
			for (ConstantMethod cm : ctMethods)
				list.add(cm);
			ctMethods.clear();
			lazyCtMethods.clear();
			lazyFbMethods.clear();
			i++;
		}

		if (ci != null) {
			List<ConstantMethod> filter = new LinkedList<ConstantMethod>();
			for (ConstantMethod cm : ctMethods) {
				if (cm.getMethodClassName().equals(ci.getName()))
					addToList(filter, cm, "constant");
				else {
					/**
					 * It's possible to find methods that are not reported
					 * (direct called) and thus lost, so we hook here
					 */
					ConstantAnalyser.addSymbolicMethod(cm);
				}
			}
			return filter;
		}
		return ctMethods;
	}

	public ClassInfo getClassInfo() {
		return ci;
	}

	@Override
	public String toString() {
		return ci.toString();
	}
}
