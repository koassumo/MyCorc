package org.igo.mycorc.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.igo.mycorc.data.auth.AuthStorage
import org.igo.mycorc.data.auth.dto.RefreshTokenResponseDto
import org.igo.mycorc.data.auth.dto.SignInUpRequestDto
import org.igo.mycorc.data.auth.dto.SignInUpResponseDto
import org.igo.mycorc.domain.model.AppUser
import org.igo.mycorc.domain.rep_interface.AuthRepository
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import org.igo.mycorc.core.time.TimeProvider
import org.igo.mycorc.BuildKonfig
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
class AuthRepositoryRestImpl(
    private val client: HttpClient,
    private val storage: AuthStorage,
    private val timeProvider: TimeProvider
) : AuthRepository {

    private val apiKey: String = BuildKonfig.FIREBASE_API_KEY.also {
        println("🔑 Firebase API Key loaded: ${it.take(10)}...")
    }
    private val signUpUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=$apiKey"
    private val signInUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$apiKey"
    private val refreshUrl = "https://securetoken.googleapis.com/v1/token?key=$apiKey"

    private val _currentUser = MutableStateFlow<AppUser?>(null)
    override val currentUser: Flow<AppUser?> = _currentUser.asStateFlow()

    init {
        val uid = storage.userIdOrNull()
        val email = storage.emailOrNull()
        if (uid != null && email != null) {
            _currentUser.value = AppUser(id = uid, email = email)
        }
    }

    override suspend fun login(email: String, pass: String): Result<Unit> =
        runCatching {
            println("🔐 Attempting login for: $email")
            val httpResponse = client.post(signInUrl) {
                contentType(ContentType.Application.Json)
                setBody(SignInUpRequestDto(email, pass))
            }
            val rawBody = httpResponse.body<String>()
            println("📡 Firebase response: $rawBody")

            val resp = Json.decodeFromString<SignInUpResponseDto>(rawBody)
            println("✅ Login successful")
            persistSession(resp)
        }.onFailure { error ->
            println("❌ Login failed: ${error.message}")
            error.printStackTrace()
        }

    override suspend fun register(email: String, pass: String): Result<Unit> =
        runCatching {
            val resp: SignInUpResponseDto = client.post(signUpUrl) {
                contentType(ContentType.Application.Json)
                setBody(SignInUpRequestDto(email, pass))
            }.body()
            persistSession(resp)
        }

    override suspend fun logout() {
        storage.clear()
        _currentUser.value = null
    }


    override suspend fun getIdTokenOrNull(): String? {
        val token = storage.idTokenOrNull() ?: return null
        val refresh = storage.refreshTokenOrNull() ?: return null

        val now = timeProvider.nowEpochSeconds()

        val exp = storage.expiresAtEpochSecOrNull() ?: decodeJwtExpOrNull(token)

        if (exp != null && exp - now <= 60L) {
            return refreshIdToken(refresh)
        }
        return token
    }

    private fun persistSession(resp: SignInUpResponseDto) {
        val now = timeProvider.nowEpochSeconds()


        val expiresAt = now + (resp.expiresIn.toLongOrNull() ?: 3600L)

        storage.save(
            idToken = resp.idToken,
            refreshToken = resp.refreshToken,
            userId = resp.localId,
            email = resp.email,
            expiresAtEpochSec = expiresAt
        )
        _currentUser.value = AppUser(id = resp.localId, email = resp.email)
    }

    private suspend fun refreshIdToken(refreshToken: String): String? =
        try {
            val resp: RefreshTokenResponseDto = client.post(refreshUrl) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("grant_type", "refresh_token")
                            append("refresh_token", refreshToken)
                        }
                    )
                )
            }.body()

            val now = timeProvider.nowEpochSeconds()
            val expiresAt = now + (resp.expiresIn.toLongOrNull() ?: 3600L)

            storage.save(
                idToken = resp.idToken,
                refreshToken = resp.refreshToken,
                userId = resp.userId,
                email = storage.emailOrNull() ?: "",
                expiresAtEpochSec = expiresAt
            )
            _currentUser.value = AppUser(id = resp.userId, email = storage.emailOrNull() ?: "")

            resp.idToken
        } catch (_: Throwable) {
            logout()
            null
        }

    @OptIn(ExperimentalEncodingApi::class)
    private fun decodeJwtExpOrNull(jwt: String): Long? =
        try {
            val payload = jwt.split(".")[1]
            val decoded = Base64.UrlSafe.decode(payload + "=".repeat((4 - payload.length % 4) % 4))
                .decodeToString()
            val json = Json.parseToJsonElement(decoded).jsonObject
            json["exp"]?.jsonPrimitive?.content?.toLong()
        } catch (_: Throwable) {
            null
        }
}
