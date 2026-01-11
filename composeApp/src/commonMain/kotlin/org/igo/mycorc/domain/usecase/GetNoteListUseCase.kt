package org.igo.mycorc.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.rep_interface.NoteRepository

class GetNoteListUseCase(private val repository: NoteRepository) {
    // Оператор invoke позволяет вызывать класс как функцию: getNoteListUseCase()
    operator fun invoke(): Flow<List<Note>> {
        return repository.getAllNotes()
    }
}
