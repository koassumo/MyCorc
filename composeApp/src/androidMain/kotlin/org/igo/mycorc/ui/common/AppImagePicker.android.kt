package org.igo.mycorc.ui.common

// 👇 ВОТ ЭТИ ИМПОРТЫ КРИТИЧЕСКИ ВАЖНЫ, ИХ НЕ ХВАТАЛО
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun AppImagePicker(onImagePicked: (ByteArray) -> Unit) {
    val context = LocalContext.current

    // Храним Uri временного файла, куда камера сохранит фото
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    // Создаем лаунчер для запуска камеры
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != null) {
            // Если фото сделано успешно, читаем байты из файла
            val imageBytes = readBytesFromUri(context, tempImageUri!!)
            if (imageBytes != null) {
                onImagePicked(imageBytes)
            }
        }
    }

    Button(
        onClick = {
            try {
                // 1. Создаем временный файл
                val tempFile = File.createTempFile("camera_photo_", ".jpg", context.cacheDir).apply {
                    createNewFile()
                    deleteOnExit()
                }

                // 2. Получаем URI через FileProvider
                // ВАЖНО: authority должен совпадать с тем, что в AndroidManifest.xml
                val uri = FileProvider.getUriForFile(
                    context,
                    "org.igo.mycorc.fileprovider",
                    tempFile
                )

                // 3. Запоминаем URI и запускаем камеру
                tempImageUri = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    ) {
        Text("Сделать фото")
    }
}

// Вспомогательная функция для чтения байтов
private fun readBytesFromUri(context: Context, uri: Uri): ByteArray? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}