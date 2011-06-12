#!/bin/bash

# This is a dummy example:
# 1. we copy the SUT into JPF's test directory
# 2. we run the JPF with symb over a "battery" of tests
# 3. we delete the SUT from the JPF's test directory

cp -r build/classes/* ../javapathfinder-trunk/build/test/

cd scripts
./run_advanced.sh
./run_basic.sh
./run_complex.sh

rm -rf ../javapathfinder-trunk/build/test/*

