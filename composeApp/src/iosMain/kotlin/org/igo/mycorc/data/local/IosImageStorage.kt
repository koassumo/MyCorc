package org.igo.mycorc.data.local

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.posix.memcpy // 👈 1. Вот этот важный импорт

class IosImageStorage : ImageStorage {

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override suspend fun saveImage(bytes: ByteArray): String {
        // 1. Получаем папку документов
        val fileManager = NSFileManager.defaultManager
        val urls = fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
        val documentsUrl = urls.first() as? NSURL ?: return ""

        // 2. Генерируем имя файла
        val fileName = "photo_${NSDate().timeIntervalSince1970}.jpg"
        val fileUrl = documentsUrl.URLByAppendingPathComponent(fileName) ?: return ""

        // 3. Конвертируем ByteArray в NSData
        val nsData = bytes.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), bytes.size.toULong())
        }

        // 4. Записываем файл
        nsData.writeToURL(fileUrl, true)

        // 5. Возвращаем путь
        return fileUrl.path ?: ""
    }

    // Метод getImage я убрал, так как его нет в интерфейсе ImageStorage.
    // Если он понадобится в будущем, мы сначала добавим его в интерфейс.
}

// Вспомогательная функция (расширение) должна быть вне класса или внутри companion object,
// но в Kotlin можно и просто внизу файла.
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun NSData.toByteArray(): ByteArray {
    return ByteArray(this.length.toInt()).apply {
        usePinned { pinned ->
            memcpy(pinned.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }
}