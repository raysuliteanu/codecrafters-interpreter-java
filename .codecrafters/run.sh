#!/bin/sh
#
# This script is used to run your program on CodeCrafters
#
# This runs after .codecrafters/compile.sh
#
# Learn more: https://codecrafters.io/program-interface

set -e # Exit on failure

exec java -ea --enable-preview -jar /tmp/codecrafters-build-interpreter-java/codecrafters-interpreter.jar "$@"
