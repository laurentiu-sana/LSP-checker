#!/bin/bash
../javapathfinder-trunk-svn/bin/jpf \
    +jpf.basedir=../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    '+symbolic.method=allocateArray(sym#sym#sym#sym)' \
    +search.multiple_errors=true +jpf.report.console.finished= \
    lsp.test.numeric.NumericTest