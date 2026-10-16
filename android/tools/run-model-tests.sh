#!/bin/sh
set -eu
OUT="build/model-tests"
rm -rf "$OUT"
mkdir -p "$OUT"

javac -d "$OUT" \
  app/src/main/java/com/campuslf/app/model/FoundItem.java \
  tools/ModelSmokeTest.java

java -cp "$OUT" ModelSmokeTest
