package org.igo.mycorc.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.rep_interface.NoteRepository

class GetNoteByIdUseCase(private val repository: NoteRepository) {
    operator fun invoke(noteId: String): Flow<Note?> {
        return repository.getNoteById(noteId)
    }
}
