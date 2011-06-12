#!/bin/bash

../../jpf-mercurial/jpf-symbc/bin/jpf \
    +jpf.basedir=../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    +listener=lsp.jpf.listeners.LSPCheckerListener \
    '+symbolic.class=lsp.test.dummy.DummyTest,lsp.test.dummy.Rectangle,lsp.test.dummy.Square' \
    '+symbolic.method=computeArea(sym#sym#sym),setWidth(sym),getWidth(),setHeight(sym),getHeight(),getArea()' \
    '+symbolic.fields=instance,static' \
    +search.multiple_errors=true +jpf.report.console.finished= \
    lsp.tests.LSPCheckerGeneratedTestDUMMY 
