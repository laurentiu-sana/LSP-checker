#!/bin/bash

../../jpf-mercurial/jpf-symbc/bin/jpf \
    +jpf.basedir=../SymbolicExecutor \
    +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory \
    +listener=lsp.jpf.listeners.LSPCheckerListener \
    '+symbolic.class=lsp.test.accounts.IAccount,lsp.test.accounts.OrdinaryAccount,lsp.test.accounts.PlatinumAccount,lsp.test.accounts.AccountsTest' \
    '+symbolic.method=open(),setDeposit(sym),close(),isOpen(),mainLSPChecker(sym),test(sym#sym)' \
    '+symbolic.fields=instance,static' \
    +search.multiple_errors=true +jpf.report.console.finished= \
    lsp.tests.LSPCheckerGeneratedTestACCOUNTS 
