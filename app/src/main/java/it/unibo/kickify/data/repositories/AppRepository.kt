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
import it.unibo.kickify.data.database.Order
import it.unibo.kickify.data.database.OrderDetailedTracking
import it.unibo.kickify.data.database.OrderProduct
import it.unibo.kickify.data.database.OrderProductDetails
import it.unibo.kickify.data.database.Product
import it.unibo.kickify.data.database.ProductDetails
import it.unibo.kickify.data.database.ProductWithImage
import it.unibo.kickify.data.database.ReviewWithUserInfo
import it.unibo.kickify.data.database.User
import it.unibo.kickify.data.database.UserOAuth
import it.unibo.kickify.data.database.Version
import it.unibo.kickify.data.database.WishlistProduct
import it.unibo.kickify.data.models.PaymentMethodInfo
import it.unibo.kickify.data.repositories.local.CartRepository
import it.unibo.kickify.data.repositories.local.ImageRepository
import it.unibo.kickify.data.repositories.local.OAuthUserRepository
import it.unibo.kickify.data.repositories.local.OrderRepository
import it.unibo.kickify.data.repositories.local.ProductCartRepository
import it.unibo.kickify.data.repositories.local.ProductRepository
import it.unibo.kickify.data.repositories.local.ReviewRepository
import it.unibo.kickify.data.repositories.local.UserRepository
import it.unibo.kickify.data.repositories.local.VersionRepository
import it.unibo.kickify.utils.ImageStorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate

class AppRepository(
    private val context: Context,
    private val remoteRepository: RemoteRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val reviewRepository: ReviewRepository,
    private val imageRepository: ImageRepository,
    private val productCartRepository: ProductCartRepository,
    private val versionRepository: VersionRepository,
    private val settingsRepository: SettingsRepository,
    private val oAuthUserRepository: OAuthUserRepository
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
                                val url = savedImages[index].second
                                try {
                                    imageRepository.insertImage(
                                        Image(
                                            productId = image.productId,
                                            number = image.number,
                                            url = url,
                                        )
                                    )
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

    suspend fun getProductImages(productId: Int): List<Image> = withContext(Dispatchers.IO) {
        return@withContext productRepository.getProductImages(productId)
    }

    suspend fun getVersions(): Result<Map<Int, List<Version>>> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.getVersions()
            if (remoteResult.isSuccess) {
                val versions = remoteResult.getOrNull() ?: emptyList()
                if (versions.isNotEmpty()) {
                    versions.forEach { version ->
                        try {
                            versionRepository.insertProductVariant(version)
                        } catch (e: Exception) {
                            Log.e(tag, "Errore inserimento versione ${version.productId}: ${e.message}")
                        }
                    }
                    val groupedVersions = versions.groupBy { it.productId }
                    return@withContext Result.success(groupedVersions)
                }
                return@withContext Result.success(emptyMap())
            }
            Result.failure(Exception("Nessuna versione trovata"))
        } catch (e: Exception) {
            Log.e(tag, "Errore in getVersions", e)
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
                }
                return@withContext remoteResult
            }
            Result.failure(Exception("Prodotto non trovato"))
        } catch (e: Exception) {
            Log.e(tag, "Errore in getProductData", e)
            Result.failure(e)
        }
    }

    suspend fun getProductsHistory(): Result<List<HistoryProduct>> =
        withContext(Dispatchers.IO) {
            try {
                val remoteResult = remoteRepository.getProductsHistory(lastAccess.first())
                if (remoteResult.isSuccess) {
                    val remoteHistory = remoteResult.getOrNull() ?: emptyList()
                    if (remoteHistory.isNotEmpty()) {
                        productRepository.insertProductsHistory(remoteHistory)
                    }
                }
                Result.success(
                    productRepository.getProductsHistory()
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

    suspend fun searchProducts(query: String): Result<List<ProductWithImage>> = withContext(Dispatchers.IO) {
        try {
            val products = productRepository.searchProducts(query)
            if (products.isNotEmpty()) {
                Result.success(products)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Log.e(tag, "Errore nella ricerca prodotti", e)
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
        return try {
            val cart = cartRepository.getCartByEmail(email)
            val result = remoteRepository.removeFromCart(email, productId, color, size)
            if (result.isSuccess && result.getOrNull() == true && cart != null) {
                productCartRepository.removeFromCart(cart.cartId, productId, color, size)
                cartRepository.updateCartTotal(cart.cartId)
            }
            result
        } catch (e: Exception) {
            Log.e(tag, "Errore in removeFromCart", e)
            Result.failure(e)
        }
    }

    // WISHLIST
    suspend fun getWishlistItems(email: String): Result<List<WishlistProduct>> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.getWishlistItems(email)
            if (remoteResult.isSuccess) {
                val remoteWishlist = remoteResult.getOrNull() ?: emptyList()
                return@withContext Result.success(remoteWishlist)
            } else {
                Result.failure(Exception("Nessun Articolo nella wishlist"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Errore in getWishlistItems", e)
            Result.failure(e)
        }
    }

    suspend fun addToWishlist(productId: Int): Result<Boolean> {
        return remoteRepository.addToWishlist(
            settingsRepository.userID.first(),
            productId
        )
    }

    suspend fun removeFromWishlist(productId: Int): Result<Boolean> {
        return remoteRepository.removeFromWishlist(
            settingsRepository.userID.first(),
            productId
        )
    }

    suspend fun isInWishlist(productId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            remoteRepository.isInWishlist(
                settingsRepository.userID.first(),
                productId)
        } catch (e: Exception) {
            Log.e(tag, "Errore in isInWishlist", e)
            Result.failure(e)
        }
    }

    // NOTIFICHE
    suspend fun getNotifications(email: String): Result<List<Notification>> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.getNotifications(email, lastAccess.first())
            if (remoteResult.isSuccess) {
                val remoteNotifications = remoteResult.getOrNull() ?: emptyList()
                return@withContext Result.success(remoteNotifications)
            }
            return@withContext Result.success(emptyList())
        } catch (e: Exception) {
            Log.e(tag, "Errore in getNotifications", e)
            Result.failure(e)
        }
    }

    suspend fun markNotificationsAsRead(email: String, notificationIds: List<Int>): Result<Boolean> {
        return remoteRepository.markNotificationsAsRead(email, notificationIds.toTypedArray())
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
    suspend fun addReview(email: String, productId: Int, rating: Double, comment: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            remoteRepository.addReview(email, productId, rating, comment)
        }

    suspend fun deleteReview(email: String, productId: Int): Result<Boolean> =
        withContext(Dispatchers.IO) {
            remoteRepository.deleteReview(email, productId)
        }


    suspend fun getReviews(productId: Int): Result<List<ReviewWithUserInfo>> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteRepository.getReviews(productId)
            if (remoteResult.isSuccess) {
                val remoteReviews = remoteResult.getOrNull() ?: emptyList()
                return@withContext Result.success(remoteReviews)
            } else {
                Result.failure(Exception("Nessuna recensione trovata"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Errore in getReviews", e)
            Result.failure(e)
        }
    }

    suspend fun getProductRating(productId: Int): Result<Double> = withContext(Dispatchers.IO) {
        Result.success(remoteRepository.getProductRating(productId))
    }

    suspend fun canUserReview(email: String, productId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            Result.success(
                reviewRepository.canUserReview(email, productId)
            )
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

    suspend fun loginWithGoogle(
        email: String, name: String,
        surname: String, idToken: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val remoteResult =
                remoteRepository.loginWithGoogle(email, name, surname, idToken)
            if (remoteResult.isSuccess) {
                val user = remoteResult.getOrNull()
                user?.let {
                    val existingUser = userRepository.getUserProfile(email.lowercase())
                    if (existingUser != null) {
                        val updatedUser = it.copy(password = existingUser.password)
                        userRepository.registerUser(updatedUser)
                    } else {
                        userRepository.registerUser(it)
                    }
                    oAuthUserRepository.insertUserOAuth(
                        UserOAuth(
                            email = it.email,
                            provider = "Google",
                            providerUserId = idToken,
                            dataLink = LocalDate.now().toString(),
                        )
                    )
                }
                Result.success(
                    userRepository.getUserProfile(email.lowercase()) ?: throw Exception("Profilo utente non trovato")
                )
            } else {
                Result.failure(Exception("Login fallito: credenziali non valide"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Errore in loginWithGoogle", e)
            Result.failure(e)
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
                    val existingUser = userRepository.getUserProfile(email.lowercase())
                    if (existingUser != null) {
                        val updatedUser = it.copy(password = existingUser.password)
                        userRepository.registerUser(updatedUser)
                    } else {
                        userRepository.registerUser(it)
                    }
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

    suspend fun addUserAddress(
        email: String, street: String, number: String, cap: String,
        city: String, province: String, nation: String, default: Boolean
    ): Result<Boolean> {
        return remoteRepository.addUserAddress(email, street, number, cap, city, province, nation, default)
    }

    suspend fun deleteUserAddress(
        email: String, street: String, number: String,
        cap: String, city: String
    ): Result<Boolean>{
        return remoteRepository.deleteUserAddress(email, street, number, cap, city)
    }

    suspend fun getUserPaymentMethod(email: String): Result<List<PaymentMethodInfo>> {
        return remoteRepository.getUserPaymentMethod(email)
    }

    suspend fun addUserPaymentMethod(
        userEmail: String, paypalEmail: String, creditCardBrand: String,
        creditCardLast4: String, creditCardExpMonth: Int, creditCardExpYear: Int
    ): Result<Boolean>{
        return remoteRepository.addUserPaymentMethod(
            userEmail, paypalEmail, creditCardBrand, creditCardLast4, creditCardExpMonth, creditCardExpYear
        )
    }

    suspend fun deleteUserPaymentMethod(id: Int): Result<Boolean>{
        return remoteRepository.deleteUserPaymentMethod(id)
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