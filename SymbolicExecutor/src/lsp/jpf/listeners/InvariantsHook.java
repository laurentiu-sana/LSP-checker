package lsp.jpf.listeners;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.PUTFIELD;
import gov.nasa.jpf.jvm.bytecode.PUTSTATIC;

import java.util.HashMap;
import java.util.LinkedList;

public class InvariantsHook {
	private static InvariantsHook m_object = new InvariantsHook();

	/** The list of methods with side-effects, per class */
	private HashMap<ClassInfo, LinkedList<MethodInfo>> noSideEffects;

	private InvariantsHook() {
		noSideEffects = new HashMap<ClassInfo, LinkedList<MethodInfo>>();
	}

	private static boolean accept(MethodInfo method) {
		// Count only public non-static methods
		if (!method.isPublic() || method.isStatic() || method.isCtor()
				|| method.isNative())
			return false;

		// The method should have 0 parameters; <this> is an argument
		if (method.getArgumentsSize() > 1)
			return false;

		// The method should return non-void values 
		if (method.getReturnTypeName().equals("void"))
			return false;
		
		if (method.isAbstract())
			return true;
		
		for (Instruction instr : method.getInstructions()) {
			/** Internal fields (member or static) should not be modified */
			if (instr instanceof PUTFIELD || instr instanceof PUTSTATIC)
				return false;

			/**
			 *  Method calls are forbidden
			 * INVOKESPECIAL, INVOKEINTERFACE, INVOKESTATIC, INVOKEVIRTUAL
			 */
			if (instr instanceof InvokeInstruction)
				return false;
		}
		return true;
	}

	private void append(ClassInfo ci, MethodInfo mi) {
		if (!noSideEffects.containsKey(ci))
			noSideEffects.put(ci, new LinkedList<MethodInfo>());
		LinkedList<MethodInfo> list = noSideEffects.get(ci);
		if (!list.contains(mi))
			list.add(mi);
	}

	public void hookImpl(ClassInfo ci) {
		for (MethodInfo mi : ci.getDeclaredMethodInfos()) {
			/** Add only the methods belonging to the current class */
			if (mi.getClassInfo().equals(ci) && accept(mi))
				append(ci, mi);
		}
	}

	public HashMap<ClassInfo, LinkedList<MethodInfo>> getNoSideEffects() {
		return noSideEffects;
	}

	public static InvariantsHook getInstance() {
		return m_object;
	}

	public static void hook(ClassInfo ci) {
		if (m_object != null)
			m_object.hookImpl(ci);
	}
}
