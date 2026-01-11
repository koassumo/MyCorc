package org.igo.mycorc.ui.common

import androidx.compose.runtime.Composable

// expect - означает "Ожидаем реализацию".
// Мы обещаем общему коду, что на каждой платформе такая функция будет.
@Composable
expect fun AppImagePicker(
    onImagePicked: (ByteArray) -> Unit
)