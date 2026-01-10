package org.igo.mycorc.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.igo.mycorc.ui.common.CommonCard
import org.igo.mycorc.ui.common.CommonTopBar
import org.igo.mycorc.ui.common.Dimens
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen() {
    // 1. Получаем ViewModel через Koin
    val viewModel = koinViewModel<SettingsViewModel>() //да прибудет с тобою koin (2)
    // эту строчку никогда не нужно менять (указывать параметры конструктора и т.д.) благодаря koin


    // 2. Подписываемся на состояние
    val state by viewModel.state.collectAsState()

    Column(Modifier.fillMaxSize()) {
        CommonTopBar(title = "Настройки")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.ScreenPaddingSides)
        ) {
            Text(
                text = "Тема оформления",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Вариант: Системная
            ThemeSelectionItem(
                label = "Как в системе",
                isSelected = state.selectedTheme == AppThemeConfig.SYSTEM,
                onSelect = { viewModel.updateTheme(AppThemeConfig.SYSTEM) }
            )

            // Вариант: Светлая
            ThemeSelectionItem(
                label = "Светлая",
                isSelected = state.selectedTheme == AppThemeConfig.LIGHT,
                onSelect = { viewModel.updateTheme(AppThemeConfig.LIGHT) }
            )

            // Вариант: Тёмная
            ThemeSelectionItem(
                label = "Тёмная",
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
    // Используем твой универсальный CommonCard
    CommonCard(
        onClick = onSelect,
        // Если выбрано — подсвечиваем карточку цветом PrimaryContainer
        containerColor = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )

            RadioButton(
                selected = isSelected,
                onClick = null // Клик обрабатывается всей карточкой (CommonCard)
            )
        }
    }
}