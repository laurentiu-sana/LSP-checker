#!/bin/bash

../../jpf-mercurial/jpf-symbc/bin/jpf \
    +jpf.basedir=../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    +listener=lsp.jpf.listeners.LSPCheckerListener \
    '+symbolic.class=lsp.test.stateless.DummySubClass,lsp.test.stateless.DummySuperClass' \
    '+symbolic.method=preconditions(sym),postconditions(sym#sym),invariants(sym),isSet(),getMember(),isSetFake(),mainLSPChecker2(sym),mainLSPChecker(sym)' \
    '+symbolic.fields=instance,static' \
    +search.multiple_errors=true +jpf.report.console.finished= \
    lsp.tests.LSPCheckerGeneratedTestSTATELESS 
