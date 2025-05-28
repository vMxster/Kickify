package it.unibo.kickify.data.repositories.local

import it.unibo.kickify.data.database.*

class CartRepository(private val cartDao: CartDao) {

    suspend fun getCartByEmail(email: String): Cart? =
        cartDao.getCartByEmail(email)

    suspend fun updateCartTotal(cartId: Int): Int =
        cartDao.updateCartTotal(cartId)

    suspend fun insertCart(cart: Cart) =
        cartDao.insertCart(cart)

    suspend fun clearCart(cartId: Int) =
        cartDao.clearCart(cartId)
}