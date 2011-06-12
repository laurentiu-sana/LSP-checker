#!/bin/bash

./deploy.sh

function test_case {
    CONFIG=config/$1.xml
    rm -rf $CONFIG

    SCRIPT=dev_xml/generated_scripts/$1/*.sh
    rm -rf $SCRIPT

    RESULTS=dev_xml/results/$2*
    rm -rf $RESULTS

    ant generateXML -Dreport.file=reports/$1.sort
    ant run -Dconfig.file=config/$1.xml
}

#test_case 'lsp.test.savadvanced.lsp' 'lsp.tests.LSPCheckerGeneratedTestSAVADVANCED'

test_case 'lsp.test.savdemoprecond.lsp' 'lsp.tests.LSPCheckerGeneratedTestSAVDEMOPRECOND'
#test_case 'lsp.test.savdemopostcond.lsp' 'lsp.tests.LSPCheckerGeneratedTestSAVDEMOPOSTCOND'

#test_case 'lsp.test.accounts.lsp' 'lsp.tests.LSPCheckerGeneratedTestACCOUNTS'
#test_case 'lsp.test.org.jdom.lsp' 'lsp.tests.LSPCheckerGeneratedTestJDOM'
exit

#test_case 'lsp.test.accounts.lsp' 'lsp.tests.LSPCheckerGeneratedTestACCOUNTS'
#test_case 'lsp.test.constraints.lsp' 'lsp.tests.LSPCheckerGeneratedTestCONSTRAINTS'
#test_case 'lsp.test.dummy.lsp' 'lsp.tests.LSPCheckerGeneratedTestDUMMY'
#test_case 'lsp.test.features.lsp' 'lsp.tests.LSPCheckerGeneratedTestFEATURES'
#test_case 'lsp.test.postconditions.lsp' 'lsp.tests.LSPCheckerGeneratedTestPOSTCONDITIONS'
#test_case 'lsp.test.savadvanced.lsp' 'lsp.tests.LSPCheckerGeneratedTestSAVADVANCED'
#test_case 'lsp.test.sav.lsp' 'lsp.tests.LSPCheckerGeneratedTestSAV'
#test_case 'lsp.test.stateful.lsp' 'lsp.tests.LSPCheckerGeneratedTestSTATEFUL'
#test_case 'lsp.test.stateless.lsp' 'lsp.tests.LSPCheckerGeneratedTestSTATELESS'


exit

# TODO: ActiveMQ
echo 'Run ActiveMQ'
ant generateXML -Dreport.file=reports/org.apache.lsp.sort
ant run -Dconfig.file=config/org.apache.lsp.xml

exit

echo 'Run JDK'
ant generateXML -Dreport.file=reports/lsp.test.jdk.util.lsp.sort
ant run -Dconfig.file=config/lsp.test.jdk.util.lsp.xml

