package org.igo.mycorc.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.igo.mycorc.domain.model.AppUser
import org.igo.mycorc.domain.rep_interface.AuthRepository

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: Flow<AppUser?> = firebaseAuth.authStateChanged.map { firebaseUser ->
        firebaseUser?.toAppUser()
    }

    override suspend fun login(email: String, pass: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, pass)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, pass: String): Result<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, pass)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    private fun FirebaseUser.toAppUser(): AppUser {
        return AppUser(id = uid, email = email)
    }
}
