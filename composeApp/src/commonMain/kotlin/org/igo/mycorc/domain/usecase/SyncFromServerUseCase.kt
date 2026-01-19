package org.igo.mycorc.domain.usecase

import org.igo.mycorc.domain.rep_interface.NoteSyncRepository

class SyncFromServerUseCase(private val repository: NoteSyncRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.syncFromServer()
    }
}
