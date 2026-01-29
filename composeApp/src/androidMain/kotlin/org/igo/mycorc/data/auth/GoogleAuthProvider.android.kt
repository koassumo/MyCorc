package org.igo.mycorc.data.auth

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import org.igo.mycorc.BuildKonfig

actual class GoogleAuthProvider actual constructor() {
    private val webClientId = BuildKonfig.GOOGLE_WEB_CLIENT_ID

    actual suspend fun signIn(activityContext: Any): Result<String> {
        val activity = activityContext as? Activity
            ?: return Result.failure(Exception("Требуется Activity context"))

        val credentialManager = CredentialManager.create(activity)

        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )

            handleSignInResult(result)
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("Вход отменён"))
        } catch (e: NoCredentialException) {
            Result.failure(Exception("Google аккаунт не найден. Добавьте аккаунт в настройках устройства."))
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Ошибка входа через Google: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Неизвестная ошибка: ${e.message}"))
        }
    }

    private fun handleSignInResult(result: GetCredentialResponse): Result<String> {
        val credential = result.credential

        return when {
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    Result.success(idToken)
                } catch (e: GoogleIdTokenParsingException) {
                    Result.failure(Exception("Ошибка парсинга токена: ${e.message}"))
                }
            }
            else -> {
                Result.failure(Exception("Неподдерживаемый тип credential"))
            }
        }
    }

    actual suspend fun signOut() {
        // Credential Manager state will be cleared automatically
        // when user signs in with a different account next time.
        // Firebase logout already clears our local session.
    }
}
