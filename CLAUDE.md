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

### Workflow & Permissions
- **NEVER run the application** (`.\gradlew.bat :composeApp:run`) without explicit user request
- **NEVER create git commits** unless explicitly asked by the user
- **NEVER push to remote** unless explicitly asked by the user
- The user handles app execution, commits, and pushes manually - focus on code changes only

### Code Quality & Context
- **Meaningful Preservation**: Do not delete comments unless the refactored code makes them strictly redundant. If deleting a comment, briefly explain why in the chat.
- **UI Integrity**: Treat existing user-facing strings (like "Без описания") as intentional. If you see a better way to handle empty states or localization, suggest it as an improvement rather than changing it silently.
- **KMP Best Practices**: Prioritize shared code in `commonMain`. When using platform-specific APIs, ensure they follow the project's established `expect/actual` patterns.
- **Minimize Color Hardcoding**: Avoid using explicit `Color()` values in components. All colors should be managed through `Color.kt` → `Theme.kt` → `MaterialTheme.colorScheme` or `CompositionLocal` for custom colors.

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

### Color System & Theming

#### Architecture (цепочка цветов)

**Стандартные цвета Material3:**
```
Color.kt (определение цвета)  →  Theme.kt (привязка к роли Material3)  →  MaterialTheme.colorScheme  →  Компонент
Пример: LightPrimary = Red500  →  primary = LightPrimary              →  .primary                   →  Button, FAB
```

**Кастомные цвета (не входят в Material3 ColorScheme):**
```
Color.kt (определение цвета)  →  Theme.kt (CompositionLocal)  →  LocalCustomXxx.current  →  Компонент
Пример: LightTopBarBackground = Grey100  →  LocalCustomTopBarBackground  →  CommonTopBar
```

Для дефолтных тем Material3 кастомные цвета получают `Color.Unspecified`, и компонент использует дефолт Material3.

#### Key Files

- **`ui/theme/Color.kt`** - Определение всех цветов. Палитра Material + семантические цвета для Light/Dark тем + кастомные цвета (секция "Custom Colors").
- **`ui/theme/Theme.kt`** - Привязка цветов из Color.kt к ролям Material3 (`primary`, `surface`, `background` и т.д.). Содержит 4 схемы: кастомная Light/Dark + дефолтная Material3 Light/Dark. Также содержит `CompositionLocal` для кастомных цветов (`LocalCustomTopBarBackground`).

#### Rules

1. **Никогда не устанавливай цвета в коде компонентов.** Все цвета должны управляться через `Color.kt` → `Theme.kt` → `MaterialTheme`. Компоненты Material3 автоматически используют правильные цвета из темы.

```kotlin
// ❌ Wrong - хардкод цвета в компоненте
containerColor = Color(0xFFFF9800)

// ❌ Wrong - переопределение дефолта Material3 (избыточно)
containerColor = MaterialTheme.colorScheme.primary  // Button и так использует primary

// ✅ Correct - не указывать цвет, Material3 сам подставит дефолт
Button(onClick = { ... }) { Text("Click") }
```

2. **Для изменения цвета — меняй только `Color.kt`.** Если нужен другой цвет TopBar, кнопок, FAB — меняй соответствующую переменную в `Color.kt` (например, `LightPrimary = Blue500`). Не трогай код компонентов.

3. **Используй Material3 роли только по назначению.** Не переопределяй `tertiary` для TopBar — если позже добавишь Switch, он тоже станет этим цветом.

4. **Для нестандартных цветов — используй паттерн CompositionLocal.** Если нужен цвет, не вписывающийся ни в одну роль Material3:
   - Определи варианты в секции "Custom Colors" в `Color.kt` (для Light/Dark тем)
   - Создай `staticCompositionLocalOf` в `Theme.kt`
   - Провайди значение в `MyAppTheme` через `CompositionLocalProvider`
   - Для дефолтных тем Material3 используй `Color.Unspecified` (компонент сам подставит дефолт)
   - В компоненте проверяй: если `!= Color.Unspecified` → используй кастомный, иначе → Material3 дефолт

```kotlin
// ❌ Wrong - хардкод кастомного цвета в компоненте
containerColor = Orange500

// ❌ Wrong - переопределение стандартной роли Material3 для другой цели
tertiary = Orange500  // Это сломает Switch, Checkbox и другие tertiary-компоненты

// ✅ Correct - CompositionLocal для кастомных цветов
// Color.kt:
val LightTopBarBackground = Grey100
val DarkTopBarBackground = Grey900

// Theme.kt:
val LocalCustomTopBarBackground = staticCompositionLocalOf { Color.Unspecified }
// В MyAppTheme: CompositionLocalProvider(LocalCustomTopBarBackground provides topBarBg)

// CommonTopBar.kt:
val customBg = LocalCustomTopBarBackground.current
colors = if (customBg != Color.Unspecified) {
    TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = customBg)
} else {
    TopAppBarDefaults.centerAlignedTopAppBarColors() // Material3 дефолт
}
```

5. **Dynamic Color (Material You) отключен.** Системные обои пользователя не влияют на цвета приложения. Не добавляй `dynamicColorScheme()`.

6. **В `Color.kt` не используй `Color(0x...)` для семантических цветов.** Все цвета берутся из палитры, определённой в начале файла (например, `Red500`, `Grey800`).

#### Material3 Color Roles (дефолты)

Комментарии в `Color.kt` и `Theme.kt` описывают, какие компоненты Material3 используют каждую роль **по умолчанию**:

| Роль | Дефолтное использование |
|---|---|
| `primary` | FilledButton, FAB, ProgressIndicator, курсор TextField |
| `primaryContainer` | FilledTonalButton, InputChip |
| `secondary` | FilterChip, Snackbar action |
| `surface` | Card, Sheet, Dialog, TopAppBar, Menu |
| `surfaceContainer` | NavigationBar, NavigationRail, BottomSheet |
| `onSurfaceVariant` | Placeholder текст, подписи, вторичные иконки |
| `outline` | OutlinedButton, OutlinedTextField, Divider |
| `error` | Ошибки валидации TextField |

#### Theme Options

В настройках доступно 5 тем:
- **System** — Light/Dark по системным настройкам (наши брендовые цвета)
- **Light** — Светлая (наши брендовые цвета из Color.kt)
- **Dark** — Тёмная (наши брендовые цвета из Color.kt)
- **Material3 Light** — Чистый дефолт Material3 (фиолетовая палитра, для сравнения)
- **Material3 Dark** — Чистый дефолт Material3 (фиолетовая палитра, для сравнения)

#### Test Colors Screen

В Settings внизу есть кнопка **"Test Colors"** → открывает тестовый экран (`ui/screen/test/TestColorsScreen.kt`), который визуализирует все цвета текущей темы. Каждый элемент подписан названиями семантических цветов:
- Цвет текста/иконки: `onPrimary`, `onSurface` и т.д.
- Цвет фона: `← primary`, `← surface` и т.д. (стрелочка на отдельной строке, меньшим шрифтом)

Экран содержит: Button, FilledTonalButton, OutlinedButton, FilterChip, Card, TextField, Divider, FAB, Error-элементы, описание цветов BottomBar.

### Navigation & BackHandler

**Navigation Stack** - `MainViewModel` использует стек навигации для правильной обработки кнопки "Назад":
- Переход на основные вкладки (Dashboard, Settings, Profile) → **очищает стек**
- Переход на подэкраны (CreateNote, TestColors) → **добавляет текущий маршрут в стек**
- Кнопка "Назад" → **возвращает на предыдущий экран из стека** (не всегда на Dashboard!)

**Важно:** Подэкраны (CreateNote, TestColors) должны:
1. Принимать параметр `onNavigateBack: () -> Unit`
2. Устанавливать `topBar.onNavigateBack = onNavigateBack`
3. Иначе TopBar будет использовать **старый** callback от предыдущего экрана (баг!)

**AppBackHandler** - Cross-platform back button handling (`ui/common/AppBackHandler.kt`):
```kotlin
AppBackHandler(enabled = true) {
    if (currentRoute == Destinations.DASHBOARD) {
        showExitDialog = true
    } else {
        viewModel.navigateBack()  // Возвращает на предыдущий экран из стека
    }
}
```

**Back button priority:**
1. Most recently registered `AppBackHandler` (wins in case of multiple handlers)
2. Settings sub-pages → main settings list
3. Sub-screens (CreateNote, TestColors) → previous screen (from navigation stack)
4. Other tabs → Dashboard
5. Dashboard → Exit dialog

**Important:** Multiple `AppBackHandler` calls stack - the last one registered has priority. Use `enabled` parameter to conditionally activate handlers.
