#!/usr/bin/env bash
set -euo pipefail

# Usage: build-and-test.sh [path-to-maven-settings.xml]
MAVEN_SETTINGS=${1:-}

echo "Running mvn clean verify"
if [[ -n "${MAVEN_SETTINGS}" ]]; then
  mvn -B -s "${MAVEN_SETTINGS}" clean verify
else
  mvn -B clean verify
fi

echo "Running checkstyle"
# If checkstyle is configured in the POM, this will run as part of verify. Optional extra checks can be added here.

echo "Build and tests completed"
