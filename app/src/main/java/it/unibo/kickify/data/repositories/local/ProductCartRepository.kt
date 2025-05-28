package it.unibo.kickify.data.repositories.local

import it.unibo.kickify.data.database.CartWithProductInfo
import it.unibo.kickify.data.database.ProductCartDao

class ProductCartRepository (private val productCartDao: ProductCartDao) {

    suspend fun getCartItems(cartId: Int): List<CartWithProductInfo> =
        productCartDao.getCartItems(cartId)

    suspend fun addToCart(cartId: Int?, productId: Int, color: String, size: Double, quantity: Int = 1) =
        productCartDao.addToCart(cartId, productId, color, size, quantity)

    suspend fun removeFromCart(cartId: Int, productId: Int, color: String, size: Double): Int =
        productCartDao.removeFromCart(cartId, productId, color, size)

    suspend fun clearCart(cartId: Int) =
        productCartDao.clearCart(cartId)
}