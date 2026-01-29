package org.igo.mycorc.data.auth

/**
 * iOS implementation - Firebase Auth SDK.
 * TODO: Реализовать позже
 */
actual class GoogleAuthProvider actual constructor() {
    actual suspend fun signIn(activityContext: Any): Result<String> {
        return Result.failure(Exception("Google Sign-In на iOS пока не реализован"))
    }

    actual suspend fun signOut() {
        // TODO: Implement
    }
}
