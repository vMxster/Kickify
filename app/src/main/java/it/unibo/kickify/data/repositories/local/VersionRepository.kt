package it.unibo.kickify.data.repositories.local

import it.unibo.kickify.data.database.Version
import it.unibo.kickify.data.database.VersionDao

class VersionRepository(private val versionDao: VersionDao) {
    suspend fun insertProductVariant(variant: Version) =
        versionDao.insertProductVariant(variant)
}