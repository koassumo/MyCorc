package org.igo.mycorc.ui.screen.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.igo.mycorc.ui.common.LoadingContent
import org.igo.mycorc.ui.common.CommonTopBar
import org.igo.mycorc.ui.common.Dimens
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
import org.igo.mycorc.ui.theme.LocalAppStrings

@Composable
fun CreateNoteScreen(
    noteId: String? = null,
    onNavigateBack: () -> Unit
) {
    val viewModel = koinViewModel<CreateNoteViewModel>()
    val state by viewModel.state.collectAsState()
    val strings = LocalAppStrings.current

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
        LoadingContent(isLoading = state.isLoading) {
        Scaffold(
            topBar = {
                val title = when {
                    state.isReadOnly -> strings.readOnlyTitle
                    state.editMode -> strings.editTitle
                    else -> strings.createNewTitle
                }
                CommonTopBar(
                    title = title,
                    canNavigateBack = true,
                    navigateUp = onNavigateBack,
                    backButtonDescription = strings.backButtonTooltip
                )
            },
            bottomBar = {
                if (!state.isReadOnly) {
                    Button(
                        onClick = { viewModel.saveNote() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.SpaceMedium)
                            .height(Dimens.ButtonHeight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (state.editMode) strings.saveChanges else strings.saveNote)
                    }
                }
            }
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimens.ScreenPaddingSides)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.CardItemSpacing)
        ) {

            // 1. Блок Биомассы
            SmartInputCard(
                title = strings.biomassWeightLabel,
                value = state.biomassWeight,
                onValueChange = { viewModel.updateBiomass(it) },
                range = 0f..2000f,
                enabled = !state.isReadOnly
            )

            // 2. Блок Описания
            Card(
                shape = RoundedCornerShape(Dimens.CardCornerRadius),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.CardPadding)
                ) {
                    Text(text = strings.descriptionSection, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(Dimens.SpaceMedium))
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { if (!state.isReadOnly) viewModel.updateDescription(it) },
                        readOnly = state.isReadOnly,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(strings.enterDescription) },
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // 3. Блок Фото
            Card(
                shape = RoundedCornerShape(Dimens.CardCornerRadius),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.CardPadding)
                ) {
                    Text(text = strings.photoSection, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(Dimens.SpaceMedium))

                    val photoPath = state.photoPath
                    val photoUrl = state.photoUrl

                    if (photoPath == null) {
                        if (!state.isReadOnly) {
                            AppImagePicker { bytes ->
                                viewModel.onPhotoPicked(bytes)
                            }
                        } else {
                            Text(strings.noPhotoPlaceholder, style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        val photoSource = when {
                            photoUrl != null -> photoUrl
                            else -> "file://$photoPath"
                        }

                        Column {
                            AsyncImage(
                                model = photoSource,
                                contentDescription = "Photo preview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.openFullscreenPhoto() },
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.height(Dimens.SpaceSmall))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = strings.photoSaved,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                if (!state.isReadOnly) {
                                    IconButton(onClick = { viewModel.clearPhoto() }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Блок Угля
            SmartInputCard(
                title = strings.coalWeightLabel,
                value = state.coalWeight,
                onValueChange = { viewModel.updateCoal(it) },
                range = 0f..1000f,
                accent = true,
                enabled = !state.isReadOnly
            )
        }
        }
        }

        // Полноэкранный просмотр фото (поверх всего)
        state.photoPath?.let { photoPath ->
            if (state.showFullscreenPhoto) {
                val photoUrl = state.photoUrl

                // Определяем источник фото: локальный файл или URL с сервера
                val photoSource = when {
                    photoUrl != null -> photoUrl // Серверный URL (приоритет)
                    else -> "file://$photoPath" // Локальный файл
                }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = photoSource,
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

        // Диалог ошибки (пакет заблокирован на сервере)
        if (state.errorMessage != null) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.clearError()
                    onNavigateBack()
                },
                title = { Text(strings.cannotEditError) },
                text = { Text(state.errorMessage ?: "") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.clearError()
                        onNavigateBack()
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

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

    Card(
        shape = RoundedCornerShape(Dimens.CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.CardPadding)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(Dimens.SpaceMedium))

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
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.height(Dimens.SpaceMedium))

            Slider(
                value = value.toFloat(),
                onValueChange = { if (enabled) onValueChange(it.toDouble().roundTo(1)) },
                valueRange = range,
                colors = SliderDefaults.colors(
                    thumbColor = color,
                    activeTrackColor = color
                ),
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun Double.roundTo(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (this * multiplier).roundToInt() / multiplier
}