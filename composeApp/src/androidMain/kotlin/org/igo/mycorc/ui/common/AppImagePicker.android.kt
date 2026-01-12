package org.igo.mycorc.ui.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun AppImagePicker(onImagePicked: (ByteArray) -> Unit) {
    val context = LocalContext.current

    // Храним Uri временного файла, куда камера будет писать
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    // 1. Лаунчер для КАМЕРЫ (срабатывает, когда камера закрылась)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != null) {
            val imageBytes = readBytesFromUri(context, tempImageUri!!)
            if (imageBytes != null) {
                onImagePicked(imageBytes)
            }
        }
    }

    // 2. Лаунчер для ЗАПРОСА ПРАВ (срабатывает, когда юзер нажал "Разрешить" или "Запретить")
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Ура, разрешили! Сразу запускаем камеру, чтобы юзеру не пришлось тыкать второй раз
            tempImageUri = createTempUri(context)
            tempImageUri?.let { cameraLauncher.launch(it) }
        } else {
            Toast.makeText(context, "Без доступа к камере фото сделать нельзя", Toast.LENGTH_SHORT).show()
        }
    }

    Button(
        onClick = {
            // 3. ПРОВЕРКА ПРАВ перед кликом
            val permissionStatus = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            )

            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                // Права уже есть — просто работаем
                tempImageUri = createTempUri(context)
                tempImageUri?.let { cameraLauncher.launch(it) }
            } else {
                // Прав нет — вызываем системное диалоговое окно
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    ) {
        Text("Сделать фото")
    }
}

// --- Вспомогательные функции ---

private fun createTempUri(context: Context): Uri? {
    return try {
        val tempFile = File.createTempFile("camera_photo_", ".jpg", context.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        // ВАЖНО: authority должен совпадать с тем, что в AndroidManifest.xml
        // У тебя там "${applicationId}.fileprovider". Если appId = "org.igo.mycorc", то всё ок.
        FileProvider.getUriForFile(
            context,
            "org.igo.mycorc.fileprovider",
            tempFile
        )
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Ошибка создания файла: ${e.message}", Toast.LENGTH_SHORT).show()
        null
    }
}

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