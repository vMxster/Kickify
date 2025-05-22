package it.unibo.kickify.data.repositories

import it.unibo.kickify.data.database.*
import it.unibo.kickify.data.repositories.local.*

class AppRepository(
    private val remoteRepository: RemoteRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val wishlistRepository: WishlistRepository,
    private val reviewRepository: ReviewRepository,
    private val notificationRepository: NotificationRepository
) {
    // PRODOTTI
    suspend fun getProducts(lastAccess: String): Result<List<Product>> {
        return remoteRepository.getProducts(lastAccess)
    }

    suspend fun getProductData(productId: Int, userEmail: String?, lastAccess: String): Result<ProductDetails> {
        return remoteRepository.getProductData(productId, userEmail, lastAccess)
    }

    suspend fun getProductById(productId: Int, lastAccess: String): Result<ProductDetails> {
        return remoteRepository.getProductById(productId, lastAccess)
    }

    suspend fun getProductHistory(productId: Int, lastAccess: String): Result<List<HistoryProduct>> {
        return remoteRepository.getProductHistory(productId, lastAccess)
    }

    // CARRELLO
    suspend fun getCart(email: String): Result<Cart> {
        return remoteRepository.getCart(email)
    }

    suspend fun getCartItems(email: String, lastAccess: String): Result<List<CartProduct>> {
        return remoteRepository.getCartItems(email, lastAccess)
    }

    suspend fun addToCart(email: String, productId: Int, color: String, size: Double, quantity: Int = 1): Result<Boolean> {
        val result = remoteRepository.addToCart(email, productId, color, size, quantity)
        if (result.isSuccess && result.getOrNull() == true) {
            cartRepository.addToCart(email, productId, color, size, quantity)
        }
        return result
    }

    suspend fun removeFromCart(email: String, productId: Int, color: String, size: Double): Result<Boolean> {
        val cart = cartRepository.getCartByEmail(email)
        val result = remoteRepository.removeFromCart(email, productId, color, size)
        if (result.isSuccess && result.getOrNull() == true && cart != null) {
            cartRepository.removeFromCart(cart.cartId, productId, color, size)
        }
        return result
    }

    // WISHLIST
    suspend fun getWishlistItems(email: String): Result<List<WishlistProduct>> {
        return remoteRepository.getWishlistItems(email)
    }

    suspend fun addToWishlist(email: String, productId: Int): Result<Boolean> {
        val result = remoteRepository.addToWishlist(email, productId)
        if (result.isSuccess && result.getOrNull() == true) {
            wishlistRepository.addToWishlist(email, productId)
        }
        return result
    }

    suspend fun removeFromWishlist(email: String, productId: Int): Result<Boolean> {
        val result = remoteRepository.removeFromWishlist(email, productId)
        if (result.isSuccess && result.getOrNull() == true) {
            wishlistRepository.removeFromWishlist(email, productId)
        }
        return result
    }

    suspend fun clearWishlist(email: String): Result<Boolean> {
        val result = remoteRepository.clearWishlist(email)
        if (result.isSuccess && result.getOrNull() == true) {
            wishlistRepository.clearWishlist(email)
        }
        return result
    }

    // NOTIFICHE
    suspend fun getNotifications(email: String, lastAccess: String): Result<List<Notification>> {
        return remoteRepository.getNotifications(email, lastAccess)
    }

    suspend fun createNotification(email: String, message: String, type: String): Result<Boolean> {
        return remoteRepository.createNotification(email, message, type)
    }

    suspend fun markNotificationsAsRead(notificationIds: Array<Int>): Result<Boolean> {
        return remoteRepository.markNotificationsAsRead(notificationIds)
    }

    // ORDINI
    suspend fun getOrders(email: String, lastAccess: String): Result<List<OrderDetails>> {
        return remoteRepository.getOrders(email, lastAccess)
    }

    suspend fun placeOrder(
        email: String,
        total: Double,
        paymentMethod: String,
        shippingType: String,
        isGift: Boolean = false,
        giftFirstName: String? = null,
        giftLastName: String? = null
    ): Result<Int> {
        return remoteRepository.placeOrder(
            email, total, paymentMethod, shippingType,
            isGift, giftFirstName, giftLastName
        )
    }

    suspend fun getOrderTracking(orderId: Int): Result<OrderTracking> {
        return remoteRepository.getOrderTracking(orderId)
    }

    // RECENSIONI
    suspend fun addReview(
        email: String,
        productId: Int,
        rating: Int,
        comment: String
    ): Result<Boolean> {
        val result = remoteRepository.addReview(email, productId, rating, comment)
        if (result.isSuccess && result.getOrNull() == true) {
            val review = Review(
                productId = productId,
                email = email,
                vote = rating.toDouble(),
                comment = comment,
                reviewDate = java.time.LocalDateTime.now().toString()
            )
            reviewRepository.addReview(review)
        }
        return result
    }

    suspend fun deleteReview(email: String, productId: Int): Result<Boolean> {
        val result = remoteRepository.deleteReview(email, productId)
        if (result.isSuccess && result.getOrNull() == true) {
            reviewRepository.deleteReview(email, productId)
        }
        return result
    }

    suspend fun getReviews(productId: Int, lastAccess: String): Result<List<Review>> {
        return remoteRepository.getReviews(productId, lastAccess)
    }

    suspend fun getProductRating(productId: Int): Result<Double> {
        return remoteRepository.getProductRating(productId)
    }

    // AUTENTICAZIONE
    suspend fun login(email: String, password: String): Result<User> {
        return remoteRepository.login(email, password)
    }

    suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        newsletter: Boolean,
        phone: String? = null
    ): Result<Boolean> {
        return remoteRepository.register(email, firstName, lastName, password, newsletter, phone)
    }

    suspend fun changePassword(email: String, password: String): Result<Boolean> {
        val result = remoteRepository.changePassword(email, password)
        if (result.isSuccess && result.getOrNull() == true) {
            userRepository.changePassword(email, password)
        }
        return result
    }

    suspend fun getUserProfile(email: String): Result<User> {
        return remoteRepository.getUserProfile(email)
    }

    suspend fun isUserRegistered(email: String): Result<Boolean> {
        return remoteRepository.isUserRegistered(email)
    }
}