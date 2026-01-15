package org.igo.mycorc.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.igo.mycorc.domain.model.AppUser
import org.igo.mycorc.domain.rep_interface.AuthRepository

class DesktopAuthStub : AuthRepository {

    override val currentUser: Flow<AppUser?> = flowOf(null)

    override suspend fun login(email: String, pass: String): Result<Unit> {
        return Result.failure(UnsupportedOperationException("Firebase Auth is not available on Desktop"))
    }

    override suspend fun register(email: String, pass: String): Result<Unit> {
        return Result.failure(UnsupportedOperationException("Firebase Auth is not available on Desktop"))
    }

    override suspend fun logout() {}

    override suspend fun getIdTokenOrNull(): String? = null

}
