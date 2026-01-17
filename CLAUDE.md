# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MyCorc is a Kotlin Multiplatform (KMP) application for managing biomass and coal packaging/shipments. It targets **Android**, **iOS**, and **Desktop (JVM)** using Compose Multiplatform for shared UI.

## Build Commands

### Android
```shell
# Build debug APK
.\gradlew.bat :composeApp:assembleDebug

# Install on connected device
.\gradlew.bat :composeApp:installDebug
```

### Desktop (JVM)
```shell
# Run desktop application
.\gradlew.bat :composeApp:run

# Package for distribution
.\gradlew.bat :composeApp:packageMsi   # Windows
.\gradlew.bat :composeApp:packageDmg   # macOS
.\gradlew.bat :composeApp:packageDeb   # Linux
```

### iOS
```shell
# Compile iOS framework
.\gradlew.bat :composeApp:compileKotlinIosSimulatorArm64

# Open in Xcode for full build/run
# Open ./iosApp directory in Xcode
```

### Tests
```shell
# Run common tests
.\gradlew.bat :composeApp:allTests

# Run specific platform tests
.\gradlew.bat :composeApp:testDebugUnitTest      # Android
.\gradlew.bat :composeApp:desktopTest            # Desktop
.\gradlew.bat :composeApp:iosSimulatorArm64Test  # iOS Simulator
```

## Architecture

The project follows **Clean Architecture** with three layers:

```
UI Layer (Screens + ViewModels)
        ↓
Domain Layer (Use Cases + Interfaces)
        ↓
Data Layer (Repositories + API/DB)
```

### Source Structure
- `composeApp/src/commonMain/` - Shared code for all platforms
- `composeApp/src/androidMain/` - Android-specific implementations
- `composeApp/src/iosMain/` - iOS-specific implementations
- `composeApp/src/desktopMain/` - Desktop/JVM-specific implementations

### Key Directories (under `commonMain/kotlin/org/igo/mycorc/`)
- `core/` - Cross-cutting concerns (TimeProvider, error handling)
- `data/` - Repository implementations, API clients, mappers, DTOs
- `domain/` - Business models, repository interfaces, use cases
- `di/` - Koin dependency injection modules
- `ui/` - Compose screens, ViewModels, theme, navigation

### Platform-Specific Patterns
The project uses `expect/actual` declarations for platform differences:
- **HttpClient engines**: OkHttp (Android), Darwin (iOS), CIO (Desktop)
- **SQLDelight drivers**: AndroidSqliteDriver, NativeSqliteDriver, JdbcSqliteDriver
- **Settings storage**: SharedPreferences (Android), NSUserDefaults (iOS), Java Preferences (Desktop)
- **Image storage**: Platform-specific file handling

## Key Technologies

- **DI**: Koin 4.0.0 - See `di/` directory for module organization
- **Database**: SQLDelight 2.0.2 - Schema in `commonMain/sqldelight/org/igo/mycorc/db/Note.sq`
- **Networking**: Ktor Client 3.0.0 - Platform-specific engines
- **Auth**: Firebase REST API (identitytoolkit.googleapis.com)
- **State Management**: StateFlow with `MutableStateFlow` + `.asStateFlow()` pattern

## Database Schema

SQLDelight generates type-safe queries from `Note.sq`. Custom column adapters handle:
- `NoteStatus` enum ↔ TEXT
- `NotePayload` data class ↔ JSON TEXT
- Boolean ↔ INTEGER

## Dependency Injection

Koin modules are organized in `di/`:
- `AppModule.kt` - Entry point, initializes all modules
- `DataModule.kt` - Repositories, API clients, database
- `DomainModule.kt` - Use cases (all as `factory`)
- `UiModule.kt` - ViewModels (all as `viewModelOf`)
- `PlatformModule.kt` - Platform-specific implementations (`expect/actual`)

## Note Status Flow

```
DRAFT → READY_TO_SEND → SENT → APPROVED/REJECTED
```
