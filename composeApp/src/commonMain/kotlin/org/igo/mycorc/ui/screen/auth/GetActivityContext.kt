package org.igo.mycorc.ui.screen.auth

import androidx.compose.runtime.Composable

/**
 * Возвращает Activity context на Android, null на других платформах.
 * Используется для платформо-специфичных операций (например, Google Sign-In на Android).
 */
@Composable
expect fun getActivityContext(): Any?
