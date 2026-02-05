package org.igo.mycorc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.igo.mycorc.ui.screen.settings.AppThemeConfig

// ============================================================
// Дефолтные темы Material3 (встроенные фиолетовые цвета, без переопределений)
// ============================================================

private val DefaultLightColors = lightColorScheme()
private val DefaultDarkColors = darkColorScheme()

// ============================================================
// Кастомные темы (наши брендовые цвета из Color.kt)
// ============================================================

// Настройка для СВЕТЛОЙ темы
// Переопределяем только нужные роли. Остальные (tertiary, inverseSurface и т.д.)
// остаются дефолтными из Material3.
private val LightColors = lightColorScheme(
    // Primary (основной брендовый цвет)
    primary = LightPrimary,                    // FilledButton, FAB, ProgressIndicator, курсор TextField
    onPrimary = LightOnPrimary,                // Текст/иконки НА primary
    primaryContainer = LightPrimaryContainer,  // FilledTonalButton, InputChip
    onPrimaryContainer = LightOnPrimaryContainer, // Текст/иконки НА primaryContainer

    // Secondary (дополнительный цвет)
    secondary = LightSecondary,                // FilterChip, Snackbar action
    onSecondary = LightOnSecondary,            // Текст/иконки НА secondary

    // Background (фон экрана)
    background = LightBackground,              // Самый нижний слой — фон за всем контентом
    onBackground = LightOnBackground,          // Текст/иконки НА background

    // Surface (поверхности)
    surface = LightSurface,                    // Card, Sheet, Dialog, TopAppBar, Menu
    onSurface = LightOnSurface,                // Основной текст/иконки НА surface
    surfaceContainer = LightSurfaceContainer,  // NavigationBar, NavigationRail, BottomSheet
    onSurfaceVariant = LightOnSurfaceVariant,  // Placeholder, подписи, вторичные иконки

    // Borders & Dividers
    outline = LightOutline,                    // OutlinedButton, OutlinedTextField, Divider

    // Error
    error = ErrorColor                         // Ошибки валидации TextField, деструктивные действия
)

// Настройка для ТЕМНОЙ темы
private val DarkColors = darkColorScheme(
    // Primary (основной брендовый цвет)
    primary = DarkPrimary,                     // FilledButton, FAB, ProgressIndicator, курсор TextField
    onPrimary = DarkOnPrimary,                 // Текст/иконки НА primary
    primaryContainer = DarkPrimaryContainer,   // FilledTonalButton, InputChip
    onPrimaryContainer = DarkOnPrimaryContainer, // Текст/иконки НА primaryContainer

    // Secondary (дополнительный цвет)
    secondary = DarkSecondary,                 // FilterChip, Snackbar action
    onSecondary = DarkOnSecondary,             // Текст/иконки НА secondary

    // Background (фон экрана)
    background = DarkBackground,               // Самый нижний слой — фон за всем контентом
    onBackground = DarkOnBackground,           // Текст/иконки НА background

    // Surface (поверхности)
    surface = DarkSurface,                     // Card, Sheet, Dialog, TopAppBar, Menu
    onSurface = DarkOnSurface,                 // Основной текст/иконки НА surface
    surfaceContainer = DarkSurfaceContainer,   // NavigationBar, NavigationRail, BottomSheet
    onSurfaceVariant = DarkOnSurfaceVariant,   // Placeholder, подписи, вторичные иконки

    // Borders & Dividers
    outline = DarkOutline,                     // OutlinedButton, OutlinedTextField, Divider

    // Error
    error = ErrorColor                         // Ошибки валидации TextField, деструктивные действия
)

@Composable
fun MyAppTheme(
    themeConfig: AppThemeConfig = AppThemeConfig.SYSTEM,
    languageConfig: org.igo.mycorc.ui.screen.settings.AppLanguageConfig,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()

    val colors = when (themeConfig) {
        AppThemeConfig.SYSTEM -> if (isSystemDark) DarkColors else LightColors
        AppThemeConfig.LIGHT -> LightColors
        AppThemeConfig.DARK -> DarkColors
        AppThemeConfig.DEFAULT_LIGHT -> DefaultLightColors
        AppThemeConfig.DEFAULT_DARK -> DefaultDarkColors
    }

    val appStrings = rememberAppStrings(languageConfig)

    CompositionLocalProvider(LocalAppStrings provides appStrings) {
        MaterialTheme(
            colorScheme = colors,
            content = content
        )
    }
}