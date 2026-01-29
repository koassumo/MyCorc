package org.igo.mycorc.data.auth

/**
 * Desktop implementation - OAuth через браузер.
 * TODO: Реализовать позже
 */
actual class GoogleAuthProvider actual constructor() {
    actual suspend fun signIn(activityContext: Any): Result<String> {
        return Result.failure(Exception("Google Sign-In на Desktop пока не реализован"))
    }

    actual suspend fun signOut() {
        // TODO: Implement
    }
}
