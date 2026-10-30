#!/bin/sh
set -eu
ROOT=$(CDPATH= cd "$(dirname "$0")/.." && pwd)
OUT="$ROOT/build/model-tests"
mkdir -p "$OUT"
javac -encoding UTF-8 -d "$OUT" \
  "$ROOT/app/src/main/java/pt/isep/dssmv/campuslf/model/FoundItem.java" \
  "$ROOT/tools/ModelSmokeTest.java"
java -cp "$OUT" ModelSmokeTest
