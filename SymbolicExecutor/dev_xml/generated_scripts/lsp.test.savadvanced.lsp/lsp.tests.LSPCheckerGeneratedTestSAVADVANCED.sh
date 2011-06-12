#!/bin/bash

../../jpf-mercurial/jpf-symbc/bin/jpf \
    +jpf.basedir=../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    +listener=lsp.jpf.listeners.LSPCheckerListener \
    '+symbolic.class=lsp.test.savadvanced.MyDummyInterface,lsp.test.savadvanced.Superclass,lsp.test.savadvanced.MyDummyImplementation1,lsp.test.savadvanced.MyDummyImplementation2,lsp.test.savadvanced.Subclass' \
    '+symbolic.method=doWork(),check(sym),precond(sym)' \
    '+symbolic.fields=instance,static' \
    +search.multiple_errors=true +jpf.report.console.finished= \
    lsp.tests.LSPCheckerGeneratedTestSAVADVANCED 
