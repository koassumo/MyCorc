package org.igo.mycorc.ui.common

import androidx.compose.runtime.Composable

/**
 * Кроссплатформенная обёртка над обработчиком кнопки "Назад".
 *
 * - Android: делегирует к androidx.activity.compose.BackHandler
 * - iOS / Desktop: пустая реализация (нет физической кнопки "Назад")
 *
 * @param enabled Включён ли обработчик. Когда false, не перехватывает нажатие.
 * @param onBack Callback, вызываемый при нажатии кнопки "Назад".
 */
@Composable
expect fun AppBackHandler(enabled: Boolean = true, onBack: () -> Unit)
