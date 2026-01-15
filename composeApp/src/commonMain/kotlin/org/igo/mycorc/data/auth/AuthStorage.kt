package org.igo.mycorc.data.auth

import com.russhwolf.settings.Settings

class AuthStorage(private val settings: Settings) {

    private companion object {
        const val KEY_ID_TOKEN = "auth_id_token"
        const val KEY_REFRESH_TOKEN = "auth_refresh_token"
        const val KEY_USER_ID = "auth_user_id"
        const val KEY_EMAIL = "auth_email"
        const val KEY_EXPIRES_AT = "auth_expires_at_epoch_sec"
    }

    fun save(
        idToken: String,
        refreshToken: String,
        userId: String,
        email: String,
        expiresAtEpochSec: Long
    ) {
        settings.putString(KEY_ID_TOKEN, idToken)
        settings.putString(KEY_REFRESH_TOKEN, refreshToken)
        settings.putString(KEY_USER_ID, userId)
        settings.putString(KEY_EMAIL, email)
        settings.putLong(KEY_EXPIRES_AT, expiresAtEpochSec)
    }

    fun clear() {
        settings.remove(KEY_ID_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_EMAIL)
        settings.remove(KEY_EXPIRES_AT)
    }

    fun idTokenOrNull(): String? = settings.getStringOrNull(KEY_ID_TOKEN)
    fun refreshTokenOrNull(): String? = settings.getStringOrNull(KEY_REFRESH_TOKEN)
    fun userIdOrNull(): String? = settings.getStringOrNull(KEY_USER_ID)
    fun emailOrNull(): String? = settings.getStringOrNull(KEY_EMAIL)
    fun expiresAtEpochSecOrNull(): Long? =
        if (settings.hasKey(KEY_EXPIRES_AT)) settings.getLong(KEY_EXPIRES_AT, 0L) else null
}
