#!/bin/bash

../../jpf-mercurial/jpf-symbc/bin/jpf \
    +jpf.basedir=../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    +listener=lsp.jpf.listeners.LSPCheckerListener \
    '+symbolic.class=lsp.test.stateful.DummyImplementation6,lsp.test.stateful.DummyInterface,lsp.test.stateful.DummySubClass,lsp.test.stateful.DummyImplementation5,lsp.test.stateful.DummyImplementation4,lsp.test.stateful.DummyConstants,lsp.test.stateful.DummyImplementation3,lsp.test.stateful.DummySuperClass,lsp.test.stateful.DummyImplementation2,lsp.test.stateful.DummyImplementation1,lsp.test.stateful.DummyBaseInterface' \
    '+symbolic.method=preconditions(sym),getResults(),preconditions(sym#sym),postconditions(sym#sym),bar(sym),foo(sym#sym),mainLSPChecker(sym),postconditions(sym),getConfiguration(),getValue(sym),computeScore(sym#sym),doSomething(),inv7(sym),inv8(sym),privatePreconditions(sym),testPreconditions(sym),isAlive(),isAlive(sym#sym),getMember(),isSet(),inv1(),inv2(),inv3(),inv4(),inv5(),inv6(),bar(),foo()' \
    '+symbolic.fields=instance,static' \
    +search.multiple_errors=true +jpf.report.console.finished= \
    lsp.tests.LSPCheckerGeneratedTestSTATEFUL 
