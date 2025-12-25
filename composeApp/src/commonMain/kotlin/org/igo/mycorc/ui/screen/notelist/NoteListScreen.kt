package org.igo.mycorc.ui.screen.notelist

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.ui.common.CommonCard
import org.igo.mycorc.ui.common.CommonTopBar

@Composable
fun NoteListScreen() {
    val viewModel = remember { NoteListViewModel() }
    val state by viewModel.state.collectAsState()

    Column(Modifier.fillMaxSize()) {

        // 1. Наша универсальная шапка
        CommonTopBar(title = "Мои партии")

        // 2. Контент
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Список от края до края
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.notes) { note ->
                    NoteItem(note = note)
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: Note) {
    // CommonCard сам возьмет отступы из Dimens
    CommonCard(
        onClick = { println("Нажали на ${note.id}") }
    ) {
        Text(text = note.massDescription, style = MaterialTheme.typography.titleLarge)
        Text(text = "Вес: ${note.massWeight} кг")

        if (note.coalWeight != null) {
            Text(text = "🏁 Уголь: ${note.coalWeight} кг", color = MaterialTheme.colorScheme.primary)
        }
    }
}
