# SportTracker - Task Completion Checklist

## After Making Code Changes

### 1. Build Verification
```bash
./gradlew build                # Full project build
./gradlew assembleDebug        # Android APK compilation
```

### 2. Code Quality Checks
```bash
./gradlew lint                 # Run lint analysis
./gradlew lintFix             # Auto-fix lint issues where possible
```

### 3. Testing (when available)
```bash
./gradlew test                 # Run unit tests
./gradlew testDebugUnitTest   # Run debug unit tests
```

### 4. Manual Verification
- Test on Android device/emulator
- Verify both portrait and landscape modes
- Check navigation flows between screens
- Test form validation and error states

### 5. Git Operations
```bash
git status                     # Check modified files
git diff                      # Review changes
git add .                     # Stage changes
git commit -m "description"   # Commit with clear message
```

## Special Considerations
- **Room Changes**: Run `./gradlew clean` before build when changing entities
- **Firebase**: Verify google-services.json is present (template is in place)
- **KMP**: Test changes don't break shared module compilation
- **Orientation**: Always verify landscape mode functionality

## Critical Areas to Test
- Activity creation and editing forms
- Data persistence (local and remote options)
- Activity list filtering and display
- Navigation between screens