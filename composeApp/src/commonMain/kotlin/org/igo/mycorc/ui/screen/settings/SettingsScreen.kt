package org.igo.mycorc.ui.screen.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.igo.mycorc.ui.common.CommonTopBar
import org.igo.mycorc.ui.common.Dimens
import org.koin.compose.viewmodel.koinViewModel
import org.igo.mycorc.ui.theme.LocalAppStrings

// Sealed interface для локальной навигации внутри настроек
private sealed interface SettingsPage {
    data object MainList : SettingsPage
    data object ThemeSelection : SettingsPage
    data object LanguageSelection : SettingsPage
}

@Composable
fun SettingsScreen() {
    val viewModel = koinViewModel<SettingsViewModel>()
    val state by viewModel.state.collectAsState()
    val strings = LocalAppStrings.current

    // Локальное состояние навигации
    var currentPage by remember { mutableStateOf<SettingsPage>(SettingsPage.MainList) }

    // AnimatedContent для плавных переходов между "подэкранами"
    AnimatedContent(
        targetState = currentPage,
        transitionSpec = {
            if (targetState != SettingsPage.MainList) {
                // Проваливаемся внутрь (слайд справа налево)
                slideInHorizontally(
                    initialOffsetX = { width -> width },
                    animationSpec = tween(300)
                ) + fadeIn() togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { width -> -width / 2 },
                            animationSpec = tween(300)
                        ) + fadeOut()
            } else {
                // Возвращаемся назад (слайд слева направо)
                slideInHorizontally(
                    initialOffsetX = { width -> -width / 2 },
                    animationSpec = tween(300)
                ) + fadeIn() togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { width -> width },
                            animationSpec = tween(300)
                        ) + fadeOut()
            }
        },
        label = "settings_page_transition"
    ) { page ->
        when (page) {
            SettingsPage.MainList -> {
                Scaffold(
                    topBar = { CommonTopBar(title = strings.settingsTitle) },
                    containerColor = MaterialTheme.colorScheme.background
                ) { padding ->
                    SettingsMainList(
                        modifier = Modifier.padding(padding),
                        currentTheme = state.selectedTheme,
                        currentLanguage = state.selectedLanguage,
                        onThemeClick = { currentPage = SettingsPage.ThemeSelection },
                        onLanguageClick = { currentPage = SettingsPage.LanguageSelection }
                    )
                }
            }

            SettingsPage.ThemeSelection -> {
                Scaffold(
                    topBar = {
                        CommonTopBar(
                            title = strings.themeSection,
                            canNavigateBack = true,
                            navigateUp = { currentPage = SettingsPage.MainList },
                            backButtonDescription = strings.backButtonTooltip
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { padding ->
                    SelectionScreen(
                        modifier = Modifier.padding(padding),
                        options = listOf(
                            AppThemeConfig.SYSTEM to strings.systemTheme,
                            AppThemeConfig.LIGHT to strings.lightTheme,
                            AppThemeConfig.DARK to strings.darkTheme
                        ),
                        selectedOption = state.selectedTheme,
                        onOptionSelected = { newTheme ->
                            viewModel.updateTheme(newTheme)
                            currentPage = SettingsPage.MainList
                        }
                    )
                }
            }

            SettingsPage.LanguageSelection -> {
                Scaffold(
                    topBar = {
                        CommonTopBar(
                            title = strings.languageSection,
                            canNavigateBack = true,
                            navigateUp = { currentPage = SettingsPage.MainList },
                            backButtonDescription = strings.backButtonTooltip
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { padding ->
                    SelectionScreen(
                        modifier = Modifier.padding(padding),
                        options = listOf(
                            AppLanguageConfig.SYSTEM to strings.systemTheme,
                            AppLanguageConfig.EN to strings.languageEn,
                            AppLanguageConfig.RU to strings.languageRu,
                            AppLanguageConfig.DE to strings.languageDe
                        ),
                        selectedOption = state.selectedLanguage,
                        onOptionSelected = { newLanguage ->
                            viewModel.updateLanguage(newLanguage)
                            currentPage = SettingsPage.MainList
                        }
                    )
                }
            }
        }
    }
}

// Главный список настроек
@Composable
private fun SettingsMainList(
    modifier: Modifier = Modifier,
    currentTheme: AppThemeConfig,
    currentLanguage: AppLanguageConfig,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit
) {
    val strings = LocalAppStrings.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPaddingSides)
    ) {
        Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

        // Пункт: Тема
        SettingsMenuItem(
            title = strings.themeSection,
            currentValue = when (currentTheme) {
                AppThemeConfig.SYSTEM -> strings.systemTheme
                AppThemeConfig.LIGHT -> strings.lightTheme
                AppThemeConfig.DARK -> strings.darkTheme
            },
            onClick = onThemeClick
        )

        HorizontalDivider()

        // Пункт: Язык
        SettingsMenuItem(
            title = strings.languageSection,
            currentValue = when (currentLanguage) {
                AppLanguageConfig.SYSTEM -> strings.systemTheme
                AppLanguageConfig.EN -> strings.languageEn
                AppLanguageConfig.RU -> strings.languageRu
                AppLanguageConfig.DE -> strings.languageDe
            },
            onClick = onLanguageClick
        )

        HorizontalDivider()
    }
}

// Пункт меню настроек (крупно название, мелко значение, стрелка справа)
@Composable
private fun SettingsMenuItem(
    title: String,
    currentValue: String,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Text(
                text = currentValue,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

// Generic экран выбора с радио-кнопками
@Composable
private fun <T> SelectionScreen(
    modifier: Modifier = Modifier,
    options: List<Pair<T, String>>, // Pair<значение, отображаемый текст>
    selectedOption: T,
    onOptionSelected: (T) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPaddingSides)
    ) {
        Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

        // Список опций с радио-кнопками
        options.forEach { (value, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOptionSelected(value) }
                    .padding(vertical = Dimens.SpaceMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (value == selectedOption),
                    onClick = null // Клик обрабатывает Row
                )

                Spacer(modifier = Modifier.width(Dimens.SpaceMedium))

                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}