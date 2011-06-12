#!/bin/bash

../../jpf-mercurial/jpf-symbc/bin/jpf \
    +jpf.basedir=../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    +listener=lsp.jpf.listeners.LSPCheckerListener \
    '+symbolic.class=lsp.test.savdemopostcond.SuperClass,lsp.test.savdemopostcond.SuperClassPostconditions,lsp.test.savdemopostcond.SubClass,lsp.test.savdemopostcond.DummyPostcondition,lsp.test.savdemopostcond.NothingClassPostconditions,lsp.test.savdemopostcond.InterfacePostconditions,lsp.test.savdemopostcond.SubClassPostconditions' \
    '+symbolic.method=postcond9(sym#sym),postcond10(sym#sym),postcond(sym),postcond1(sym),postcond2(sym#sym),postcond3(sym#sym),postcond4(sym#sym),postcond5(sym#sym),postcond6(sym),getStaticMember(),postcond7(sym),doSomething(sym),postcond8(sym),doWork(sym)' \
    '+symbolic.fields=instance,static' \
    +search.multiple_errors=true +jpf.report.console.finished= \
    lsp.tests.LSPCheckerGeneratedTestSAVDEMOPOSTCOND 
