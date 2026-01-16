package org.igo.mycorc.domain.usecase

import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.rep_interface.NoteSyncRepository

class SyncNoteUseCase(private val repository: NoteSyncRepository) {
    suspend operator fun invoke(note: Note): Result<Unit> = repository.syncNote(note)
}
