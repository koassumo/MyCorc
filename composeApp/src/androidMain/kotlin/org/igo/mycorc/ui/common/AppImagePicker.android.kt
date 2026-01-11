package org.igo.mycorc.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import androidx.compose.material3.Button
import androidx.compose.material3.Text

@Composable
actual fun AppImagePicker(onImagePicked: (ByteArray) -> Unit) {
    val scope = rememberCoroutineScope()

    // Вот она, библиотека Peekaboo, живет только тут
    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays: List<ByteArray> ->
            if (byteArrays.isNotEmpty()) {
                onImagePicked(byteArrays[0])
            }
        }
    )

    Button(onClick = { singleImagePicker.launch() }) {
        Text("Добавить фото")
    }
}