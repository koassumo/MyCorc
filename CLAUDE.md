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

## UI Architecture & Patterns

### Single TopBar Architecture

The app uses a **single Scaffold in MainScreen** that owns TopBar, BottomBar, FAB, and Snackbar. Child screens publish their TopBar configuration through `TopBarState` via CompositionLocal.

**Key components:**
- **`TopBarState`** (`ui/common/TopBarState.kt`) - Reactive state holder with `mutableStateOf` properties
- **`LocalTopBarState`** - `staticCompositionLocalOf` for distributing state down the composition tree
- **Single Scaffold** - MainScreen owns the only Scaffold, child screens are content-only

**Example usage in child screens:**
```kotlin
@Composable
fun DashboardScreen() {
    val topBar = LocalTopBarState.current
    val strings = LocalAppStrings.current

    // Publish TopBar configuration
    topBar.title = strings.dashboardTitle
    topBar.canNavigateBack = false

    // Screen content (no Scaffold)
    Box(Modifier.fillMaxSize()) { ... }
}
```

**Why this pattern:**
- ✅ Single source of truth for TopBar state
- ✅ No nested Scaffolds (avoids window insets issues)
- ✅ Status bar color propagates correctly
- ✅ Centralized FAB management (shown only on specific routes)
- ✅ Easy to test (no Koin needed, just create `TopBarState()`)

### Centralized Dimensions (Dimens.kt)

All UI dimensions are centralized in `ui/common/Dimens.kt`. **Never hardcode `.dp` values** in screens or components.

**Key dimension groups:**
```kotlin
// Screen spacing
val ScreenPaddingSides = 12.dp

// Card styling
val CardCornerRadius = 16.dp
val CardPadding = 16.dp

// Input fields
val InputFieldCornerRadius = 8.dp

// Icons
val IconSizeSmall = 20.dp    // Spinners, trailing icons
val IconSizeLarge = 28.dp    // Section headers

// Common spacing
val SpaceSmall = 8.dp
val SpaceMedium = 16.dp
val SpaceLarge = 24.dp
```

**Usage:**
```kotlin
// ❌ Wrong
Text("Title", modifier = Modifier.padding(16.dp))

// ✅ Correct
Text("Title", modifier = Modifier.padding(Dimens.SpaceMedium))
```

### Reusable Components

**CommonCard** (`ui/common/CommonCard.kt`) - Standard card component used across the app:
```kotlin
CommonCard(
    modifier = Modifier.fillMaxWidth(),
    onClick = { /* handle click */ }
) {
    // Card content
    Text("Title")
    Text("Details")
}
```

Default styling:
- Corner radius: `Dimens.CommonCardCornerRadius` (16.dp)
- Border: 1.dp outline
- Elevation: 0.dp (Material Design 3 + iOS compatibility)
- Content padding: 16.dp

**When to use:**
- ✅ Dashboard items (package cards)
- ✅ Profile card
- ✅ Any clickable/non-clickable content cards
- ❌ Settings menu items (use Material3 `ListItem` instead)

### Localization

All user-facing strings are managed through **Jetpack Compose Resources** (multiplatform string resources).

**Supported languages:**
- English (default) - `values/strings.xml`
- Russian - `values-ru/strings.xml`
- German - `values-de/strings.xml`

**Adding new strings:**
1. Add to interface: `domain/strings/AppStrings.kt`
2. Add to data class: `ui/theme/AppStringsImpl.kt`
3. Add resource mapping: `rememberAppStrings()` in `AppStringsImpl.kt`
4. Add XML entries to all 3 language files: `composeResources/values*/strings.xml`

**Usage in screens:**
```kotlin
@Composable
fun MyScreen() {
    val strings = LocalAppStrings.current
    Text(strings.myStringKey)  // ✅ Correct
    Text("Hardcoded text")      // ❌ Wrong
}
```

**Never hardcode user-facing text.** All text must be in string resources.

### LazyRow/LazyColumn Content Padding

For horizontal scrolling lists (filters, chips), use **`contentPadding`** instead of `padding(horizontal = ...)`:

```kotlin
// ❌ Wrong - creates dead zone on right edge
LazyRow(
    modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingSides)
) { ... }

// ✅ Correct - padding inside scroll area
LazyRow(
    contentPadding = PaddingValues(horizontal = Dimens.ScreenPaddingSides)
) { ... }
```

This ensures:
- First item starts with padding
- Items can scroll under the padding (no dead zone)
- Last item ends with padding

### Navigation & BackHandler

**AppBackHandler** - Cross-platform back button handling (`ui/common/AppBackHandler.kt`):
```kotlin
AppBackHandler(enabled = true) {
    if (currentRoute == Destinations.DASHBOARD) {
        showExitDialog = true
    } else {
        viewModel.navigateBack()
    }
}
```

**Back button priority:**
1. Most recently registered `AppBackHandler` (wins in case of multiple handlers)
2. Settings sub-pages → main settings list
3. Create/Edit screen → Dashboard
4. Other tabs → Dashboard
5. Dashboard → Exit dialog

**Important:** Multiple `AppBackHandler` calls stack - the last one registered has priority. Use `enabled` parameter to conditionally activate handlers.
