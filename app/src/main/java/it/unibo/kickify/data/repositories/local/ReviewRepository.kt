package it.unibo.kickify.data.repositories.local

import it.unibo.kickify.data.database.*

class ReviewRepository(private val reviewDao: ReviewDao) {
    suspend fun canUserReview(email: String, productId: Int): Boolean =
        reviewDao.canUserReview(email, productId) > 0
}