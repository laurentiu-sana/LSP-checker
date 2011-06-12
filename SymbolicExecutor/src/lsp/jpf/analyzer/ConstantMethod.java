package lsp.jpf.analyzer;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.bytecode.INVOKEINTERFACE;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.PUTFIELD;
import gov.nasa.jpf.jvm.bytecode.PUTSTATIC;
import gov.nasa.jpf.symbc.bytecode.INVOKESPECIAL;
import gov.nasa.jpf.symbc.bytecode.INVOKESTATIC;
import gov.nasa.jpf.symbc.bytecode.INVOKEVIRTUAL;

import java.util.HashSet;
import java.util.List;

import lsp.jpf.listeners.LSPCheckerListener;
import lsp.jpf.parse.Constants;

import org.apache.log4j.Logger;

/**
 * A constant method is equivalent with <public foo(): const> from C++.
 * We use this to approximate the invariants of a class; there methods are
 * actually a subset of the invariants.
 */
public class ConstantMethod {
	private final static Logger LOGGER = Logger.getLogger(ConstantMethod.class);
	private static final boolean ALLOW_INVOKESPECIAL = true;
	private MethodInfo mi;
	private HashSet<ClassInfo> possibleInstances;

	private int phases;
	private String buffer[] = null;

	public ConstantMethod(MethodInfo mi) {
		this.mi = mi;
		possibleInstances = new HashSet<ClassInfo>();
		phases = 1;
		buffer = new String[phases];
		for (int i = 0; i < phases; i++)
			buffer[i] = null;
	}

	public MethodInfo getMethodInfo() {
		return mi;
	}

	public ClassInfo getClassInfo() {
		return mi.getClassInfo();
	}

	public String getMethodClassName() {
		return mi.getClassInfo().getName();
	}

	private boolean isConstantImpl() {
		if (mi.isCtor() || mi.isNative())
			return false;

		// The method should return non-void values 
		if (mi.getReturnTypeName().equals("void"))
			return false;
		
		if (mi.getInstructions() == null)
			return false;

		for (Instruction instr : mi.getInstructions()) {
			/** Internal fields (member or static) should not be modified */
			if (instr instanceof PUTFIELD || instr instanceof PUTSTATIC)
				return false;
		}
		return true;
	}

	public boolean isConstant() {
		/**
		 * This is a buffering scheme, because symbolic execution
		 * runs the code multiple times
		 */
		if (buffer[0] == null) {
			boolean result = isConstantImpl();
			buffer[0] = result ? "true" : "false";
			return result;
		}
		return buffer[0].equals("true") ? true : false;
	}

	/** Checks if a method is constant after the first filter stage */
	private boolean contains(List<ConstantMethod> ctMethods, MethodInfo mi) {
		for (ConstantMethod cm : ctMethods) {
			/** XXX: JPF is doesn't override the equals() method */
			if (cm.mi.getFullName().equals(mi.getFullName())) {
				return true;
			}
		}
		return false;
	}

	private boolean contains(List<ConstantMethod> fbMethods,
			String methodFullName) {
		for (ConstantMethod cm : fbMethods) {
			if (cm.mi.getFullName().equals(methodFullName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isParent(ClassInfo ci, ClassInfo pi) {
		if (ci.getName().equals(pi.getName()))
			return false;

		if (ci.getSuperClass() == null)
			return false;

		// Direct inheritance
		if (ci.getSuperClass().getName().equals(pi.getName()))
			return true;

		// Interfaces implementation
		for (String ii : ci.getInterfaces())
			if (ii.equals(pi.getName()))
				return true;

		return false;
	}

	private void dfs_travel(HashSet<ClassInfo> possibleInstances, ClassInfo ci) {
		for (ClassInfo lc : ClassInfo.getLoadedClasses()) {
			if (isParent(lc, ci)) {
				possibleInstances.add(lc);
				dfs_travel(possibleInstances, lc);
			}
		}
	}

	private void analyzeClassHierarchy(String invokedMethodClassName,
			String invokedMethodName, List<ConstantMethod> ctMethods,
			List<ConstantMethod> fbMethods, List<ConstantMethod> lazyCtMethods,
			List<ConstantMethod> lazyFbMethods) {
		/** If have possible instances then we computed them before */
		if (Constants.ALLOW_CLASS_LOAD && possibleInstances.size() > 0)
			return;

		ClassInfo ii = ClassInfo.getClassInfo(invokedMethodClassName);

		possibleInstances.add(ii);
		dfs_travel(possibleInstances, ii);

		// We analyze the whole hierarchy so recursive lookup is not necessary
		for (ClassInfo ci : possibleInstances) {
			for (MethodInfo mi : ci.getDeclaredMethodInfos()) {
				// Skip abstract methods (maybe declared in interfaces)
				if (mi.isAbstract())
					continue;

				if (mi.getUniqueName().equals(invokedMethodName))
					addLazyConstantMethod(mi, ctMethods, fbMethods,
							lazyCtMethods, lazyFbMethods);
			}
		}
	}

	private void addLazyConstantMethod(MethodInfo mi,
			List<ConstantMethod> ctMethods, List<ConstantMethod> fbMethods,
			List<ConstantMethod> lazyCtMethods,
			List<ConstantMethod> lazyFbMethods) {
		if (mi == null)
			return;

		if (!contains(ctMethods, mi) && !contains(fbMethods, mi)) {
			ConstantMethod cm = new ConstantMethod(mi);
			if (cm.isConstant()) {
				LOGGER.debug("[isConstant] method " + cm + " lazy constant");
				lazyCtMethods.add(cm);

				int argsSize = mi.getArgumentsSize();
				if (!mi.isStatic())
					argsSize--;
				boolean isSymbolic = LSPCheckerListener.isSymbolic(
						Constants.GLOBAL_CONFIGURATION, mi.getClassName(), mi,
						mi.getName(), argsSize);
				if (isSymbolic)
					ConstantAnalyser.addSymbolicClass(mi.getClassInfo());
			} else {
				LOGGER.debug("[isConstant] method " + cm + " lazy banned");
				lazyFbMethods.add(cm);
			}
		}
	}

	public boolean isConstant(List<ConstantMethod> ctMethods,
			List<ConstantMethod> fbMethods, List<ConstantMethod> lazyCtMethods,
			List<ConstantMethod> lazyFbMethods) {
		for (Instruction instr : mi.getInstructions()) {
			/**
			 *  method calls are forbidden
			 * INVOKESPECIAL, INVOKEINTERFACE, INVOKESTATIC, INVOKEVIRTUAL
			 */
			if (instr instanceof INVOKESPECIAL) {
				/** 
				 * Example of special invoke: toString(); we think
				 * that Java bases classes respects the LSP
				 */

				String className = null;
				String methodName = null;
				MethodInfo mi = null;

				INVOKESPECIAL invks = (INVOKESPECIAL) instr;
				className = invks.getInvokedMethodClassName();
				methodName = invks.getInvokedMethodName();
				mi = invks.getInvokedMethod();

				if (isSuperClass(mi.getClassInfo(), this.mi.getClassInfo())) {
					addLazyConstantMethod(mi, ctMethods, fbMethods,
							lazyCtMethods, lazyFbMethods);

					/** Remove some constant methods */
					if (contains(ctMethods, this.mi)
							&& contains(fbMethods, className + "." + methodName))
						return false;
				}

				if (!ALLOW_INVOKESPECIAL)
					return false;
			} else if (instr instanceof INVOKEINTERFACE) {
				/** We have no idea about how's going to be called so we drop this method */
				INVOKEINTERFACE invki = (INVOKEINTERFACE) instr;
				String className = invki.getInvokedMethodClassName();
				String methodName = invki.getInvokedMethodName();

				analyzeClassHierarchy(className, methodName, ctMethods,
						fbMethods, lazyCtMethods, lazyFbMethods);

				/** Remove some constant methods */
				if (contains(ctMethods, this.mi)
						&& contains(fbMethods, className + "." + methodName))
					return false;
			} else if (instr instanceof INVOKEVIRTUAL
					|| instr instanceof INVOKESTATIC) {
				String className = null;
				String methodName = null;
				MethodInfo mi = null;

				if (instr instanceof INVOKEVIRTUAL) {
					INVOKEVIRTUAL invkv = (INVOKEVIRTUAL) instr;
					className = invkv.getInvokedMethodClassName();
					methodName = invkv.getInvokedMethodName();
					mi = invkv.getInvokedMethod();
				} else {
					INVOKESTATIC invks = (INVOKESTATIC) instr;
					className = invks.getInvokedMethodClassName();
					methodName = invks.getInvokedMethodName();
					mi = invks.getInvokedMethod();
				}

				addLazyConstantMethod(mi, ctMethods, fbMethods, lazyCtMethods,
						lazyFbMethods);

				/** Remove some constant methods */
				if (contains(ctMethods, this.mi)
						&& contains(fbMethods, className + "." + methodName))
					return false;
			}
		}

		return true;
	}

	public boolean isOOPConstant(List<ConstantMethod> ctMethods,
			List<ConstantMethod> fbMethods, List<ConstantMethod> lazyCtMethods,
			List<ConstantMethod> lazyFbMethods) {
		/** All the possible methods should be constant! */
		String methodFullName = this.mi.getFullName();
		for (Instruction instr : mi.getInstructions()) {
			String invokedMethodName = null;
			if (instr instanceof INVOKEINTERFACE) {
				INVOKEINTERFACE invki = (INVOKEINTERFACE) instr;
				invokedMethodName = invki.getInvokedMethodName();
			} else if (instr instanceof INVOKESPECIAL) {
				INVOKESPECIAL invks = (INVOKESPECIAL) instr;
				invokedMethodName = invks.getInvokedMethodName();
			} else if (instr instanceof INVOKEVIRTUAL) {
				INVOKEVIRTUAL invkv = (INVOKEVIRTUAL) instr;
				invokedMethodName = invkv.getInvokedMethodName();
			}

			if (invokedMethodName != null) {
				for (ClassInfo ci : possibleInstances) {
					for (MethodInfo mi : ci.getDeclaredMethodInfos()) {
						// Skip abstract methods (maybe declared in interfaces)
						if (mi.isAbstract())
							continue;

						String currentInvkMethodName = ci.getName() + "."
								+ invokedMethodName;

						if (mi.getUniqueName().equals(invokedMethodName)) {
							/** The code is scattered for performance reasons */
							boolean inFb = contains(fbMethods,
									currentInvkMethodName);

							if (inFb) {
								/** 
								 * If we find a banned method, then we ban it
								 */
								if (contains(ctMethods, methodFullName)) {
									LOGGER.debug("Invoking method "
											+ currentInvkMethodName
											+ " banned " + methodFullName);
									return false;
								}
								if (contains(lazyCtMethods, methodFullName)) {
									LOGGER.debug("Invoking method "
											+ currentInvkMethodName
											+ " banned " + methodFullName);
									return false;
								}
							}

							boolean inLazyFb = contains(lazyFbMethods,
									currentInvkMethodName);
							if (inLazyFb) {
								/** I'm not pretty sure that javac will optimize this */
								if (contains(ctMethods, methodFullName)) {
									LOGGER.debug("Invoking method "
											+ currentInvkMethodName
											+ " banned " + methodFullName);
									return false;
								}
								if (contains(lazyCtMethods, methodFullName)) {
									LOGGER.debug("Invoking method "
											+ currentInvkMethodName
											+ " banned " + methodFullName);
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	private boolean isSuperClass(ClassInfo parent, ClassInfo child) {
		if (parent.getName().equals(Object.class.getName())
				|| child.getName().equals(Object.class.getName()))
			return false;

		if (parent.getName().equals(child.getName()))
			return true;

		// Climb in the hierarchy
		return isSuperClass(parent, child.getSuperClass());
	}

	public boolean isInvariantSubset() {
		// Non-public methods are not subset of the invariants
		return mi.isPublic();
	}

	@Override
	public String toString() {
		return mi.getFullName();
	}
}
