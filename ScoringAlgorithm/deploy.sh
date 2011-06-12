#!/bin/bash

ant compile-tests -Dg.tests.dir=results -Dg.results.dir=results/build

# SUT
rm -rf ../SymbolicExecutor/tests/*
cp -r input/* ../SymbolicExecutor/tests

# Generated test cases
mkdir -p ../SymbolicExecutor/tests/generated_tests
cp -r results/build/* ../SymbolicExecutor/tests/generated_tests

# Delete the .svn folder
rm -rf ../SymbolicExecutor/tests/generated_tests/*/.svn
rm -rf ../SymbolicExecutor/tests/generated_tests/*/*/.svn

# Sorting results
cp -r results/*.sort  ../SymbolicExecutor/reports
