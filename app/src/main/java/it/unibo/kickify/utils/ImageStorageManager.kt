package it.unibo.kickify.utils

import android.content.Context
import java.io.File

class ImageStorageManager(private val context: Context) {

    fun saveAll(images: List<Pair<String, ByteArray>>): List<Pair<String, String>> {
        return images.map { (url, bytes) ->
            val path = saveImageLocally(url, bytes)
            url to path
        }
    }

    private fun saveImageLocally(url: String, bytes: ByteArray): String {
        val fileName = "img_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        file.writeBytes(bytes)
        return file.absolutePath
    }
}
