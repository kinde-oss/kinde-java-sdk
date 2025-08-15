#!/bin/bash

# Script to set the correct Java version for building the Kinde Java SDK
# This project requires Java 17

export JAVA_HOME=/Users/brandtkruger/Library/Java/JavaVirtualMachines/ms-17.0.15/Contents/Home

echo "Setting JAVA_HOME to: $JAVA_HOME"
echo "Java version:"
java -version

echo ""
echo "You can now run Maven commands like:"
echo "  mvn clean compile"
echo "  mvn test"
echo "  mvn package"

