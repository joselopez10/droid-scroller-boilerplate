# droid-scroller-boilerplate

Command-line-first Android foundation for the spatial card scroller.

## Prerequisites

- Android Studio with a compatible JDK configured through `JAVA_HOME` (JDK 21 is available in the local environment; the bundled JDK 25 is not currently compatible with the Kotlin/Gradle baseline).
- Android SDK platform 35 and build tools 35.0.0.
- Gradle is provided by the checked-in wrapper; a system Gradle installation is not required.

For the local Android Studio installation used during development:

```bash
export JAVA_HOME="$HOME/.jdks/jbr-21.0.11"
export ANDROID_HOME="$HOME/Android/Sdk"
export PATH="$ANDROID_HOME/platform-tools:$PATH"
```

Do not commit `local.properties`; Android tooling can generate it for the local SDK location.

Check the local prerequisites before invoking Gradle:

```bash
./scripts/check-android-environment.sh
```

## Command-line verification

From the repository root:

```bash
./gradlew tasks
./gradlew test
./gradlew assembleDebug
```

Run a single JVM test class:

```bash
./gradlew testDebugUnitTest --tests 'com.jtonomous.droidscroller.BootstrapTest'
```

The first functional increment intentionally provides only a launchable Compose shell. Product behavior is implemented in subsequent user-story increments.