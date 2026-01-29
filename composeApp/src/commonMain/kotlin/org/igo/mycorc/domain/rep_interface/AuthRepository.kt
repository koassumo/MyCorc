package org.igo.mycorc.domain.rep_interface

import kotlinx.coroutines.flow.Flow
import org.igo.mycorc.domain.model.AppUser

interface AuthRepository {
    val currentUser: Flow<AppUser?>

    suspend fun login(email: String, pass: String): Result<Unit>
    suspend fun register(email: String, pass: String): Result<Unit>
    suspend fun signInWithGoogle(activityContext: Any): Result<Unit>
    suspend fun logout()

    // Нужно для REST-запросов (Firestore и т.п.)
    suspend fun getIdTokenOrNull(): String?
}
