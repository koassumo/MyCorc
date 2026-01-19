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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun CreateNoteScreen(
    noteId: String? = null,
    onNavigateBack: () -> Unit
) {
    val viewModel = koinViewModel<CreateNoteViewModel>()
    val state by viewModel.state.collectAsState()

    // Загружаем запись при входе в режим редактирования
    LaunchedEffect(noteId) {
        if (noteId != null) {
            viewModel.loadNote(noteId)
        }
    }

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

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                val title = when {
                    state.isReadOnly -> "Просмотр партии (синхронизировано)"
                    state.editMode -> "Редактирование партии"
                    else -> "Новая партия"
                }
                CommonTopBar(
                    title = title,
                    canNavigateBack = true,
                    navigateUp = onNavigateBack
                )
            },
            bottomBar = {
                // Кнопка "Сохранить" всегда доступна (кроме read-only режима)
                if (!state.isReadOnly) {
                    Button(
                        onClick = { viewModel.saveNote() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp)
                    ) {
                        Text(if (state.editMode) "Сохранить изменения" else "Сохранить партию")
                    }
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
                range = 0f..2000f,
                enabled = !state.isReadOnly
            )

            // 2. Блок Описания
            CommonCard {
                Text(text = "Описание партии", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { if (!state.isReadOnly) viewModel.updateDescription(it) },
                    readOnly = state.isReadOnly,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Введите описание партии") },
                    minLines = 2,
                    maxLines = 4
                )
            }

            // 3. Блок Фото
            CommonCard {
                Text(text = "Фотография", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))

                val photoPath = state.photoPath

                if (photoPath == null) {
                    // Если фото нет — показываем кнопку камеры (если не read-only)
                    if (!state.isReadOnly) {
                        AppImagePicker { bytes ->
                            viewModel.onPhotoPicked(bytes)
                        }
                    } else {
                        Text("Фото отсутствует", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    // Если фото есть — показываем превью
                    Column {
                        AsyncImage(
                            model = "file://$photoPath",
                            contentDescription = "Превью фото",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable { viewModel.openFullscreenPhoto() },
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Фото сохранено",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            if (!state.isReadOnly) {
                                IconButton(onClick = { viewModel.clearPhoto() }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                                }
                            }
                        }
                    }
                }
            }

            // 4. Блок Угля
            SmartInputCard(
                title = "Вес Угля (кг)",
                value = state.coalWeight,
                onValueChange = { viewModel.updateCoal(it) },
                range = 0f..1000f,
                accent = true,
                enabled = !state.isReadOnly
            )
        }
        }

        // Полноэкранный просмотр фото
        if (state.showFullscreenPhoto && state.photoPath != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = "file://${state.photoPath}",
                    contentDescription = "Полноэкранное фото",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                // Кнопка "назад"
                IconButton(
                    onClick = { viewModel.closeFullscreenPhoto() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Закрыть",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

// 👇 Вспомогательные функции вынесены из тела CreateNoteScreen
@Composable
fun SmartInputCard(
    title: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    accent: Boolean = false,
    enabled: Boolean = true
) {
    val color = if (accent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    CommonCard {
        Text(text = title, style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = value.toString(),
                onValueChange = { str ->
                    if (enabled) {
                        val num = str.toDoubleOrNull()
                        if (num != null) onValueChange(num)
                    }
                },
                readOnly = !enabled,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                enabled = enabled
            )
        }

        Spacer(Modifier.height(8.dp))

        Slider(
            value = value.toFloat(),
            onValueChange = { if (enabled) onValueChange(it.toDouble().roundTo(1)) },
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color
            ),
            enabled = enabled
        )
    }
}

fun Double.roundTo(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (this * multiplier).roundToInt() / multiplier
}