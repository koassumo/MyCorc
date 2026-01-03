package org.igo.mycorc.ui.screen.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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

@Composable
fun DashboardScreen() {
    // 💉 ВНЕДРЕНИЕ ЗАВИСИМОСТИ (Koin)
    // Koin сам создаст ViewModel (и переживет поворот экрана)
    val viewModel = koinViewModel<DashboardViewModel>()

    val state by viewModel.state.collectAsState()

    Column(Modifier.fillMaxSize()) {
        CommonTopBar(title = "Dashboard") // Поменял заголовок под новое имя

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
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