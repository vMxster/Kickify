package it.unibo.kickify.data.repositories

import android.content.Context
import android.util.Log
import it.unibo.kickify.data.database.Address
import it.unibo.kickify.data.database.Cart
import it.unibo.kickify.data.database.CartWithProductInfo
import it.unibo.kickify.data.database.CompleteProduct
import it.unibo.kickify.data.database.HistoryProduct
import it.unibo.kickify.data.database.Image
import it.unibo.kickify.data.database.Notification
import it.unibo.kickify.data.database.NotificationState
import it.unibo.kickify.data.database.Order
import it.unibo.kickify.data.database.OrderDetailedTracking
import it.unibo.kickify.data.database.OrderProduct
import it.unibo.kickify.data.database.OrderProductDetails
import it.unibo.kickify.data.database.Product
import it.unibo.kickify.data.database.ProductDetails
import it.unibo.kickify.data.database.ProductState
import it.unibo.kickify.data.database.Review
import it.unibo.kickify.data.database.ReviewWithUserInfo
import it.unibo.kickify.data.database.User
import it.unibo.kickify.data.repositories.local.CartRepository
import it.unibo.kickify.data.repositories.local.ImageRepository
import it.unibo.kickify.data.repositories.local.NotificationRepository
import it.unibo.kickify.data.repositories.local.OrderRepository
import it.unibo.kickify.data.repositories.local.ProductCartRepository
import it.unibo.kickify.data.repositories.local.ProductRepository
import it.unibo.kickify.data.repositories.local.ReviewRepository
import it.unibo.kickify.data.repositories.local.UserRepository
import it.unibo.kickify.data.repositories.local.VersionRepository
import it.unibo.kickify.data.repositories.local.WishlistRepository
import it.unibo.kickify.utils.ImageStorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

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
    private val productCartRepository: ProductCartRepository,
    private val versionRepository: VersionRepository,
    private val settingsRepository: SettingsRepository
) {
    private val tag = "AppRepository"

   val lastAccess = settingsRepository.lastAccess

    suspend fun setLastAccessNow() = withContext(Dispatchers.IO) {
        settingsRepository.setLastAccess()
    }

    // PRODOTTI
    suspend fun getProducts(): Result<Map<Product, Image>> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.getProducts(lastAccess.first())

            if (remoteResult.isSuccess) {
                val remoteProducts = remoteResult.getOrNull() ?: emptyList()

                if (remoteProducts.isNotEmpty()) {
                    remoteProducts.forEach { product ->
                        try {
                            productRepository.insertProduct(product)
                        } catch (e: Exception) {
                            Log.e(tag, "Errore inserimento prodotto ${product.productId}: ${e.message}")
                        }
                    }
                    val imagesResult = remoteRepository.getProductsImages(remoteProducts.map { it.productId })
                    val images = imagesResult.getOrNull() ?: emptyList()
                    if (images.isNotEmpty()) {
                        val remoteImagesResult = remoteRepository.downloadImagesFromUrls(images.map { it.url })
                        val remoteImages = remoteImagesResult.getOrNull() ?: emptyList()
                        if (remoteImages.isNotEmpty()) {
                            val savedImages = ImageStorageManager(context).saveAll(remoteImages)
                            images.forEachIndexed { index, image ->
                                image.url = savedImages[index].second
                                try {
                                    imageRepository.insertImage(image)
                                } catch (e: Exception) {
                                    Log.e(tag, "Errore inserimento immagine per prodotto ${image.productId}: ${e.message}")
                                }
                            }
                        }
                    }
                }
            }
            val productsWithImages = productRepository.getProductsWithImage()
            Result.success(productsWithImages)
        } catch (e: Exception) {
            Log.e(tag, "Errore in getProducts", e)
            Result.failure(e)
        }
    }

    suspend fun getProductData(productId: Int, userEmail: String): Result<ProductDetails> = withContext(Dispatchers.IO) {
            try {
                val remoteResult = remoteRepository.getProductData(productId, userEmail)
                if (remoteResult.isSuccess) {
                    val remoteProduct = remoteResult.getOrNull()
                    remoteProduct?.let {
                        for (variant in it.variants) {
                            versionRepository.insertProductVariant(variant)
                        }
                        for (review in it.reviews) {
                            reviewRepository.addReview(review)
                        }
                    }
                    return@withContext remoteResult
                }
                Result.failure(Exception("Prodotto non trovato"))
            } catch (e: Exception) {
                Log.e(tag, "Errore in getProductData", e)
                Result.failure(e)
            }
        }

    suspend fun getProductHistory(productId: Int): Result<List<HistoryProduct>> =
        withContext(Dispatchers.IO) {
            try {
                val remoteResult = remoteRepository.getProductHistory(productId, lastAccess.first())
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

    suspend fun getProductWithVariants(productId: Int): Result<CompleteProduct> = withContext(Dispatchers.IO) {
        try {
            val product = productRepository.getProductWithVariants(productId)
            if (product != null) {
                Result.success(product)
            } else {
                Result.failure(Exception("Prodotto non trovato"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Errore in getProductWithVariants", e)
            Result.failure(e)
        }
    }

    suspend fun getPopularProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val products = productRepository.getPopularProducts()
            if (products.isNotEmpty()) {
                Result.success(products)
            } else {
                Result.failure(Exception("Nessun prodotto popolare trovato"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Errore in getPopularProducts", e)
            Result.failure(e)
        }
    }

    suspend fun getNewProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val products = productRepository.getNewProducts()
            if (products.isNotEmpty()) {
                Result.success(products)
            } else {
                Result.failure(Exception("Nessun prodotto nuovo trovato"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Errore in getNewProducts", e)
            Result.failure(e)
        }
    }

    suspend fun getDiscountedProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val products = productRepository.getDiscountedProducts()
            if (products.isNotEmpty()) {
                Result.success(products)
            } else {
                Result.failure(Exception("Nessun prodotto scontato trovato"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Errore in getDiscountedProducts", e)
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
    suspend fun getWishlistItems(email: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.getWishlistItems(email)
            if (remoteResult.isSuccess) {
                val remoteWishlist = remoteResult.getOrNull() ?: emptyList()
                if (remoteWishlist.isNotEmpty()) {
                    for (item in remoteWishlist) {
                        wishlistRepository.addToWishlist(email, item.productId)
                    }
                }
                Result.success(
                    wishlistRepository.getWishlistItems(email)
                )
            } else {
                Result.failure(Exception("Nessun Articolo nella wishlist"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Errore in getWishlistItems", e)
            Result.failure(e)
        }
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
    suspend fun getNotifications(email: String): Result<List<Notification>> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.getNotifications(email, lastAccess.first())
            if (remoteResult.isSuccess) {
                val remoteNotifications = remoteResult.getOrNull() ?: emptyList()
                if (remoteNotifications.isNotEmpty()) {
                    for (notification in remoteNotifications) {
                        notificationRepository.addNotification(notification)
                    }
                }
            }
            Result.success(
                notificationRepository.getUserNotifications(email)
            )
        } catch (e: Exception) {
            Log.e(tag, "Errore in getNotifications", e)
            Result.failure(e)
        }
    }

    suspend fun createNotification(email: String, message: String, type: String): Result<Boolean> {
        val result = remoteRepository.createNotification(email, message, type)
        if (result.isSuccess && result.getOrNull() == true) {
            notificationRepository.createNotification(Notification(
                email = email,
                message = message,
                type = type,
                date = LocalDateTime.now().toString(),
                state = "Unread",
                notificationId = 0
            ))
        }
        return result
    }

    suspend fun markNotificationsAsRead(email: String, notificationIds: List<Int>): Result<Boolean> {
        val result = remoteRepository.markNotificationsAsRead(email, notificationIds.toTypedArray())
        if (result.isSuccess && result.getOrNull() == true) {
            notificationRepository.markNotificationsAsRead(email, notificationIds)
        }
        return result
    }

    suspend fun getUnreadNotificationsCount(email: String): Int {
        return notificationRepository.getUnreadNotificationsCount(email)
    }

    // ORDINI
    suspend fun getOrders(email: String): Result<List<Order>> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.getOrders(email, lastAccess.first())
            if (remoteResult.isSuccess) {
                val remoteOrders = remoteResult.getOrNull() ?: emptyList()
                if (remoteOrders.isNotEmpty()) {
                    for (order in remoteOrders) {
                        orderRepository.insertOrder(order)
                        addOrderDetails(order.orderId).onFailure { e ->
                            Log.e(tag, "Errore in getOrderDetails per l'ordine ${order.orderId}", e)
                        }
                    }
                }
            }
            Result.success(
                orderRepository.getOrders(email)
            )
        } catch (e: Exception) {
            Log.e(tag, "Errore in getOrders", e)
            Result.failure(e)
        }
    }

    private suspend fun addOrderDetails(orderId: Int): Result<Any> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.getOrderDetails(orderId)
            if (remoteResult.isSuccess) {
                val remoteOrder = remoteResult.getOrNull()
                remoteOrder?.let {
                    it.forEach { orderDetail ->
                        orderDetail.products.forEach { product ->
                            orderRepository.insertOrderProduct(
                                OrderProduct(
                                    orderId = product.orderId,
                                    productId = product.productId,
                                    color = product.color,
                                    size = product.size,
                                    quantity = product.quantity,
                                    purchasePrice = product.purchasePrice
                                )
                            )
                        }
                    }
                }
            }
            Result.success(Any())
        } catch (e: Exception) {
            Log.e(tag, "Errore in getOrderDetails", e)
            Result.failure(e)
        }
    }

    suspend fun getOrdersWithProducts(email: String): Result<List<OrderProductDetails>> = withContext(Dispatchers.IO) {
        try {
            Result.success(orderRepository.getOrdersWithProducts(email))
        } catch (e: Exception) {
            Log.e(tag, "Errore in getOrderDetails", e)
            Result.failure(e)
        }
    }

    suspend fun getOrderTracking(orderId: Int): Result<OrderDetailedTracking> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.getOrderTracking(orderId)
            if (remoteResult.isSuccess) {
                val remoteTracking = remoteResult.getOrNull()
                remoteTracking?.forEach() {
                    orderRepository.insertTrackingInfo(it)
                }
            }
            Result.success(orderRepository.getOrderTracking(orderId))
        } catch (e: Exception) {
            Log.e(tag, "Errore in getOrderTracking", e)
            Result.failure(e)
        }
    }

    suspend fun placeOrder(
        email: String,
        total: Double,
        paymentMethod: String,
        shippingType: String,
        isGift: Boolean = false,
        giftFirstName: String? = null,
        giftLastName: String? = null,
        street: String,
        city: String,
        civic: Int,
        cap: Int
    ) = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.placeOrder(email, total, paymentMethod,
                shippingType, isGift, giftFirstName, giftLastName,
                street, city, civic, cap)
            if (remoteResult.isSuccess) {
                cartRepository.getCartByEmail(email)?.cartId?.let {
                    productCartRepository.clearCart(it)
                }
                cartRepository.getCartByEmail(email)?.cartId?.let {
                    cartRepository.clearCart(it)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Errore in placeOrder", e)
            throw e
        }
    }

    // RECENSIONI
    suspend fun addReview(
        email: String,
        productId: Int,
        rating: Double,
        comment: String
    ): Result<Boolean> {
        val result = remoteRepository.addReview(email, productId, rating, comment)
        if (result.isSuccess && result.getOrNull() == true) {
            val review = Review(
                productId = productId,
                email = email,
                vote = rating,
                comment = comment,
                reviewDate = LocalDateTime.now().toString()
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

    suspend fun getReviews(productId: Int): Result<List<ReviewWithUserInfo>> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.getReviews(productId, lastAccess.first())
            if (remoteResult.isSuccess) {
                val remoteReviews = remoteResult.getOrNull() ?: emptyList()
                if (remoteReviews.isNotEmpty()) {
                    for (review in remoteReviews) {
                        reviewRepository.addReview(review)
                    }
                }
            }
            Result.success(
                reviewRepository.getProductReviews(productId, lastAccess.first())
            )
        } catch (e: Exception) {
            Log.e(tag, "Errore in getReviews", e)
            Result.failure(e)
        }
    }

    suspend fun getProductRating(productId: Int): Result<Double> = withContext(Dispatchers.IO) {
        Result.success(reviewRepository.getProductRating(productId))
    }

    suspend fun canUserReview(email: String, productId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val canReview = reviewRepository.canUserReview(email, productId)
            Result.success(canReview)
        } catch (e: Exception) {
            Log.e(tag, "Errore in canUserReview", e)
            Result.failure(e)
        }
    }

    // AUTENTICAZIONE
    suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.login(email.lowercase(), password)
            if (remoteResult.isSuccess) {
                val userProfile = remoteResult.getOrNull()
                userProfile?.let {
                    userRepository.registerUser(it)
                }
                Result.success(
                    userRepository.getUserProfile(email.lowercase()) ?: throw Exception("Profilo utente non trovato")
                )
            } else {
                Result.failure(Exception("Login fallito: credenziali non valide"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Errore in login", e)
            Result.failure(e)
        }
    }

    suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        remoteRepository.register(email.lowercase(), firstName, lastName, password)
            .onSuccess {
                remoteRepository.getUserProfile(email.lowercase())
                    .onSuccess { userProfile ->
                        userRepository.registerUser(userProfile)
                    }
                    .onFailure { e ->
                        Log.e(tag, "Errore nel recupero del profilo utente dopo la registrazione", e)
                    }
            }
    }

    suspend fun changePassword(email: String, password: String): Result<Boolean> {
        val result = remoteRepository.changePassword(email.lowercase(), password)
        if (result.isSuccess && result.getOrNull() == true) {
            userRepository.changePassword(email.lowercase(), password)
        }
        return result
    }

    suspend fun getUserProfile(email: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.getUserProfile(email.lowercase())
            if (remoteResult.isSuccess) {
                val userProfile = remoteResult.getOrNull()
                userProfile?.let {
                    userRepository.registerUser(it)
                }
            }
            Result.success(
                userRepository.getUserProfile(email.lowercase()) ?: throw Exception("Profilo utente non trovato")
            )
        } catch (e: Exception) {
            Log.e(tag, "Errore in getUserProfile", e)
            Result.failure(e)
        }
    }

    suspend fun getUserAddress(email: String): Result<List<Address>> {
        return remoteRepository.getUserAddress(email)
    }

    suspend fun updateUserAddress(
        email: String, street: String, number: String, cap: String,
        city: String, province: String, nation: String, default: Boolean
    ): Result<Boolean> {
        return remoteRepository.updateUserAddress(email, street, number, cap, city, province, nation, default)
    }

    suspend fun deleteUserAddress(
        email: String, street: String, number: String,
        cap: String, city: String
    ): Result<Boolean>{
        return remoteRepository.deleteUserAddress(email, street, number, cap, city)
    }

    suspend fun updateUserImage(email: String, imgFile: ByteArray, mimeType: String): Result<String> {
        return remoteRepository.updateUserImage(email.lowercase(), imgFile, mimeType)
    }

    suspend fun isUserRegistered(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        remoteRepository.isUserRegistered(email.lowercase())
    }

    suspend fun sendMailWithOTP(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        remoteRepository.sendMailWithOTP(email.lowercase())
    }

    suspend fun verifyOTP(email: String, otp: String): Result<Boolean> = withContext(Dispatchers.IO) {
        remoteRepository.verifyOTP(email.lowercase(), otp)
    }
}