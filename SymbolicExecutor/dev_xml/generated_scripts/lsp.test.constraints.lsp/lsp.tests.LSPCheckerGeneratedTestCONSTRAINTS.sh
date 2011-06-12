#!/bin/bash

../../jpf-mercurial/jpf-symbc/bin/jpf \
    +jpf.basedir=../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    +listener=lsp.jpf.listeners.LSPCheckerListener \
    '+symbolic.class=lsp.test.constraints.SubClass,lsp.test.constraints.ConstraintsTest,lsp.test.constraints.SuperClass' \
    '+symbolic.method=preconditions(sym),postconditions(sym),invariants(sym),constantMethod(),mainLSPChecker(sym)' \
    '+symbolic.fields=instance,static' \
    +search.multiple_errors=true +jpf.report.console.finished= \
    lsp.tests.LSPCheckerGeneratedTestCONSTRAINTS 
