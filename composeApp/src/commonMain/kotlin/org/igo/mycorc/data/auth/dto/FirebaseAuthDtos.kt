package org.igo.mycorc.data.auth.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInUpRequestDto(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean = true
)

@Serializable
data class SignInUpResponseDto(
    val idToken: String,
    val email: String,
    val refreshToken: String,
    val expiresIn: String,
    val localId: String,
    val registered: Boolean? = null,
    val kind: String? = null,
    val displayName: String? = null
)

@Serializable
data class RefreshTokenResponseDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("id_token") val idToken: String,
    @SerialName("user_id") val userId: String,
    @SerialName("project_id") val projectId: String? = null
)

/**
 * Request for signInWithIdp (OAuth providers like Google, Apple)
 */
@Serializable
data class SignInWithIdpRequestDto(
    val postBody: String,
    val requestUri: String = "http://localhost",
    val returnIdpCredential: Boolean = true,
    val returnSecureToken: Boolean = true
)

/**
 * Response from signInWithIdp
 */
@Serializable
data class SignInWithIdpResponseDto(
    val idToken: String,
    val email: String? = null,
    val refreshToken: String,
    val expiresIn: String,
    val localId: String,
    val providerId: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val federatedId: String? = null,
    val emailVerified: Boolean? = null,
    val rawUserInfo: String? = null
)
