#!/bin/bash

# Deploys binaries in the JPF
ant
rm -rf ../javapathfinder-trunk-svn/build/test/*
cp -r build/classes/* ../javapathfinder-trunk-svn/build/test/

# Already in CLASSPATH
#cp -r tests/classes/* ../javapathfinder-trunk-svn/build/test

