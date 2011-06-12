#!/bin/bash

../../jpf-mercurial/jpf-symbc/bin/jpf \
    +jpf.basedir=../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    +listener=lsp.jpf.listeners.LSPCheckerListener \
    '+symbolic.class=lsp.test.savdemo.DummyImplementation1,lsp.test.savdemo.SAVSuperClass,lsp.test.savdemo.SAVSubClass,lsp.test.savdemo.DummyImplementation2,lsp.test.savdemo.MyDummyImplementation2,lsp.test.savdemo.MyDummyImplementation1,lsp.test.savdemo.SAVInterface,lsp.test.savdemo.MyDummyInterface' \
    '+symbolic.method=check(sym),doWork(),precond3(sym),precond1(sym),precond2(sym#sym)' \
    '+symbolic.fields=instance,static' \
    +search.multiple_errors=true +jpf.report.console.finished= \
    lsp.tests.LSPCheckerGeneratedTestSAVDEMO 
