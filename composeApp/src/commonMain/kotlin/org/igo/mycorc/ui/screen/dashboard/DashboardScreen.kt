package org.igo.mycorc.ui.screen.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.ui.common.CommonCard
import org.igo.mycorc.ui.common.CommonTopBar
import org.koin.compose.viewmodel.koinViewModel // 👈 Обязательный импорт для Koin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp




@Composable
fun DashboardScreen(
    onNavigateToCreate: () -> Unit, // Создание новой записи
    onNavigateToEdit: (String) -> Unit = {} // Редактирование существующей (noteId)
) {
    val viewModel = koinViewModel<DashboardViewModel>()
    val state by viewModel.state.collectAsState()

    // Используем Scaffold локально для FAB,
    // либо можно добавить FAB в общий Scaffold в MainScreen (если кнопка нужна везде).
    // Для теста добавим прямо здесь.
    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Dashboard",
                actions = {
                    IconButton(
                        onClick = { viewModel.syncFromServer() },
                        enabled = !state.isSyncing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Синхронизировать с сервером"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { innerPadding ->

        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.notes.isEmpty()) {
                // Заглушка, если список пуст
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет записей. Нажми +", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.notes) { note ->
                        DashboardItem(
                            note = note,
                            onClick = { onNavigateToEdit(note.id) }, // 👈 Клик по карточке → редактирование
                            onSendClick = { viewModel.syncNote(note) } // Кнопка "Отправить"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardItem(
    note: Note,
    onClick: () -> Unit = {}, // Клик по всей карточке
    onSendClick: () -> Unit // Кнопка "Отправить"
) {
    CommonCard(
        onClick = onClick
    ) {
        Column(Modifier.fillMaxWidth()) {
            // --- Основная инфа ---
            Text(text = note.massDescription, style = MaterialTheme.typography.titleLarge)
            Text(text = "Вес: ${note.massWeight} кг")

            if (note.coalWeight != null) {
                Text(
                    text = "🏁 Уголь: ${note.coalWeight} кг",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Блок синхронизации ---
            // Проверяем статус карточки
            when (note.status) {
                org.igo.mycorc.domain.model.NoteStatus.DRAFT -> {
                    // Не все поля заполнены - показываем подсказку
                    Text(
                        text = "⚠️ Заполните все поля для отправки",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                org.igo.mycorc.domain.model.NoteStatus.READY_TO_SEND -> {
                    // Все поля заполнены - показываем кнопку "Отправить"
                    Button(
                        onClick = onSendClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Отправить на сервер")
                    }
                }
                org.igo.mycorc.domain.model.NoteStatus.SENT -> {
                    // Отправлено на сервер - показываем статус
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "Отправлено на регистрацию",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Ok",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                org.igo.mycorc.domain.model.NoteStatus.APPROVED -> {
                    // Одобрено
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "Одобрено",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF4CAF50)
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Approved",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                org.igo.mycorc.domain.model.NoteStatus.REJECTED -> {
                    // Отклонено
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "Отклонено",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
               // Используем цвет из нашей новой темы (Theme.kt)
