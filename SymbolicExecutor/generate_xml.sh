#!/bin/bash

ant

ant generateXML -Dreport.file=reports/lsp.test.accounts.lsp.sort
ant generateXML -Dreport.file=reports/lsp.test.cars.lsp.sort
ant generateXML -Dreport.file=reports/lsp.test.constraints.lsp.sort
ant generateXML -Dreport.file=reports/lsp.test.dummy.lsp.sort
ant generateXML -Dreport.file=reports/lsp.test.numeric.lsp.sort
ant generateXML -Dreport.file=reports/lsp.test.sorting.lsp.sort
ant generateXML -Dreport.file=reports/lsp.test.stateful.lsp.sort
ant generateXML -Dreport.file=reports/lsp.test.stateful.lsp.sort
ant generateXML -Dreport.file=reports/lsp.test.features.lsp.sort
ant generateXML -Dreport.file=reports/lsp.test.jdk.util.lsp.sort

