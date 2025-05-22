package it.unibo.kickify.data.repositories.local

import it.unibo.kickify.data.database.*

class CartRepository(private val cartDao: CartDao) {

    suspend fun getCartByEmail(email: String): Cart? =
        cartDao.getCartByEmail(email)

    suspend fun addToCart(email: String, productId: Int, color: String, size: Double, quantity: Int = 1) =
        cartDao.addToCart(email, productId, color, size, quantity)

    suspend fun removeFromCart(cartId: Int, productId: Int, color: String, size: Double): Int =
        cartDao.removeFromCart(cartId, productId, color, size)

    suspend fun getCartItems(cartId: Int): List<CartWithProductInfo> =
        cartDao.getCartItems(cartId)

    suspend fun updateCartItemQuantity(cartId: Int, productId: Int, color: String, size: Double, quantity: Int): Int =
        cartDao.updateCartItemQuantity(cartId, productId, color, size, quantity)

    suspend fun clearCart(cartId: Int): Int =
        cartDao.clearCart(cartId)

    suspend fun updateCartTotal(cartId: Int): Int =
        cartDao.updateCartTotal(cartId)
}