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
- **Storage**: Firebase Storage REST API (firebasestorage.googleapis.com) - Photo upload
- **Backend**: Firebase Firestore REST API (firestore.googleapis.com) - Data storage
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

## Collaborative Development Principles

### Code Quality & Context
- **Meaningful Preservation**: Do not delete comments unless the refactored code makes them strictly redundant. If deleting a comment, briefly explain why in the chat.
- **UI Integrity**: Treat existing user-facing strings (like "Без описания") as intentional. If you see a better way to handle empty states or localization, suggest it as an improvement rather than changing it silently.
- **KMP Best Practices**: Prioritize shared code in `commonMain`. When using platform-specific APIs, ensure they follow the project's established `expect/actual` patterns.

### Intelligent Refactoring
- **Incremental Improvements**: If you see an opportunity to improve Clean Architecture or Koin usage, point it out. We value high-quality code over "just making it work."
- **Model Usage**: Default to Sonnet 4.5 for high-speed, high-quality development. Switch to Opus 4.5 only for deep architectural debugging or complex logic migration.

### Technical Guardrails
- **Sync DI**: Ensure any new components are registered in the appropriate Koin module (`di/`).
- **Domain First**: Keep business logic in Use Cases. Avoid putting complex logic directly into ViewModels or Data Mappers.


## Firebase Storage & Sync

### Data Structure

**Firestore (metadata):**
```
users/{userId}/packages/{noteId}
  - noteId, userId, status
  - massWeight, coalWeight, etc.
  - photoPath: "users/{userId}/packages/{noteId}/photo.jpg"
  - photoUrl: "https://firebasestorage.googleapis.com/.../photo.jpg?token=..."
  - payloadJson: serialized NotePayload
```

**Firebase Storage (files):**
```
users/{userId}/packages/{noteId}/photo.jpg
```

### Sync Workflow

1. User creates note with photo → saved locally (SQLDelight + ImageStorage)
2. User taps "Send to server" → `NoteSyncRepositoryImpl.syncNote()`:
   - Loads photo from local storage (`ImageStorage.loadImage()`)
   - Uploads to Firebase Storage (`FirebaseStorageApi.uploadPhoto()`)
   - Receives `(storagePath, downloadUrl)` pair
   - Sends metadata to Firestore with both paths
   - Marks note as synced in local DB

### Security Rules

Security rules are defined in `FIREBASE_SECURITY_RULES.md`. Key points:
- User-scoped paths: `users/{userId}/...`
- Auth check: `request.auth.uid == userId`
- Applied in Firebase Console (Firestore Rules + Storage Rules)

## Recent Fixes (Commit 4daf20a)

### Photo Display Across Platforms

**Problem:** Photos taken on Android were not displaying on Desktop after server sync because `photoUrl` from Firebase wasn't being saved to local database.

**Solution:**
- `NoteSyncRepositoryImpl.createNoteFromServer()` and `updateNoteFromServer()` now extract `photoUrl` and `photoPath` from Firestore and properly update `payload.biomass`
- `CreateNoteScreen` uses smart logic to display photos:
  - If `photoUrl != null` → use HTTPS URL from Firebase Storage
  - Otherwise → use local file path with `file://` protocol

**CreateNoteScreen photo source selection:**
```kotlin
val photoSource = when {
    photoUrl != null -> photoUrl // Server URL (HTTPS)
    else -> "file://$photoPath"  // Local file
}
```

**Important Note:** Older records (synced before this fix) may have `photoUrl=null` in the database. To fix them, re-save those records on the Android device to upload photos with the new code and generate proper `photoUrl` values.

### Loading Indicators & UI Polish

- Created reusable `LoadingContent` component in `ui/common/LoadingContent.kt` with semi-transparent overlay
- Fixed smart cast issues with nullable `photoPath` in Compose state by using `.let {}` blocks
- State no longer shows old data when opening cards (changed to one-time load instead of continuous Flow subscription)

## Note Status Flow

```
DRAFT → READY_TO_SEND → SENT → APPROVED/REJECTED
```
