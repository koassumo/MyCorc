package org.igo.mycorc.data.local

import java.io.File
import java.util.UUID

class JvmImageStorage : ImageStorage {
    override suspend fun saveImage(bytes: ByteArray): String {
        val folder = File("mycorc_images")
        if (!folder.exists()) folder.mkdirs()

        val fileName = "img_${UUID.randomUUID()}.jpg"
        val file = File(folder, fileName)
        file.writeBytes(bytes)
        return file.absolutePath
    }

    override suspend fun deleteImage(path: String) {
        File(path).delete()
    }
}