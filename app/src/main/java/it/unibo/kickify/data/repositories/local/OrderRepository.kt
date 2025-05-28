package it.unibo.kickify.data.repositories.local

import androidx.room.withTransaction
import it.unibo.kickify.data.database.*

class OrderRepository(
    private val orderDao: OrderDao
) {
    suspend fun insertOrder(order: Order) {
        orderDao.insertOrder(order)
    }

    suspend fun getOrders(email: String): List<Order> =
        orderDao.getOrders(email)

    suspend fun getOrdersWithProducts(email: String): List<OrderProductDetails> =
        orderDao.getOrdersWithProducts(email)

    suspend fun getOrderTracking(orderId: Int): OrderDetailedTracking =
        orderDao.getOrderTracking(orderId)

    suspend fun insertOrderProduct(orderProduct: OrderProduct) {
        orderDao.insertOrderProduct(orderProduct)
    }

    suspend fun insertTrackingInfo(trackingShipping: TrackingShipping) {
        orderDao.insertTrackingInfo(trackingShipping)
    }
}