package org.igo.mycorc.domain.usecase

import org.igo.mycorc.domain.rep_interface.NoteSyncRepository

class SyncSingleNoteUseCase(private val repository: NoteSyncRepository) {
    suspend operator fun invoke(noteId: String): Result<Unit> {
        return repository.syncSingleNoteFromServer(noteId)
    }
}
