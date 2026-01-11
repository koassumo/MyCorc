package org.igo.mycorc.data.local

interface ImageStorage {
    suspend fun saveImage(bytes: ByteArray): String
}