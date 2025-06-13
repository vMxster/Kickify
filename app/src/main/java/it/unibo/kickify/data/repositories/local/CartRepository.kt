package it.unibo.kickify.data.repositories.local

import android.database.sqlite.SQLiteConstraintException
import it.unibo.kickify.data.database.*

class CartRepository(private val cartDao: CartDao) {

    suspend fun getCartByEmail(email: String): Cart? =
        cartDao.getCartByEmail(email)

    suspend fun updateCartTotal(cartId: Int): Int {
        try {
            return cartDao.updateCartTotal(cartId)
        } catch (e: SQLiteConstraintException) {
            cartDao.clearCart(cartId)
            return 1
        }
    }

    suspend fun insertCart(cart: Cart) {
        cartDao.insertCart(cart)
    }

    suspend fun clearCart(cartId: Int) =
        cartDao.clearCart(cartId)
}