package org.igo.mycorc.data.local

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.posix.memcpy

class IosImageStorage : ImageStorage {
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override suspend fun saveImage(bytes: ByteArray): String {
        val fileName = "img_${NSUUID().UUIDString}.jpg"
        // Получаем папку Documents
        val docsDir = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true).first() as String
        val fullPath = "$docsDir/$fileName"

        // Магия превращения Kotlin ByteArray в iOS NSData
        val data = bytes.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), bytes.size.toULong())
        }

        data.writeToFile(fullPath, true)
        return fullPath
    }
}