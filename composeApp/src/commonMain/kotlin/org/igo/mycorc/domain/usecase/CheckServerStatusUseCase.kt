package org.igo.mycorc.domain.usecase

import org.igo.mycorc.domain.model.NoteStatus
import org.igo.mycorc.domain.rep_interface.NoteSyncRepository

class CheckServerStatusUseCase(private val repository: NoteSyncRepository) {
    suspend operator fun invoke(noteId: String): Result<NoteStatus?> {
        return repository.checkServerStatus(noteId)
    }
}
