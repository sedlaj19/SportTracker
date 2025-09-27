# SportTracker - Suggested Commands

## Build Commands
```bash
./gradlew build                 # Build the entire project
./gradlew assembleDebug         # Build debug APK
./gradlew assembleRelease       # Build release APK
./gradlew clean                 # Clean build directory
```

## Testing Commands
```bash
./gradlew test                  # Run all unit tests
./gradlew testDebugUnitTest    # Run unit tests for debug variant
./gradlew connectedAndroidTest  # Run instrumented tests on device/emulator
```

## Code Quality Commands
```bash
./gradlew lint                  # Run Android lint checks
./gradlew lintFix              # Run lint and apply safe fixes
./gradlew check                # Run all checks (lint + tests)
```

## Development Commands
```bash
./gradlew installDebug         # Install debug APK on connected device
./gradlew uninstallDebug       # Uninstall debug APK from device
```

## System Commands (macOS/Darwin)
```bash
ls -la                         # List files with details
find . -name "*.kt"           # Find Kotlin files
grep -r "searchterm" src/     # Search in source code
cd path/to/directory          # Change directory
git status                    # Check git status
git diff                      # Show changes
```

## Project-Specific Notes
- Use `./gradlew` (not `gradle`) for consistent builds
- KSP annotation processing requires clean builds when changing Room entities
- Firebase integration is implemented but needs real project setup (google-services.json)