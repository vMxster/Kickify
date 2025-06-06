package it.unibo.kickify.data.repositories.local

import it.unibo.kickify.data.database.UserOAuth
import it.unibo.kickify.data.database.UserOAuthDao


class OAuthUserRepository(private val oauthDao: UserOAuthDao) {

    suspend fun insertUserOAuth(user: UserOAuth) =
        oauthDao.insertUserOAuth(user)

    suspend fun getUserOAuth(email: String): UserOAuth? =
        oauthDao.getUserOAuth(email)

    suspend fun checkUserOAuthExists(email: String): Boolean {
        return oauthDao.getUserOAuth(email) != null
    }
}