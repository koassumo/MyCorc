package org.igo.mycorc.ui.screen.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.model.NoteStatus
import org.igo.mycorc.ui.common.Dimens
import org.igo.mycorc.ui.common.LocalTopBarState
import org.igo.mycorc.ui.common.LoadingContent
import org.igo.mycorc.ui.common.formatNoteTitle
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import org.igo.mycorc.ui.theme.LocalAppStrings


@Composable
fun DashboardScreen(
    onNavigateToEdit: (String) -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val topBar = LocalTopBarState.current
    val viewModel = koinViewModel<DashboardViewModel>()
    val state by viewModel.state.collectAsState()
    val strings = LocalAppStrings.current
    var selectedFilters by remember { mutableStateOf<Set<NoteStatus>>(emptySet()) }

    // Публикуем конфигурацию TopBar
    topBar.title = strings.dashboardTitle
    topBar.canNavigateBack = false

    // Автоматическая синхронизация при открытии экрана
    LaunchedEffect(Unit) {
        viewModel.syncFromServer()
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    LoadingContent(isLoading = state.isSyncing) {
        Box(Modifier.fillMaxSize()) {
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (state.notes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(strings.noRecordsMessage, style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    // Фильтрация списка
                    val filteredNotes = if (selectedFilters.isEmpty()) {
                        state.notes // Показываем все, если фильтры не выбраны (как "All")
                    } else {
                        state.notes.filter { note -> note.status in selectedFilters }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Фильтры как первый элемент списка
                        item {
                            Column {
                                Spacer(modifier = Modifier.height(Dimens.SpaceMedium))
                                DashboardFilterRow(
                                    selectedFilters = selectedFilters,
                                    onFilterToggle = { status ->
                                        selectedFilters = if (status == null) {
                                            // "All" button clicked - clear all filters
                                            emptySet()
                                        } else if (status in selectedFilters) {
                                            // Deselect filter
                                            selectedFilters - status
                                        } else {
                                            // Select filter
                                            selectedFilters + status
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(Dimens.SpaceMedium))
                            }
                        }

                        // Список пакетов
                        items(filteredNotes) { note ->
                            Column {
                                DashboardItem(
                                    note = note,
                                    onClick = { onNavigateToEdit(note.id) },
                                    onSendClick = { viewModel.syncNote(note) },
                                    modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingSides)
                                )
                                Spacer(modifier = Modifier.height(Dimens.CardItemSpacing))
                            }
                        }
                    }
                }
            }
        }
}

data class FilterItem(
    val label: String,
    val status: NoteStatus? // null for "All"
)

@Composable
fun DashboardFilterRow(
    selectedFilters: Set<NoteStatus> = emptySet(),
    onFilterToggle: (NoteStatus?) -> Unit = {}
) {
    val filters = listOf(
        FilterItem("All", null),
        FilterItem("Pending", NoteStatus.DRAFT),
        FilterItem("Ready", NoteStatus.READY_TO_SEND),
        FilterItem("Sent", NoteStatus.SENT),
        FilterItem("Approved", NoteStatus.APPROVED),
        FilterItem("Rejected", NoteStatus.REJECTED)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ScreenPaddingSides),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            val isSelected = if (filter.status == null) {
                // "All" is selected when no specific filters are active
                selectedFilters.isEmpty()
            } else {
                filter.status in selectedFilters
            }

            FilterChip(
                selected = isSelected,
                onClick = { onFilterToggle(filter.status) },
                label = { Text(filter.label) },
                modifier = Modifier.height(Dimens.ChipHeight)
            )
        }
    }
}

@OptIn(kotlin.time.ExperimentalTime::class)
@Composable
fun DashboardItem(
    note: Note,
    onClick: () -> Unit = {},
    onSendClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(Dimens.CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.CardPadding)
        ) {
            // Header Row: Title + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatNoteTitle(note),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f).padding(end = Dimens.SpaceSmall)
                )
                StatusBadge(status = note.status, strings = strings)
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            // Info Row: Weight + Coal
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)
            ) {
                InfoText("${strings.weightLabel}: ${note.massWeight} кг")

                if (note.coalWeight != null && note.coalWeight!! > 0) {
                    InfoText("${strings.coalLabel}: ${note.coalWeight} кг")
                }
            }

            // Action based on status
            when (note.status) {
                org.igo.mycorc.domain.model.NoteStatus.READY_TO_SEND -> {
                    Spacer(modifier = Modifier.height(Dimens.SpaceMedium))
                    Button(
                        onClick = onSendClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(strings.sendToRegistration)
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun StatusBadge(
    status: org.igo.mycorc.domain.model.NoteStatus,
    strings: org.igo.mycorc.domain.strings.AppStrings
) {
    val (badgeText, badgeColor, textColor) = when (status) {
        org.igo.mycorc.domain.model.NoteStatus.DRAFT ->
            Triple("Pending", Color(0xFFFFF3E0), Color(0xFFE65100))

        org.igo.mycorc.domain.model.NoteStatus.READY_TO_SEND ->
            Triple("Ready", Color(0xFFF0F4FF), Color(0xFF1565C0))

        org.igo.mycorc.domain.model.NoteStatus.SENT ->
            Triple("Sent", Color(0xFFE0F2F1), Color(0xFF00695C))

        org.igo.mycorc.domain.model.NoteStatus.APPROVED ->
            Triple("Approved", Color(0xFFC8E6C9), Color(0xFF2E7D32))

        org.igo.mycorc.domain.model.NoteStatus.REJECTED ->
            Triple("Rejected", Color(0xFFFFCDD2), Color(0xFFC62828))
    }

    Surface(
        color = badgeColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = badgeText,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun InfoText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
