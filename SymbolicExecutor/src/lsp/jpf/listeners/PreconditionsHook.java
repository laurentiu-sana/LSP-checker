package lsp.jpf.listeners;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.bytecode.INVOKEINTERFACE;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.symbc.bytecode.INVOKESPECIAL;
import gov.nasa.jpf.symbc.bytecode.INVOKESTATIC;
import gov.nasa.jpf.symbc.bytecode.INVOKEVIRTUAL;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lsp.jpf.analyzer.ConstantMethod;

public class PreconditionsHook {
	private static PreconditionsHook m_object;

	// class -> methods -> list of exceptions
	private HashMap<ClassInfo, HashMap<MethodInfo, List<PathCondition>>> exceptionsPC;

	// method -> called methods; used in preconditions inheritance
	private HashMap<MethodInfo, HashSet<MethodInfo>> calledMethods;

	// method -> virtual methods; used in preconditions inheritance from the
	// polymorphic methods
	private HashMap<MethodInfo, HashMap<PathCondition, HashSet<MethodInfo>>> virtualCalledMethods;

	private PreconditionsHook() {
		exceptionsPC = new HashMap<ClassInfo, HashMap<MethodInfo, List<PathCondition>>>();
		calledMethods = new HashMap<MethodInfo, HashSet<MethodInfo>>();
		virtualCalledMethods = new HashMap<MethodInfo, HashMap<PathCondition, HashSet<MethodInfo>>>();
	}

	public void hookImpl(MethodInfo mi, PathCondition pc) {
		ClassInfo ci = mi.getClassInfo();
		HashMap<MethodInfo, List<PathCondition>> map;
		List<PathCondition> mpc = null;

		if (exceptionsPC.containsKey(ci)) {
			map = exceptionsPC.get(ci);
			if (map.containsKey(mi))
				mpc = map.get(mi);
			else {
				mpc = new LinkedList<PathCondition>();
				map.put(mi, mpc);
			}
		} else {
			mpc = new LinkedList<PathCondition>();
			map = new HashMap<MethodInfo, List<PathCondition>>();
			map.put(mi, mpc);
			exceptionsPC.put(ci, map);
		}
		mpc.add(pc);
	}

	private void setCallLinkImpl(MethodInfo caller, MethodInfo callee) {
		if (caller.getFullName().equals(callee.getFullName()))
			return;
		m_addImpl(m_getImpl(calledMethods, caller), callee);
	}

	private void m_addImpl(HashSet<MethodInfo> list, MethodInfo callee) {
		Iterator<MethodInfo> it = list.iterator();
		while (it.hasNext()) {
			MethodInfo mi = it.next();
			if (mi.getFullName().equals(callee.getFullName()))
				return;
		}
		list.add(callee);
	}

	private HashSet<MethodInfo> m_getImpl(
			HashMap<MethodInfo, HashSet<MethodInfo>> calledMethods,
			MethodInfo caller) {
		Iterator<MethodInfo> it = calledMethods.keySet().iterator();
		while (it.hasNext()) {
			MethodInfo mi = it.next();
			if (mi.getFullName().equals(caller.getFullName()))
				return calledMethods.get(mi);
		}
		HashSet<MethodInfo> list = new HashSet<MethodInfo>();
		calledMethods.put(caller, list);
		return list;
	}

	public HashMap<ClassInfo, HashMap<MethodInfo, List<PathCondition>>> getExceptionsPC() {
		return exceptionsPC;
	}

	public HashMap<MethodInfo, HashSet<MethodInfo>> getCalledMethods() {
		return calledMethods;
	}

	public HashMap<MethodInfo, HashMap<PathCondition, HashSet<MethodInfo>>> getVirtualCalledMethods() {
		return virtualCalledMethods;
	}

	public static PreconditionsHook getInstance() {
		if (m_object == null)
			m_object = new PreconditionsHook();
		return m_object;
	}

	public static void hook(MethodInfo mi, PathCondition pc) {
		getInstance().hookImpl(mi, pc);
	}

	public static void setCallLink(MethodInfo caller, MethodInfo callee) {
		getInstance().setCallLinkImpl(caller, callee);
	}

	public static void addVirtualInvocation(Instruction insn, PathCondition pc) {
		getInstance().addVirtualInvocationImpl(insn, pc);
	}

	private static void addVirtualCalledMethod(HashSet<MethodInfo> set,
			MethodInfo mi) {
		boolean add = true;

		for (MethodInfo cm : set)
			if (cm.getFullName().equals(mi.getFullName())) {
				add = false;
				break;
			}

		if (add && !mi.isAbstract())
			set.add(mi);
	}

	private void dfs_travel(HashSet<MethodInfo> possibleInstances, MethodInfo mi) {
		for (ClassInfo lc : ClassInfo.getLoadedClasses()) {
			if (ConstantMethod.isParent(lc, mi.getClassInfo())) {
				MethodInfo lm = null;
				lm = lc.getMethod(mi.getUniqueName(), false);
				if (lm == null)
					continue;
				addVirtualCalledMethod(possibleInstances, lm);
				dfs_travel(possibleInstances, lm);
			}
		}
	}

	private void addVirtualInvocationImpl(Instruction insn, PathCondition pc) {
		HashSet<MethodInfo> possibleMethods = new HashSet<MethodInfo>();
		MethodInfo caller = null;
		MethodInfo mi = null;

		if (insn instanceof INVOKEVIRTUAL) {
			INVOKEVIRTUAL invkv = (INVOKEVIRTUAL) insn;
			mi = invkv.getInvokedMethod();
			caller = invkv.getMethodInfo();
		} else if (insn instanceof INVOKEINTERFACE) {
			INVOKEINTERFACE invki = (INVOKEINTERFACE) insn;
			mi = invki.getInvokedMethod();
			caller = invki.getMethodInfo();
		} else if (insn instanceof INVOKESPECIAL) {
			INVOKESPECIAL invki = (INVOKESPECIAL) insn;
			mi = invki.getInvokedMethod();
			caller = invki.getMethodInfo();
		} else if (insn instanceof INVOKESTATIC) {
			INVOKESTATIC invki = (INVOKESTATIC) insn;
			mi = invki.getInvokedMethod();
			caller = invki.getMethodInfo();
		}
		else
			throw new RuntimeException("Preconditions hook");

		/** main(String[] args) should not contain polymorphic methods */
		if (mi == null || caller == null
				|| caller.getUniqueName().equals("main([Ljava/lang/String;)V"))
			return;

		addVirtualCalledMethod(possibleMethods, mi);
		dfs_travel(possibleMethods, mi);

		if (!containsMethod(virtualCalledMethods, caller)) {
			HashMap<PathCondition, HashSet<MethodInfo>> info = new HashMap<PathCondition, HashSet<MethodInfo>>();
			info.put(pc, possibleMethods);
			virtualCalledMethods.put(caller, info);
		} else {
			HashMap<PathCondition, HashSet<MethodInfo>> info = getMethod(
					virtualCalledMethods, caller);
			info.put(pc, possibleMethods);
		}
	}

	private HashMap<PathCondition, HashSet<MethodInfo>> getMethod(
			HashMap<MethodInfo, HashMap<PathCondition, HashSet<MethodInfo>>> virtualCalledMethods,
			MethodInfo caller) {
		Iterator<MethodInfo> it = virtualCalledMethods.keySet().iterator();
		while (it.hasNext()) {
			MethodInfo mi = it.next();
			if (mi.getUniqueName().equals(caller.getUniqueName())
					&& mi.getClassInfo().getName()
							.equals(caller.getClassInfo().getName()))
				return virtualCalledMethods.get(mi);
		}
		return null;
	}

	private boolean containsMethod(
			HashMap<MethodInfo, HashMap<PathCondition, HashSet<MethodInfo>>> virtualCalledMethods,
			MethodInfo caller) {
		Iterator<MethodInfo> it = virtualCalledMethods.keySet().iterator();
		while (it.hasNext()) {
			MethodInfo mi = it.next();
			if (mi.getUniqueName().equals(caller.getUniqueName())
					&& mi.getClassInfo().getName()
							.equals(caller.getClassInfo().getName()))
				return true;
		}
		return false;
	}

}
