package org.igo.mycorc.ui.screen.notelist

import org.igo.mycorc.domain.model.Note

data class NoteListState(
    val isLoading: Boolean = false,
    val notes: List<Note> = emptyList() // 👈 Добавили список
)