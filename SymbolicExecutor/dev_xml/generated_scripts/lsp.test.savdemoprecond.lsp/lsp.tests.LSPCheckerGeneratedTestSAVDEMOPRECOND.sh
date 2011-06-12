#!/bin/bash

../../jpf-mercurial/jpf-symbc/bin/jpf \
    +jpf.basedir=../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    +listener=lsp.jpf.listeners.LSPCheckerListener \
    '+symbolic.class=lsp.test.savdemoprecond.MyDummyImplementation2,lsp.test.savdemoprecond.MyDummyImplementation1,lsp.test.savdemoprecond.SAVSubClass,lsp.test.savdemoprecond.MyDummyInterface,lsp.test.savdemoprecond.SAVSuperClass' \
    '+symbolic.method=doWork(),check(sym),precond3(sym),precond1(sym),precond2(sym#sym)' \
    '+symbolic.fields=instance,static' \
    +search.multiple_errors=true +jpf.report.console.finished= \
    lsp.tests.LSPCheckerGeneratedTestSAVDEMOPRECOND 
