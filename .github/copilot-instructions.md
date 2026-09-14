# Copilot instructions for droid-scroller-boilerplate

## Project purpose

This repository is a command-line-first Android foundation for a local-first spatial card scroller. Product behavior is developed as small, independently verifiable increments from the canonical story file under `docs/` (currently `docs/user-stories-v0.1.yaml`).

Use this repository for the Android implementation itself. Global orchestration, security, permissions, sensitive-data handling, execution gates, and operation-reporting rules are defined by the `j-tonomous` project instructions and policies.

## Toolchain and environment

The current baseline is:

- Gradle Wrapper 8.11.1
- Android Gradle Plugin 8.9.0
- Kotlin 2.2.0
- Jetpack Compose with Material 3
- Android compile SDK 35
- Minimum SDK 28
- JDK 21 for the current local Gradle/Kotlin toolchain

The Android Studio bundled JDK 25 is currently incompatible with this Kotlin/Gradle baseline. Use a compatible JDK through `JAVA_HOME`; do not assume the system `java` command is configured.

For the local environment used during bootstrap:

```bash
export JAVA_HOME="$HOME/.jdks/jbr-21.0.11"
export ANDROID_HOME="$HOME/Android/Sdk"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"
```

Run the environment preflight from the repository root:

```bash
./scripts/check-android-environment.sh
```

Do not commit `local.properties`; it is machine-specific and should point Android tooling at the local SDK.

## Build, test, and lint commands

Use the checked-in Gradle wrapper rather than a system Gradle installation:

```bash
./gradlew tasks
./gradlew test
./gradlew lint
./gradlew assembleDebug
```

Run one JVM test class:

```bash
./gradlew testDebugUnitTest --tests 'com.example.TestClass'
```

Run one test method:

```bash
./gradlew testDebugUnitTest --tests 'com.example.TestClass.testMethod'
```

Run Android instrumentation tests on a connected device or emulator:

```bash
./gradlew connectedDebugAndroidTest
```

Use JVM tests for deterministic domain and application behavior. Use instrumentation or device checks for behavior that depends on Android runtime, Compose rendering, gestures, lifecycle, or physical-device characteristics.

## User-story and TDD workflow

Select work from the canonical story file under `docs/` (currently `docs/user-stories-v0.1.yaml`). Prioritize stories by their declared priority and status, and implement one acceptance criterion or small increment at a time.

Before starting any story or increment:

1. Load all stories marked `status: completed`.
2. Treat their acceptance criteria, dependencies, and documented current-state notes as regression constraints.
3. Identify which completed stories are affected by the planned files or behavior.
4. Preserve those completed behaviors or explicitly record a follow-up defect/story before changing them.
5. Include focused validation for affected completed stories in the implementation plan.

For each increment:

1. Identify the story, acceptance criterion, and smallest useful behavior.
2. Add a failing JVM or Android test, or record a reproducible failing check when a test is not appropriate.
3. Implement the minimum behavior needed to pass.
4. Run the focused test or check.
5. Refactor only after the relevant checks are green.
6. Run broader validation appropriate to the change.
7. Update the story status or increment documentation only when the acceptance criteria are actually satisfied.

Keep automated and device verification distinct. Device validation should not replace the normal JVM TDD loop.

## Architecture and code boundaries

The project is intentionally small and should preserve clear boundaries as it grows:

- `app/src/main/java/com/jtonomous/droidscroller/` contains the Android application and Compose UI.
- Navigation, card sequencing, insertion, deletion, mode transitions, and other business rules should remain JVM-testable and avoid unnecessary Android framework dependencies.
- Android-specific concerns such as activities, Compose rendering, gestures, lifecycle integration, and device behavior should be isolated at the platform boundary.
- Persistence should be accessed through an explicit boundary so domain behavior can use fakes or test doubles.
- Keep the first implementation local-first and account-free; do not add cloud services or external integrations unless a user story explicitly requires them.

The current app is only a launchable Compose shell. Subsequent stories should introduce domain models and state transitions before adding complex spatial effects or platform-specific behavior.

## Android and repository conventions

- Follow the versions and dependency aliases in `gradle/libs.versions.toml`.
- Prefer Kotlin and Compose patterns already established by the project.
- Keep application logic deterministic and independently testable.
- Avoid hard-coded device dimensions; use available layout constraints for responsive presentation.
- Keep Android Studio metadata, build outputs, Gradle caches, and local SDK configuration out of version control.
- Do not copy `local.properties` or machine-specific configuration from reference projects.
- Use the Android Studio and reference projects under the local workspace only as technical references; adapt code and dependencies to this repository rather than coupling the project to those locations.
- Record physical-device findings as follow-up increments or story changes instead of silently working around them.
- Connected instrumentation tests may uninstall the debug application after completion. If the user wants the APK left on a physical device, reinstall and launch the verified debug APK as the final device step; do not interpret the test cleanup as an app crash.
- For persistence work, verify both user content and browsing state (active interest and focused card) across force-stop and relaunch, and surface persistence errors instead of silently falling back.
- Keep spatial presentation rules deterministic and JVM-testable: focused, first-neighbor, and second-neighbor scale/alpha should be derived from relative position and available layout size.

## Scope boundaries

The initial foundation is local-first and intentionally excludes cloud sync, external integrations, telemetry, rich media, alternate layouts, and advanced physics controls. Revisit deferred scope only through an explicit user story or approved increment.
