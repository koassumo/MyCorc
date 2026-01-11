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

@Composable
fun CreateNoteScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel = koinViewModel<CreateNoteViewModel>()
    val state by viewModel.state.collectAsState()

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
            // Кнопка "Сохранить" внизу
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
                .verticalScroll(rememberScrollState()) // Чтобы можно было скроллить, если клавиатура вылезет
        ) {

            // 1. Блок Биомассы
            SmartInputCard(
                title = "Вес Биомассы (кг)",
                value = state.biomassWeight,
                onValueChange = { viewModel.updateBiomass(it) },
                range = 0f..2000f
            )

            // 2. Блок Угля
            SmartInputCard(
                title = "Вес Угля (кг)",
                value = state.coalWeight,
                onValueChange = { viewModel.updateCoal(it) },
                range = 0f..1000f,
                accent = true // Подсветим другим цветом
            )
        }
    }
}

// 👇 Наш многоразовый компонент (Card + Input + Slider)
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
            // Поле ввода цифр
            OutlinedTextField(
                value = value.toString(),
                onValueChange = { str ->
                    // Пробуем превратить строку в Double, если ошибка — оставляем старое
                    val num = str.toDoubleOrNull()
                    if (num != null) onValueChange(num)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))

        // Слайдер (Бегунок)
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toDouble().roundTo(1)) }, // Округляем до 1 знака
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color
            )
        )
    }
}

// Вспомогательная функция для округления (чтобы не было 500.00000001)
fun Double.roundTo(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (this * multiplier).roundToInt() / multiplier
}