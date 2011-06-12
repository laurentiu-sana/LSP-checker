#!/bin/bash

../../javapathfinder-trunk-svn/bin/jpf \
    -show \
    +jpf.basedir=../../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    '+symbolic.method=doTest(sym#sym)' \
    +search.multiple_errors=true \
    +jpf.report.console.finished= \
    lsp.jpf.test.BasicSymbcTest

