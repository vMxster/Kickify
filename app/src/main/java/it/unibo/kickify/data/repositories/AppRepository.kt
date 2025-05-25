package it.unibo.kickify.data.repositories

import android.content.Context
import android.util.Log
import it.unibo.kickify.data.database.*
import it.unibo.kickify.data.repositories.local.*
import it.unibo.kickify.utils.ImageStorageManager
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class AppRepository(
    private val context: Context,
    private val remoteRepository: RemoteRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val wishlistRepository: WishlistRepository,
    private val reviewRepository: ReviewRepository,
    private val notificationRepository: NotificationRepository,
    private val imageRepository: ImageRepository,
    private val productCartRepository: ProductCartRepository
) {
    private val tag = "AppRepository"

    // get last access
    //val lastAccess: Flow<String> = dataStore.data.map { preferences ->
    //    preferences[stringPreferencesKey("last_access")] ?: "0"
    //}

    // set last access
    //suspend fun setLastAccess(timestamp: String) = dataStore.edit { preferences ->
    //    preferences[stringPreferencesKey("last_access")] = timestamp
    //}

    // PRODOTTI
    suspend fun getProducts(lastAccess: String): Result<Map<Product, Image>> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.getProducts(lastAccess)

            if (remoteResult.isSuccess) {
                val remoteProducts = remoteResult.getOrNull() ?: emptyList()

                if (remoteProducts.isNotEmpty()) {
                    val images = remoteRepository.getProductsImages(remoteProducts.map { it.productId })
                        .getOrNull() ?: emptyList()
                    if (images.isNotEmpty()) {
                        val remoteImages = remoteRepository.downloadImagesFromUrls(images.map { it.url })
                            .getOrNull() ?: emptyList()
                        if (remoteImages.isNotEmpty()) {
                            val savedImages = ImageStorageManager(context).saveAll(remoteImages)
                            images.forEachIndexed { index, image ->
                                image.url = savedImages[index].second
                            }
                            imageRepository.insertImages(images)
                        }
                    }
                    productRepository.insertProducts(remoteProducts)
                }
            }

            Result.success(
                productRepository.getProductsWithImage()
            )
        } catch (e: Exception) {
            Log.e(tag, "Errore in getProducts", e)
            Result.failure(e)
        }
    }

    suspend fun getProductData(productId: Int, userEmail: String): Result<ProductDetails> = withContext(Dispatchers.IO) {
            try {
                val remoteResult = remoteRepository.getProductData(productId, userEmail)
                if (remoteResult.isSuccess) {
                    return@withContext remoteResult
                }
                Result.failure(Exception("Prodotto non trovato"))
            } catch (e: Exception) {
                Log.e(tag, "Errore in getProductData", e)
                Result.failure(e)
            }
        }

    suspend fun getProductHistory(productId: Int, lastAccess: String): Result<List<HistoryProduct>> =
        withContext(Dispatchers.IO) {
            try {
                val remoteResult = remoteRepository.getProductHistory(productId, lastAccess)
                if (remoteResult.isSuccess) {
                    val remoteHistory = remoteResult.getOrNull() ?: emptyList()

                    if (remoteHistory.isNotEmpty()) {
                        productRepository.insertProductHistory(remoteHistory)
                    }
                }
                Result.success(
                    productRepository.getProductHistory(productId)
                )
            } catch (e: Exception) {
                Log.e(tag, "Errore in getProductHistory", e)
                Result.failure(e)
            }
        }

    // CARRELLO
    suspend fun getCart(email: String): Result<Cart> {
        val remoteResult = remoteRepository.getCart(email)
        if (remoteResult.isSuccess) {
            val remoteCart = remoteResult.getOrNull()
            remoteCart?.let {
                cartRepository.insertCart(it)
            }
        }
        return remoteResult
    }

    suspend fun getCartItems(email: String): Result<List<CartWithProductInfo>> =
        withContext(Dispatchers.IO) {
            try {
                val cartId = cartRepository.getCartByEmail(email)?.cartId
                val remoteResult = remoteRepository.getCartItems(email)
                if (remoteResult.isSuccess) {
                    val remoteCartItems = remoteResult.getOrNull() ?: emptyList()
                    if (remoteCartItems.isNotEmpty()) {
                        remoteCartItems.forEach { item ->
                            productCartRepository.addToCart(
                                cartId, item.productId, item.color, item.size, item.quantity
                            )
                        }
                    }
                }
                val cart = cartRepository.getCartByEmail(email)
                Result.success(
                    cart?.let {
                        productCartRepository.getCartItems(it.cartId)
                    } ?: emptyList()
                )
            } catch (e: Exception) {
                Log.e(tag, "Errore in getCartItems", e)
                Result.failure(e)
            }
        }

    suspend fun addToCart(email: String, productId: Int, color: String, size: Double, quantity: Int = 1): Result<Boolean> {
        val result = remoteRepository.addToCart(email, productId, color, size, quantity)
        if (result.isSuccess && result.getOrNull() == true) {
            val cartId = cartRepository.getCartByEmail(email)?.cartId
            if (cartId != null) {
                productCartRepository.addToCart(cartId, productId, color, size, quantity)
                cartRepository.updateCartTotal(cartId)
            }
        }
        return result
    }

    suspend fun removeFromCart(email: String, productId: Int, color: String, size: Double): Result<Boolean> {
        val cart = cartRepository.getCartByEmail(email)
        val result = remoteRepository.removeFromCart(email, productId, color, size)
        if (result.isSuccess && result.getOrNull() == true && cart != null) {
            productCartRepository.removeFromCart(cart.cartId, productId, color, size)
            cartRepository.updateCartTotal(cart.cartId)
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