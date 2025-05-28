package it.unibo.kickify.data.repositories.local

import it.unibo.kickify.data.database.*

class UserRepository(private val userDao: UserDao) {

    suspend fun getUserProfile(email: String): User? =
        userDao.getUserProfile(email)

    suspend fun registerUser(user: User): Long =
        userDao.registerUser(user)

    suspend fun changePassword(email: String, password: String): Int =
        userDao.changePassword(email, password)
}