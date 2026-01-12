package org.igo.mycorc.ui.screen.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.igo.mycorc.ui.common.CommonCard
import org.igo.mycorc.ui.common.CommonTopBar
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt
import org.igo.mycorc.ui.common.AppImagePicker
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete

@Composable
fun CreateNoteScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel = koinViewModel<CreateNoteViewModel>()
    val state by viewModel.state.collectAsState()

    // Чистим состояние, когда экран УНИЧТОЖАЕТСЯ (при выходе)
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }

    // Если запись сохранена — уходим назад
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Новая партия",
                canNavigateBack = true,
                navigateUp = onNavigateBack
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.saveNote() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
            ) {
                Text("Сохранить партию")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            // 1. Блок Биомассы
            SmartInputCard(
                title = "Вес Биомассы (кг)",
                value = state.biomassWeight,
                onValueChange = { viewModel.updateBiomass(it) },
                range = 0f..2000f
            )

            // 2. Блок Фото
            CommonCard {
                Text(text = "Фотография", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))

                // 👇 Вводим локальную переменную для безопасной проверки (Smart Cast fix)
                val currentImage = state.imageBytes

                if (currentImage == null) {
                    // Если фото нет — показываем кнопку камеры
                    AppImagePicker { bytes ->
                        viewModel.onPhotoPicked(bytes)
                    }
                } else {
                    // Если фото есть — показываем статус
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Фото добавлено!", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                // Показываем размер в КБ
                                text = "${currentImage.size / 1024} KB",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { viewModel.clearPhoto() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить")
                        }
                    }
                }
            }

            // 3. Блок Угля
            SmartInputCard(
                title = "Вес Угля (кг)",
                value = state.coalWeight,
                onValueChange = { viewModel.updateCoal(it) },
                range = 0f..1000f,
                accent = true
            )
        }
    }
} // 👈 ВОТ ОНА, ГЛАВНАЯ ЗАКРЫВАЮЩАЯ СКОБКА! Теперь все, что ниже, находится СНАРУЖИ.

// 👇 Вспомогательные функции вынесены из тела CreateNoteScreen
@Composable
fun SmartInputCard(
    title: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    accent: Boolean = false
) {
    val color = if (accent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    CommonCard {
        Text(text = title, style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = value.toString(),
                onValueChange = { str ->
                    val num = str.toDoubleOrNull()
                    if (num != null) onValueChange(num)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toDouble().roundTo(1)) },
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color
            )
        )
    }
}

fun Double.roundTo(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (this * multiplier).roundToInt() / multiplier
}