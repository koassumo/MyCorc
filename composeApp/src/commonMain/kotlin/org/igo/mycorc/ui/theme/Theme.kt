package org.igo.mycorc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

// Настройка для СВЕТЛОЙ темы
private val LightColors = lightColorScheme(
    primary = BrandKey,
    onPrimary = White,
    primaryContainer = BrandLight,
    onPrimaryContainer = Black,

    secondary = AccentKey,
    onSecondary = White,

    background = LightBackground,  // Светло-фиолетовый фон
    onBackground = Black,

    surface = White,
    onSurface = Black,

    surfaceContainer = White, // Бары такого же цвета как surface

    outline = LightCardBorder, // Обводка карточек

    error = ErrorRed
)

// Настройка для ТЕМНОЙ темы
private val DarkColors = darkColorScheme(
    primary = BrandKey,
    onPrimary = White,
    primaryContainer = BrandDark,
    onPrimaryContainer = White,

    secondary = AccentKey,
    onSecondary = White,

    background = DarkBackground,        // Самый тёмный - фон экрана
    onBackground = White,

    surface = DarkSurfaceCard,          // Карточки (светлее баров)
    onSurface = White,

    surfaceContainer = DarkSurfaceBar,  // Бары (чуть светлее фона, темнее карточек)

    outline = DarkCardBorder,           // Обводка карточек

    error = ErrorRed
)

@Composable
fun MyAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    languageConfig: org.igo.mycorc.ui.screen.settings.AppLanguageConfig,
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors
    val appStrings = rememberAppStrings(languageConfig)

    CompositionLocalProvider(LocalAppStrings provides appStrings) {
        MaterialTheme(
            colorScheme = colors,
            content = content
        )
    }
}