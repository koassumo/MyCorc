package org.igo.mycorc.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.material3.Button
import androidx.compose.material3.Text

@Composable
actual fun AppImagePicker(onImagePicked: (ByteArray) -> Unit) {
    // Просто кнопка, которая говорит "Недоступно"
    Button(
        onClick = { /* Ничего не делаем */ },
        enabled = false // Делаем кнопку серой
    ) {
        Text("Фото (Только на телефоне)")
    }
}