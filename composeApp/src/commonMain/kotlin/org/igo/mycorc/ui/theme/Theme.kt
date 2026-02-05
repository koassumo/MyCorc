package org.igo.mycorc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.igo.mycorc.ui.screen.settings.AppThemeConfig

// ============================================================
// Кастомные темы (наши брендовые цвета из Color.kt)
// ============================================================

// Настройка для СВЕТЛОЙ темы
// Переопределяем только нужные роли. Остальные (tertiary, inverseSurface и т.д.)
// остаются дефолтными из Material3.
private val LightColors = lightColorScheme(
    // Primary (основной брендовый цвет)
    primary = LightPrimary,                    // FilledButton, ProgressIndicator, курсор TextField
    onPrimary = LightOnPrimary,                // Текст/иконки НА primary
    primaryContainer = LightPrimaryContainer,  // FAB, FilledTonalButton, InputChip
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
    outline = LightOutline,                    // OutlinedButton, OutlinedTextField, обводка Card
    outlineVariant = LightOutlineVariant,      // HorizontalDivider, мягкие разделители

    // Error
    error = ErrorColor                         // Ошибки валидации TextField, деструктивные действия
)

// Настройка для ТЕМНОЙ темы
private val DarkColors = darkColorScheme(
    // Primary (основной брендовый цвет)
    primary = DarkPrimary,                     // FilledButton, ProgressIndicator, курсор TextField
    onPrimary = DarkOnPrimary,                 // Текст/иконки НА primary
    primaryContainer = DarkPrimaryContainer,   // FAB, FilledTonalButton, InputChip
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
    outline = DarkOutline,                     // OutlinedButton, OutlinedTextField, обводка Card
    outlineVariant = DarkOutlineVariant,       // HorizontalDivider, мягкие разделители

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
    }

    val appStrings = rememberAppStrings(languageConfig)

    CompositionLocalProvider(
        LocalAppStrings provides appStrings
    ) {
        MaterialTheme(
            colorScheme = colors,
            content = content
        )
    }
}