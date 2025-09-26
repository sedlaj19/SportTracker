# SportTracker - Project TODO List

## Project Overview
Kotlin Multiplatform (KMP) + Compose Multiplatform (CMP) aplikace pro sledování sportovních aktivit s lokálním (Room) a vzdáleným (Firebase) úložištěm.

## Architecture Status
- ✅ **MVVM Architecture** - Complete with ViewModels, UiState, Events
- ✅ **Repository Pattern** - Single Source of Truth implementation
- ✅ **Clean Architecture** - Domain, Data, Presentation layers
- ✅ **Dependency Injection** - Koin configuration ready
- ✅ **KMP Structure** - Shared business logic, platform-specific implementations

---

## 🏗️ Core Infrastructure

### ✅ Project Setup & Build
- ✅ KMP project structure (shared, composeApp, androidApp modules)
- ✅ Gradle configuration with Kotlin 2.2.20
- ✅ Dependencies setup (Compose, Room, Koin, Firebase, etc.)
- ✅ Build fixes - JVM compatibility, KSP 2.2.20-2.0.3
- ✅ Android APK compilation working

### ✅ Domain Layer
- ✅ SportActivity entity with all required fields
- ✅ StorageType enum (LOCAL, REMOTE)
- ✅ SyncStatus enum for offline-first approach
- ✅ Use cases: GetActivities, SaveActivity, UpdateActivity, DeleteActivity
- ✅ Repository interface with Result-based error handling

### ✅ Data Layer
- ✅ Repository implementation with last-write-wins strategy
- ✅ Room database setup (ActivityEntity, ActivityDao, SportTrackerDatabase)
- ✅ Local data source implementation
- ✅ Mock remote data source (temporary)
- ✅ Data mapping between domain models and entities

### ✅ Presentation Layer
- ✅ ActivityListViewModel with UiState and Events
- ✅ AddEditActivityViewModel with validation
- ✅ UiState models for both screens
- ✅ Event models for navigation and feedback

### ✅ UI Layer
- ✅ ActivityListScreen with FAB, empty state, activity cards
  - ✅ Expandable cards with long-press
  - ✅ Delete confirmation dialogs
  - ✅ Activity type filtering
  - ✅ Activity count in app bar
  - ✅ Refresh button
- ✅ AddEditActivityScreen with form validation
  - ✅ Activity type selection with icons
  - ✅ Auto-fill based on activity type
  - ✅ Enhanced form organization
- ✅ Compose components and Material 3 theming
  - ✅ Custom activity icons based on type
  - ✅ Sync status indicators
  - ✅ Offline indicators
- ✅ Czech localization strings

---

## 🎯 MVP - Version 1.0 (Core Features)

### 🔴 MVP Critical Implementation
- ✅ **Duration picker dialog**
  - ✅ Create time picker component (hours/minutes)
  - ✅ Integrate with AddEditActivityScreen
  - ✅ Replace read-only duration field
  - ✅ Custom NumberPicker with scroll UI
  - ✅ DurationDisplay card component
- ✅ **Landscape mode support** (COMPLETED)
  - ✅ **AddEditActivityScreen landscape layout** (COMPLETED)
    - ✅ Optimized two-column layout: Configuration (35%) | Activity data (65%)
    - ✅ All 8 ActivityTypes accessible with scrollable left panel
    - ✅ Enhanced visual hierarchy with "KONFIGURACE" and "ÚDAJE AKTIVITY" headers
    - ✅ Proper proportions for better space utilization
    - ✅ Consistent scrollable design with ActivityListScreen
  - ✅ **ActivityListScreen landscape layout** (COMPLETED)
    - ✅ Two-column layout: Filter panel (30%) | Activities grid (70%)
    - ✅ Scrollable filter panel with vertical storage and activity type filters
    - ✅ LazyVerticalGrid with 2 columns for activities
    - ✅ All functionality preserved (expand, delete, edit)
  - ✅ **Orientation detection implementation** (COMPLETED)
    - ✅ Proper expect/actual pattern for multiplatform compatibility
    - ✅ Real orientation detection using LocalConfiguration on Android
    - ✅ Seamless transitions between portrait/landscape modes
  - ✅ **Build stability and testing** (COMPLETED)
    - ✅ Android APK builds successfully
    - ✅ All landscape interactions working correctly
    - ✅ State preservation during rotation
    - ✅ Performance optimized for both orientations
- [🔴] **Firebase integration** (NOT IMPLEMENTED - using Mock)
  - ✅ Created FIREBASE_SETUP.md documentation
  - ✅ Added google-services plugin to build files
  - ⚠️ **BLOCKED**: Firebase SDK requires JVM 17 (project uses JVM 11)
  - ❌ FirebaseRemoteDataSource created but removed due to JVM conflicts
  - ❌ FirebaseAuthService created but removed due to JVM conflicts
  - 🟡 Currently using MockRemoteDataSource (in-memory only)
  - 🟡 Currently using MockAuthService (fake user ID)
  - [ ] Upgrade entire project to JVM 17
  - [ ] Restore Firebase implementations from androidMain
  - [ ] Set up actual Firebase project in console
  - [ ] Download real google-services.json
  - [ ] Test actual cloud synchronization
- ✅ **Navigation Setup**
  - ✅ Set up NavHost with screen routes
  - ✅ Implement navigation between ActivityList and AddEdit screens
  - ✅ Handle back navigation and arguments passing
- ✅ **MainActivity implementation**
  - ✅ Create MainActivity with proper Compose setup
  - ✅ Initialize app theme and navigation
  - ✅ Koin DI initialization in Application class

### ✅ MVP Polish & Stability (COMPLETED)
- [🟡] **Form validation improvements**
  - ✅ Basic validation for required fields
  - [ ] Enhanced validation messages
  - [ ] Real-time validation feedback
  - [ ] Prevent multiple submissions
- ✅ **Basic error handling**
  - ✅ Loading states in UI
  - ✅ Network error messages (though network not actually used)
  - ✅ Offline indicators (cosmetic only - no real network features)
  - ✅ Error recovery with retry
  - ✅ NetworkMonitor implementation (monitors network but app doesn't use it)
- ✅ **App startup fixes**
  - ✅ Fixed Room annotation processor crash
  - ✅ Fixed navigation serialization issues
  - ✅ Fixed kotlinx.datetime compatibility
  - ✅ Added missing permissions (ACCESS_NETWORK_STATE)
  - ✅ Proper Koin initialization flow
  - ✅ Basic app stability achieved

---What 

## 🚀 POST-MVP BACKLOG (Version 2.0+)
*Following features will be implemented AFTER MVP is complete and stable*

### 🟢 Enhanced User Experience
- [🟡] **Advanced activity management features**
  - ✅ Delete functionality with confirmation dialog (from list view)
  - [ ] **Delete functionality in edit screen** - add delete button/action to AddEditActivityScreen
  - [ ] Swipe-to-delete gesture
  - [ ] Pull-to-refresh implementation
  - [ ] Activity search by name/location
- [🟡] **Activity filtering system**
  - ✅ Filter by activity type (dynamic)
  - ✅ Filter by storage type (Local/Remote)
  - [ ] **Empty state for filtered results** - placeholder when filters result in no activities
    - [ ] "No activities match your current filters" message
    - [ ] Suggestions to adjust filters or add new activities
    - [ ] Different messages for specific filter combinations (e.g., "No local activities found")
  - [ ] Date range filtering
  - [ ] Activity duration filtering
- [ ] **Activity details screen**
  - [ ] Detailed activity view with full information
  - [ ] Edit from detail view
  - [ ] Activity sharing functionality

### 🟢 Advanced Data Features
- [🟡] **Activity categories/types**
  - ✅ Predefined activity types (běh, cyklistika, plavání, etc.)
  - ✅ Category-specific icons and colors
  - ✅ Activity type selection in AddEdit screen
  - [ ] Custom category creation
- [ ] **Activity statistics & analytics**
  - [ ] Weekly/monthly summaries
  - [ ] Time tracking graphs and charts
  - [ ] Personal records tracking
  - [ ] Progress visualization
- [ ] **Advanced sync features**
  - [ ] Conflict resolution improvements
  - [ ] Sync status indicators in UI
  - [ ] Background sync optimization
  - [ ] Export/import functionality

### 🟢 UI/UX Polish & Advanced Features
- [ ] **Material 3 theming enhancements**
  - [ ] Custom color scheme
  - [ ] Dark/light theme support
  - [ ] Dynamic color (Material You)
- [ ] **Advanced animations & micro-interactions**
  - [ ] Skeleton loading animations
  - [ ] Success feedback animations
  - [ ] Smooth transitions between screens
- [ ] **Accessibility improvements**
  - [ ] Content descriptions
  - [ ] Screen reader support
  - [ ] High contrast support
  - [ ] Large text support

### 🟢 Performance & Quality
- [ ] **Performance optimization**
  - [ ] Image loading optimization
  - [ ] Database query optimization
  - [ ] Memory leak prevention
- [ ] **Advanced error handling**
  - [ ] Error state illustrations
  - [ ] Retry mechanisms
  - [ ] Graceful degradation
- [ ] **Deep linking support**
  - [ ] Direct links to activities
  - [ ] Share functionality integration

### 🟢 Platform Expansion (Future)
- [ ] **iOS support completion**
  - [ ] iOS-specific data sources
  - [ ] iOS native UI components
  - [ ] iOS app store deployment
- [ ] **Desktop support (optional)**
  - [ ] Desktop Compose UI adaptations
  - [ ] Desktop-specific features

---

## 🧪 Testing & Quality

### 🟢 Testing Implementation
- [ ] **Unit tests**
  - [ ] ViewModel tests with fake repositories
  - [ ] Use case tests with mock dependencies
  - [ ] Repository tests with Room testing database
- [ ] **Integration tests**
  - [ ] End-to-end activity creation flow
  - [ ] Data synchronization testing
  - [ ] Navigation flow testing
- [ ] **UI tests**
  - [ ] Compose UI tests for key user flows
  - [ ] Screenshot tests for visual regression

### 🟢 Code Quality
- [ ] **Static analysis**
  - [ ] Lint checks and fixes
  - [ ] Code formatting consistency
  - [ ] Performance optimization
- [ ] **Documentation**
  - [ ] API documentation
  - [ ] Architecture documentation
  - [ ] Setup and deployment guides

---

## 🚀 Deployment & DevOps

### 🟢 Build & Release
- [ ] **CI/CD pipeline**
  - [ ] GitHub Actions for automated builds
  - [ ] Automated testing on PR
  - [ ] Release automation
- [ ] **App distribution**
  - [ ] Play Store preparation
  - [ ] App signing configuration
  - [ ] Release notes and versioning

---

## 📋 Current Status Summary

**✅ Completed (Major Milestones):**
- Complete KMP architecture with MVVM pattern
- Room database with working annotation processing
- Compose UI screens with Material 3 and enhanced UI
- Build system fully functional
- Core business logic implemented
- Local data storage fully working
- Mock remote services (NOT real Firebase)

**🔴 Next Critical Steps (MVP Focus):**
1. ✅ Implement duration picker dialog
2. ✅ Complete MainActivity and navigation setup
3. ✅ Basic stability and error handling
4. ❌ Firebase integration (BLOCKED by JVM 17 requirement)
   - App currently uses Mock services only
   - "Remote" storage option saves locally (fake)
   - No actual cloud synchronization

**⚠️ Note:** App crash fixes moved to MVP Polish phase - focusing on core features first

**📊 Progress: ~90% MVP Features Complete** (Landscape mode fully implemented and polished)

**⚠️ IMPORTANT**: Firebase integration is NOT working. The app uses Mock services only. All data is stored locally even when "Remote" option is selected.

**🔴 NEXT FOCUS**: Firebase integration (blocked by JVM 17 requirement) or moving to POST-MVP enhancements.

---

*Last updated: 2025-01-26*

## Current App Status

### ✅ What Works:
- Creating, editing, deleting activities
- Local Room database storage
- UI with filters, icons, and animations
- Navigation between screens
- Error handling and offline indicators
- **Complete landscape mode support** for both screens
- Responsive design with proper orientation detection
- Scrollable panels and optimized layouts

### ❌ What Doesn't Work:
- Firebase cloud synchronization (using Mock only)
- Remote storage (saves locally even when "Remote" selected)
- User authentication (fake user ID "mock_user_123")
- Cross-device data sync

### 🟡 Technical Debt:
- Need JVM 17 upgrade for Firebase support
- Firebase code exists but removed due to compatibility
- google-services.json is just example file
*This file tracks the overall project progress and should be updated as tasks are completed.*