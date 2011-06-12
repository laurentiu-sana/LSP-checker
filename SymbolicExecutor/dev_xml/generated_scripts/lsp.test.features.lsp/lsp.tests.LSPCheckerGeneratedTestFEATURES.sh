#!/bin/bash

../../jpf-mercurial/jpf-symbc/bin/jpf \
    +jpf.basedir=../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    +listener=lsp.jpf.listeners.LSPCheckerListener \
    '+symbolic.class=lsp.test.features.MyInterface,lsp.test.features.AdvancedConstructor,lsp.test.features.SubClass,lsp.test.features.SuperClass,lsp.test.features.DeepPreconditions,lsp.test.features.MultiplePostconditionsSuperclass,lsp.test.features.DeepPostconditions,lsp.test.features.MultiplePreconditions,lsp.test.features.MultiplePostconditionsSubclass' \
    '+symbolic.method=preconditions(sym),postconditions(sym),foo(sym#sym#sym#sym),foo(sym),foo(),bar(sym),mainLSPChecker(sym),multiplePostconditions(sym#sym#sym),simpleInterfacePostconditions(sym#sym),simplePostconditions(sym#sym),multiplePreconditions(sym#sym#sym),simpleInterfacePreconditions(sym#sym),simplePreconditions(sym#sym)' \
    '+symbolic.fields=instance,static' \
    +search.multiple_errors=true +jpf.report.console.finished= \
    lsp.tests.LSPCheckerGeneratedTestFEATURES 
