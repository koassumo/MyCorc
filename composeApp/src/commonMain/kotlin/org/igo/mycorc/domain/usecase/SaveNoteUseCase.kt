package org.igo.mycorc.domain.usecase

import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.rep_interface.NoteRepository

class SaveNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) {
        repository.saveNote(note)
    }
}