package org.igo.mycorc.ui.screen.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.igo.mycorc.ui.common.CommonTopBar
import org.igo.mycorc.ui.common.Dimens
import org.koin.compose.viewmodel.koinViewModel
import org.igo.mycorc.ui.theme.LocalAppStrings

@Composable
fun SettingsScreen() {
    // 1. Получаем ViewModel через Koin
    val viewModel = koinViewModel<SettingsViewModel>() //да прибудет с тобою koin (2)
    // эту строчку никогда не нужно менять (указывать параметры конструктора и т.д.) благодаря koin


    // 2. Подписываемся на состояние
    val state by viewModel.state.collectAsState()
    val strings = LocalAppStrings.current

    Scaffold(
        topBar = { CommonTopBar(title = strings.settingsTitle) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Dimens.ScreenPaddingSides)
        ) {
            Text(
                text = strings.themeSection,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = Dimens.SpaceLarge)
            )

            // Вариант: Системная
            ThemeSelectionItem(
                label = strings.systemTheme,
                isSelected = state.selectedTheme == AppThemeConfig.SYSTEM,
                onSelect = { viewModel.updateTheme(AppThemeConfig.SYSTEM) }
            )

            Spacer(modifier = Modifier.height(Dimens.CardItemSpacing))

            // Вариант: Светлая
            ThemeSelectionItem(
                label = strings.lightTheme,
                isSelected = state.selectedTheme == AppThemeConfig.LIGHT,
                onSelect = { viewModel.updateTheme(AppThemeConfig.LIGHT) }
            )

            Spacer(modifier = Modifier.height(Dimens.CardItemSpacing))

            // Вариант: Тёмная
            ThemeSelectionItem(
                label = strings.darkTheme,
                isSelected = state.selectedTheme == AppThemeConfig.DARK,
                onSelect = { viewModel.updateTheme(AppThemeConfig.DARK) }
            )
        }
    }
}

@Composable
fun ThemeSelectionItem(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(Dimens.CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.CardPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )

            RadioButton(
                selected = isSelected,
                onClick = null
            )
        }
    }
}