#!/bin/bash

../../javapathfinder-trunk-svn/bin/jpf \
    +jpf.basedir=../../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    '+symbolic.method=myMethod1()' \
    +search.multiple_errors=true \
    +jpf.report.console.finished= \
    lsp.jpf.test.ComplexSymbcTest

