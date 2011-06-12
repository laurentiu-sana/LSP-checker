#!/bin/bash

../../jpf-mercurial/jpf-symbc/bin/jpf \
    +jpf.basedir=../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    +listener=lsp.jpf.listeners.LSPCheckerListener \
    '+symbolic.class=lsp.test.postconditions.InterfacePostconditions,lsp.test.postconditions.SubClassPostconditions,lsp.test.postconditions.SuperClassPostconditions,lsp.test.postconditions.NothingClassPostconditions,lsp.test.postconditions.DummyPostcondition' \
    '+symbolic.method=doWork(sym),postcond9(sym#sym),postcond10(sym#sym)' \
    '+symbolic.fields=instance,static' \
    +search.multiple_errors=true +jpf.report.console.finished= \
    lsp.tests.LSPCheckerGeneratedTestPOSTCONDITIONS 
