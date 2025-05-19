package it.unibo.kickify.data.repositories

import it.unibo.kickify.data.database.*

class ReviewRepository(private val reviewDao: ReviewDao) {

    suspend fun addReview(review: Review): Long =
        reviewDao.addReview(review)

    suspend fun getProductReviews(productId: Int, lastAccess: String): List<ReviewWithUserInfo> =
        reviewDao.getProductReviews(productId, lastAccess)

    suspend fun deleteReview(email: String, productId: Int): Int =
        reviewDao.deleteReview(email, productId)

    suspend fun getProductRating(productId: Int): Double? =
        reviewDao.getProductRating(productId)

    suspend fun canUserReview(email: String, productId: Int): Boolean =
        reviewDao.canUserReview(email, productId) > 0
}