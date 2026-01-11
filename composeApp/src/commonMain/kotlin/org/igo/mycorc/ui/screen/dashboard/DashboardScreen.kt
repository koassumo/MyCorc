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
import androidx.compose.material3.Icon

@Composable
fun DashboardScreen() {
    val viewModel = koinViewModel<DashboardViewModel>()
    val state by viewModel.state.collectAsState()

    // Используем Scaffold локально для FAB,
    // либо можно добавить FAB в общий Scaffold в MainScreen (если кнопка нужна везде).
    // Для теста добавим прямо здесь.
    Scaffold(
        topBar = { CommonTopBar(title = "Dashboard") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.addTestNote() },
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
                        DashboardItem(note = note)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardItem(note: Note) {
    CommonCard(
        onClick = { println("Нажали на ${note.id}") }
    ) {
        Text(text = note.massDescription, style = MaterialTheme.typography.titleLarge)
        Text(text = "Вес: ${note.massWeight} кг")

        if (note.coalWeight != null) {
            Text(
                text = "🏁 Уголь: ${note.coalWeight} кг",
                // Используем цвет из нашей новой темы (Theme.kt)
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}