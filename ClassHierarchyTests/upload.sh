#!/bin/bash

DEST=ceata.org:public_html/diploma/iulie
scp -r src/ ${DEST}
scp -r ../SymbolicExecutor/reports ${DEST}
scp -r ../SymbolicExecutor/config ${DEST}

scp -r ../SymbolicExecutor/dev_xml/generated_scripts ${DEST}
scp -r ../SymbolicExecutor/dev_xml/results ${DEST}

