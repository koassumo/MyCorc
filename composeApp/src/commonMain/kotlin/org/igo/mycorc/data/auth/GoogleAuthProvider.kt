package org.igo.mycorc.data.auth

/**
 * Platform-specific Google Sign-In provider.
 * Returns Google ID Token that can be used to authenticate with Firebase.
 */
expect class GoogleAuthProvider() {
    /**
     * Launches Google Sign-In flow and returns Google ID Token.
     * @param activityContext Platform-specific activity context (Activity on Android)
     * @return Google ID Token on success, null if cancelled or failed
     */
    suspend fun signIn(activityContext: Any): Result<String>

    /**
     * Signs out from Google account.
     */
    suspend fun signOut()
}
