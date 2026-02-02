package org.igo.mycorc.ui.common

import androidx.compose.runtime.Composable

@Composable
actual fun AppBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Desktop не имеет физической кнопки "Назад"
}
