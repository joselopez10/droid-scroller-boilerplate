#!/usr/bin/env bash

set -euo pipefail

fail() {
    printf 'error: %s\n' "$1" >&2
    exit 1
}

[[ -n "${JAVA_HOME:-}" ]] || fail "JAVA_HOME is not set"
[[ -x "$JAVA_HOME/bin/java" ]] || fail "JAVA_HOME does not point to an executable JDK"
[[ -n "${ANDROID_HOME:-}" ]] || fail "ANDROID_HOME is not set"
[[ -d "$ANDROID_HOME/platforms/android-35" ]] || fail "Android SDK platform android-35 is not installed"
[[ -x "./gradlew" ]] || fail "run this command from the project root with an executable ./gradlew"

printf 'Java: '
"$JAVA_HOME/bin/java" -version 2>&1 | head -1
printf 'Android SDK: %s\n' "$ANDROID_HOME"
printf 'Compile SDK: android-35\n'
printf 'Gradle wrapper: available\n'
