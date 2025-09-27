# SportTracker Project Overview

## Purpose
SportTracker je Android aplikace postavená na Kotlin Multiplatform (KMP) s Compose Multiplatform (CMP) pro sledování sportovních aktivit. Aplikace umožňuje:
- Zaznamenávání sportovních výkonů (název, místo, doba trvání)
- Ukládání do lokální databáze (Room) nebo vzdálené (Firebase)
- Výpis aktivit s filtrováním podle typu úložiště
- Podporu portrait i landscape módu

## Tech Stack
- **Platform**: Kotlin Multiplatform (KMP)
- **UI Framework**: Compose Multiplatform (CMP)
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Koin 4.1.1
- **Local Database**: Room 2.8.1
- **Remote Backend**: Firebase (Firestore + Auth)
- **Navigation**: Jetpack Navigation Compose
- **Build System**: Gradle 8.13 with Kotlin 2.2.20
- **Target SDK**: 36, Min SDK: 31

## Modules Structure
- **shared**: Společná business logika (domain, data, presentation layers)
- **composeApp**: UI komponenty a screens (Compose Multiplatform)
- **androidApp**: Android-specific code (MainActivity, Application, DI)

## Key Features Implemented
- ✅ Complete MVVM architecture
- ✅ Room database with KSP
- ✅ Firebase integration (ready, needs real project)
- ✅ Landscape/portrait mode support
- ✅ Duration picker with custom NumberPicker
- ✅ Activity type selection with icons
- ✅ Material 3 theming and Czech localization