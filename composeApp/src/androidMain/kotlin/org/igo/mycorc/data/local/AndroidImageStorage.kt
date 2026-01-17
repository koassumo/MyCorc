package org.igo.mycorc.data.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class AndroidImageStorage(private val context: Context) : ImageStorage {
    override suspend fun saveImage(bytes: ByteArray): String = withContext(Dispatchers.IO) {
        val fileName = "img_${UUID.randomUUID()}.jpg"
        // Сохраняем во внутреннюю приватную папку приложения
        val file = File(context.filesDir, fileName)
        file.writeBytes(bytes)
        return@withContext file.absolutePath
    }

    override suspend fun deleteImage(path: String) {
        withContext(Dispatchers.IO) {
            File(path).delete()
        }
    }
}