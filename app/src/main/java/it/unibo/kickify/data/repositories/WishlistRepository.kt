package it.unibo.kickify.data.repositories

import it.unibo.kickify.data.database.*

class WishlistRepository(private val wishlistDao: WishlistDao) {

    suspend fun getWishlistItems(email: String): List<Product> =
        wishlistDao.getWishlistItems(email)

    suspend fun addToWishlist(email: String, productId: Int): Long =
        wishlistDao.addToWishlist(WishlistProduct(productId, email))

    suspend fun removeFromWishlist(email: String, productId: Int): Int =
        wishlistDao.removeFromWishlist(email, productId)

    suspend fun clearWishlist(email: String): Int =
        wishlistDao.clearWishlist(email)

    suspend fun isInWishlist(email: String, productId: Int): Boolean =
        wishlistDao.isInWishlist(email, productId) > 0
}