#!/bin/bash

../../javapathfinder-trunk-svn/bin/jpf \
    +jpf.basedir=../../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    +jpf.listener=gov.nasa.jpf.symbc.SymbolicListener \
    '+symbolic.method=doTest(sym#sym)' \
    +search.multiple_errors=true \
    +jpf.report.console.finished= \
    lsp.jpf.test.MyDummyTest

