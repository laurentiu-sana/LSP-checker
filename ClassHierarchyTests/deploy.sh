#!/bin/bash

TEST_DIR="input"

ant

rm -rf ../ScoringAlgorithm/${TEST_DIR}/*

cp -r build/*  ../ScoringAlgorithm/${TEST_DIR}

#ant clean

