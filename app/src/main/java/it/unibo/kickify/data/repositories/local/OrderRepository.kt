package it.unibo.kickify.data.repositories.local

import androidx.room.withTransaction
import it.unibo.kickify.data.database.*

class OrderRepository(
    private val orderDao: OrderDao,
    private val database: KickifyDatabase
) {

    suspend fun insertOrder(order: Order, products: List<OrderProduct>): Long {
        return database.withTransaction {
            val orderId = orderDao.insertOrder(order)
            products.forEach { product ->
                orderDao.insertOrderProduct(product.copy(orderId = orderId.toInt()))
            }
            orderId
        }
    }

    suspend fun getOrders(email: String): List<Order> =
        orderDao.getOrders(email)

    suspend fun getOrdersWithProducts(email: String): List<OrderProductDetails> =
        orderDao.getOrdersWithProducts(email)

    suspend fun getOrderTracking(orderId: Int): OrderDetailedTracking =
        orderDao.getOrderTracking(orderId)
}