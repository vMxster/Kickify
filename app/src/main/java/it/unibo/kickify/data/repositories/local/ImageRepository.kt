package it.unibo.kickify.data.repositories.local

import it.unibo.kickify.data.database.Image
import it.unibo.kickify.data.database.ImageDao

class ImageRepository (private val imageDao: ImageDao) {

    suspend fun insertImage(image: Image) {
        imageDao.insertImage(image)
    }
}