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
import org.igo.mycorc.ui.common.LocalTopBarState
import org.igo.mycorc.ui.common.CommonCard
import org.igo.mycorc.ui.common.Dimens
import org.igo.mycorc.ui.common.formatNoteTitle
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt
import org.igo.mycorc.ui.common.AppImagePicker
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import org.igo.mycorc.ui.theme.LocalAppStrings

/**
 * Formats duration in minutes to "Xh Ym" format
 * Examples: 90 -> "1h 30m", 45 -> "45m", 120 -> "2h"
 */
private fun formatDuration(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return when {
        hours > 0 && mins > 0 -> "${hours}h ${mins}m"
        hours > 0 -> "${hours}h"
        else -> "${mins}m"
    }
}

@OptIn(kotlin.time.ExperimentalTime::class)
@Composable
fun CreateNoteScreen(
    noteId: String? = null,
    onNavigateBack: () -> Unit
) {
    val topBar = LocalTopBarState.current
    val viewModel = koinViewModel<CreateNoteViewModel>()
    val state by viewModel.state.collectAsState()
    val strings = LocalAppStrings.current

    // Local state for new fields (not yet connected to ViewModel/DB)
    var transportDistance by remember { mutableStateOf(0.0) }
    var pyrolysisDuration by remember { mutableStateOf(0) }
    var pyrolysisTemperature by remember { mutableStateOf(0.0) }

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

    // Динамический заголовок TopBar
    val title = state.existingNote?.let { note ->
        formatNoteTitle(note)
    } ?: strings.createNewTitle
    topBar.title = title
    topBar.canNavigateBack = true
    topBar.onNavigateBack = onNavigateBack

    Box(modifier = Modifier.fillMaxSize()) {
        LoadingContent(isLoading = state.isLoading) {
        Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.CardItemSpacing)
        ) {

            // ═══════════════════════════════════════════════
            // SECTION 1: BIOMASS
            // ═══════════════════════════════════════════════
            CommonCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Dimens.ScreenPaddingSides,
                            end = Dimens.ScreenPaddingSides,
                            top = Dimens.SpaceSmall
                        ),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)
                ) {
                    // Icon column
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )

                    // Content column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium)
                    ) {
                        // Section Title
                        Text(
                            text = strings.sectionBiomass,
                            style = MaterialTheme.typography.titleLarge
                        )

                        // Biomass Weight
                        Column {
                            Text(text = strings.biomassWeightLabel, style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(Dimens.SpaceSmall))
                            OutlinedTextField(
                                value = state.biomassWeight.toString(),
                                onValueChange = { str ->
                                    if (!state.isReadOnly) {
                                        val num = str.toDoubleOrNull()
                                        if (num != null) viewModel.updateBiomass(num)
                                    }
                                },
                                readOnly = state.isReadOnly,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isReadOnly,
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                        }

                        // Transport Distance
                        Column {
                            Text(text = strings.transportDistanceLabel, style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(Dimens.SpaceSmall))
                            OutlinedTextField(
                                value = transportDistance.toString(),
                                onValueChange = { str ->
                                    if (!state.isReadOnly) {
                                        val num = str.toDoubleOrNull()
                                        if (num != null) transportDistance = num
                                    }
                                },
                                readOnly = state.isReadOnly,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isReadOnly,
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                        }

                        // Description
                        Column {
                            Text(text = strings.descriptionSection, style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(Dimens.SpaceSmall))
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
                }
            }

            // ═══════════════════════════════════════════════
            // SECTION 2: PYROLYSIS
            // ═══════════════════════════════════════════════
            CommonCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Dimens.ScreenPaddingSides,
                            end = Dimens.ScreenPaddingSides,
                            top = Dimens.SpaceSmall
                        ),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)
                ) {
                    // Icon column
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )

                    // Content column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium)
                    ) {
                        // Section Title
                        Text(
                            text = strings.sectionPyrolysis,
                            style = MaterialTheme.typography.titleLarge
                        )

                        // Duration
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = strings.pyrolysisDurationLabel, style = MaterialTheme.typography.labelMedium)
                                if (pyrolysisDuration > 0) {
                                    Text(
                                        text = formatDuration(pyrolysisDuration),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(Modifier.height(Dimens.SpaceSmall))
                            OutlinedTextField(
                                value = pyrolysisDuration.toString(),
                                onValueChange = { str ->
                                    if (!state.isReadOnly) {
                                        val num = str.toIntOrNull()
                                        if (num != null) pyrolysisDuration = num
                                    }
                                },
                                readOnly = state.isReadOnly,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isReadOnly,
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                suffix = { Text("min") }
                            )
                        }

                        // Temperature
                        Column {
                            Text(text = strings.pyrolysisTemperatureLabel, style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(Dimens.SpaceSmall))
                            OutlinedTextField(
                                value = pyrolysisTemperature.toString(),
                                onValueChange = { str ->
                                    if (!state.isReadOnly) {
                                        val num = str.toDoubleOrNull()
                                        if (num != null) pyrolysisTemperature = num
                                    }
                                },
                                readOnly = state.isReadOnly,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isReadOnly,
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // ═══════════════════════════════════════════════
            // SECTION 3: BIOCHAR
            // ═══════════════════════════════════════════════
            CommonCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Dimens.ScreenPaddingSides,
                            end = Dimens.ScreenPaddingSides,
                            top = Dimens.SpaceSmall
                        ),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)
                ) {
                    // Icon column
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )

                    // Content column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium)
                    ) {
                        // Section Title
                        Text(
                            text = strings.sectionBiochar,
                            style = MaterialTheme.typography.titleLarge
                        )

                        // Coal Weight
                        Column {
                            Text(text = strings.coalWeightLabel, style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(Dimens.SpaceSmall))
                            OutlinedTextField(
                                value = state.coalWeight.toString(),
                                onValueChange = { str ->
                                    if (!state.isReadOnly) {
                                        val num = str.toDoubleOrNull()
                                        if (num != null) viewModel.updateCoal(num)
                                    }
                                },
                                readOnly = state.isReadOnly,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isReadOnly,
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                        }

                        // Photo
                        Column {
                            Text(text = strings.photoSection, style = MaterialTheme.typography.labelMedium)
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
                }
            }

            // ═══════════════════════════════════════════════
            // SECTION 4: DELIVERY
            // ═══════════════════════════════════════════════
            CommonCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Dimens.ScreenPaddingSides,
                            end = Dimens.ScreenPaddingSides,
                            top = Dimens.SpaceSmall
                        ),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)
                ) {
                    // Icon column
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )

                    // Content column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium)
                    ) {
                        // Section Title
                        Text(
                            text = strings.sectionDelivery,
                            style = MaterialTheme.typography.titleLarge
                        )

                        // Placeholder for future delivery/logistics fields
                        Text(
                            text = "Delivery information will be added here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        // Кнопка сохранения (была в bottomBar)
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