package org.igo.mycorc.ui.screen.dashboard

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
import org.igo.mycorc.ui.common.CommonCard
import org.igo.mycorc.ui.common.Dimens
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ChevronRight
import org.igo.mycorc.ui.theme.LocalAppStrings




@Composable
fun DashboardScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit = {}
) {
    val viewModel = koinViewModel<DashboardViewModel>()
    val state by viewModel.state.collectAsState()
    val strings = LocalAppStrings.current
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedFilter by remember { mutableStateOf(0) }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            DashboardTopBar(
                onNotificationClick = {},
                onSyncClick = { viewModel.syncFromServer() },
                isSyncing = state.isSyncing
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = strings.addButtonTooltip)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Row
            DashboardFilterRow(
                selectedFilter = selectedFilter,
                onFilterSelect = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            // Content
            Box(Modifier.fillMaxSize()) {
                if (state.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (state.notes.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(strings.noRecordsMessage, style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Dimens.ScreenPaddingSides),
                        verticalArrangement = Arrangement.spacedBy(Dimens.CardItemSpacing)
                    ) {
                        items(state.notes) { note ->
                            DashboardItem(
                                note = note,
                                onClick = { onNavigateToEdit(note.id) },
                                onSendClick = { viewModel.syncNote(note) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
    onNotificationClick: () -> Unit,
    onSyncClick: () -> Unit,
    isSyncing: Boolean = false
) {
    val strings = LocalAppStrings.current

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "My Carbon Packages",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        },
        actions = {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications"
                )
            }
            IconButton(
                onClick = onSyncClick,
                enabled = !isSyncing
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Sync"
                )
            }
        }
    )
}

@Composable
fun DashboardFilterRow(
    selectedFilter: Int = 0,
    onFilterSelect: (Int) -> Unit = {}
) {
    val filters = listOf("All", "Pending", "Verified", "Sent")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ScreenPaddingSides),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters.size) { index ->
            FilterChip(
                selected = selectedFilter == index,
                onClick = { onFilterSelect(index) },
                label = { Text(filters[index]) },
                modifier = Modifier.height(Dimens.ChipHeight)
            )
        }
    }
}

@Composable
fun DashboardItem(
    note: Note,
    onClick: () -> Unit = {},
    onSendClick: () -> Unit = {}
) {
    val strings = LocalAppStrings.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(Dimens.CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.CardElevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.CardPadding)
        ) {
            // Header Row: Title + Chevron
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = note.massDescription.ifEmpty { "Package #${note.id.take(4)}" },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

            // Status Badge
            StatusBadge(status = note.status, strings = strings)

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
