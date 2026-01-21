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

    background = GrayLight,
    onBackground = Black,

    surface = White,
    onSurface = Black,

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

    background = GrayDark,
    onBackground = White,

    surface = Black,
    onSurface = White,

    error = ErrorRed
)

@Composable
fun MyAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors
    val appStrings = rememberAppStrings()

    CompositionLocalProvider(LocalAppStrings provides appStrings) {
        MaterialTheme(
            colorScheme = colors,
            content = content
        )
    }
}