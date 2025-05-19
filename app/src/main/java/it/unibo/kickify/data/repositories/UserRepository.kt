package it.unibo.kickify.data.repositories

import it.unibo.kickify.data.database.*

class UserRepository(private val userDao: UserDao) {

    suspend fun getUserProfile(email: String): User? =
        userDao.getUserProfile(email)

    suspend fun registerUser(user: User): Long =
        userDao.registerUser(user)

    suspend fun loginUser(email: String, password: String): User? =
        userDao.loginUser(email, password)

    suspend fun changePassword(email: String, password: String): Int =
        userDao.changePassword(email, password)

    suspend fun checkUserExists(email: String): String? =
        userDao.checkUserExists(email)
}