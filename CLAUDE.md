# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SportTracker is an Android application built with Kotlin. It's a newly initialized project using Android Studio's template with minimal code implementation currently in place.

## Build Configuration

- **Gradle Version**: 8.13
- **Android Gradle Plugin**: 8.13.0
- **Kotlin Version**: 2.0.21
- **Target SDK**: 36
- **Min SDK**: 31
- **Compile SDK**: 36

## Common Commands

### Building
```bash
./gradlew build                 # Build the entire project
./gradlew assembleDebug         # Build debug APK
./gradlew assembleRelease       # Build release APK
./gradlew clean                 # Clean build directory
```

### Testing
```bash
./gradlew test                  # Run all unit tests
./gradlew testDebugUnitTest    # Run unit tests for debug variant
./gradlew connectedAndroidTest  # Run instrumented tests on device/emulator
```

### Code Quality
```bash
./gradlew lint                  # Run Android lint checks
./gradlew lintFix              # Run lint and apply safe fixes
./gradlew check                # Run all checks (lint + tests)
```

### Running the App
```bash
./gradlew installDebug         # Install debug APK on connected device
./gradlew uninstallDebug       # Uninstall debug APK from device
```

## Project Structure

The project follows standard Android application structure:
- **app module**: Main application module at `/app`
- **Source code**: Currently empty, located at `app/src/main/java/com/example/sporttracker/`
- **Tests**: Unit tests in `app/src/test/` and instrumented tests in `app/src/androidTest/`
- **Resources**: Android resources in `app/src/main/res/`
- **Dependencies**: Managed via version catalog at `gradle/libs.versions.toml`

## Key Dependencies

- AndroidX Core KTX
- AndroidX AppCompat
- Material Design Components
- JUnit for unit testing
- Espresso for UI testing

## Development Notes

- The project uses Gradle's version catalog for dependency management
- Java compatibility is set to Java 11
- The app currently has no activities or main entry point defined in the manifest
- ProGuard is disabled for release builds by default