package it.unibo.kickify.data.repositories

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import it.unibo.kickify.data.database.Address
import it.unibo.kickify.data.database.Cart
import it.unibo.kickify.data.database.CartProduct
import it.unibo.kickify.data.database.HistoryProduct
import it.unibo.kickify.data.database.Image
import it.unibo.kickify.data.database.Notification
import it.unibo.kickify.data.database.Order
import it.unibo.kickify.data.database.OrderDetails
import it.unibo.kickify.data.database.Product
import it.unibo.kickify.data.database.ProductDetails
import it.unibo.kickify.data.database.ReviewWithUserInfo
import it.unibo.kickify.data.database.TrackingShipping
import it.unibo.kickify.data.database.User
import it.unibo.kickify.data.database.Version
import it.unibo.kickify.data.database.WishlistProduct
import it.unibo.kickify.data.models.PaymentMethodInfo
import it.unibo.kickify.utils.RemoteResponseParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class RemoteRepository(
    private val httpClient: HttpClient
) {
    private val tag = "RemoteRepository"
    private val baseUrl = "https://kickify.altervista.org"

    // PRODOTTI
    suspend fun getProducts(lastAccess: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getProducts",
                "last_access" to lastAccess
            )
            val response = makeRequest("product_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val jsonArray = jsonObject.getJSONArray("products")
            val products = RemoteResponseParser.parseProducts(jsonArray)
            Result.success(products)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero dei prodotti", e)
            Result.failure(e)
        }
    }

    suspend fun getProductsImages(productIds: List<Int>): Result<List<Image>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getProductsImages",
                "productIds" to productIds.joinToString(",")
            )
            val response = makeRequest("product_handler.php", params)
            val jsonResponse = JSONObject(response)
            val images = RemoteResponseParser.parseProductsWithImages(jsonResponse)
            Result.success(images)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero delle immagini dei prodotti", e)
            Result.failure(e)
        }
    }

    suspend fun getProductData(productId: Int, userEmail: String): Result<ProductDetails> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getProductData",
                "productId" to productId.toString(),
                "email" to userEmail
            )
            val response = makeRequest("product_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val productData = jsonObject.getJSONObject("productData")
            val product = RemoteResponseParser.parseProductDetails(productData)
            Result.success(product)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero dei dati del prodotto $productId", e)
            Result.failure(e)
        }
    }

    suspend fun getProductsHistory(lastAccess: String): Result<List<HistoryProduct>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getProductsHistory",
                "last_access" to lastAccess
            )
            val response = makeRequest("product_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            Result.success(
                RemoteResponseParser.parseProductHistory(jsonObject)
            )
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero della cronologia dei prodotti", e)
            Result.failure(e)
        }
    }

    suspend fun getProductRating(productId: Int): Double {
        return try {
            val params = mapOf(
                "action" to "getProductRating",
                "productId" to productId.toString()
            )
            val response = makeRequest("review_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                throw Exception(RemoteResponseParser.parseError(jsonObject))
            }
            jsonObject.optDouble("rating", 0.0)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero della valutazione del prodotto $productId", e)
            0.0
        }
    }

    suspend fun getPopularProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf("action" to "getPopularProducts")
            val response = makeRequest("product_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                throw Exception(RemoteResponseParser.parseError(jsonObject))
            }
            val jsonArray = jsonObject.getJSONArray("products")
            RemoteResponseParser.parseProducts(jsonArray)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero dei prodotti popolari", e)
            emptyList()
        }
    }

    suspend fun getDiscountedProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf("action" to "getDiscountedProducts")
            val response = makeRequest("product_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                throw Exception(RemoteResponseParser.parseError(jsonObject))
            }
            val jsonArray = jsonObject.getJSONArray("products")
            RemoteResponseParser.parseProducts(jsonArray)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero dei prodotti scontati", e)
            emptyList()
        }
    }

    suspend fun getVersions(): Result<List<Version>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf("action" to "getVersions")
            val response = makeRequest("product_handler.php", params)
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONArray("versions")
            val versions = RemoteResponseParser.parseVersions(jsonArray)
            Result.success(versions)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero delle versioni dei prodotti", e)
            Result.failure(e)
        }
    }

    // CARRELLO
    suspend fun getCart(email: String): Result<Cart> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getCart",
                "user_email" to email
            )
            val response = makeRequest("cart_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val cart = RemoteResponseParser.parseCart(jsonObject)
            Result.success(cart)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero del carrello", e)
            Result.failure(e)
        }
    }

    suspend fun getCartItems(email: String): Result<List<CartProduct>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getCartItems",
                "user_email" to email
            )
            val response = makeRequest("cart_handler.php", params)
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONArray("items")
            val items = RemoteResponseParser.parseCartItems(jsonArray)
            Result.success(items)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero degli elementi del carrello", e)
            Result.failure(e)
        }
    }

    suspend fun addToCart(email: String, productId: Int, color: String, size: Double, quantity: Int): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val params = mapOf(
                    "action" to "add_to_cart",
                    "email" to email,
                    "productId" to productId.toString(),
                    "color" to color,
                    "size" to size.toString(),
                    "quantity" to quantity.toString()
                )
                val response = makeRequest("product_handler.php", params)
                val jsonObject = JSONObject(response)
                if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                    return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
                }
                val success = RemoteResponseParser.parseSuccess(jsonObject)
                Result.success(success)
            } catch (e: Exception) {
                Log.e(tag, "Errore durante l'aggiunta al carrello", e)
                Result.failure(e)
            }
        }

    suspend fun removeFromCart(email: String, productId: Int, color: String, size: Double): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val params = mapOf(
                    "action" to "remove_from_cart",
                    "email" to email,
                    "productId" to productId.toString(),
                    "color" to color,
                    "size" to size.toString()
                )
                val response = makeRequest("product_handler.php", params)
                val jsonObject = JSONObject(response)
                if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                    return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
                }
                val success = RemoteResponseParser.parseSuccess(jsonObject)
                Result.success(success)
            } catch (e: Exception) {
                Log.e(tag, "Errore durante la rimozione dal carrello", e)
                Result.failure(e)
            }
        }

    // WISHLIST
    suspend fun getWishlistItems(email: String): Result<List<WishlistProduct>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getWishlistItems",
                "user_email" to email
            )
            val response = makeRequest("wishlist_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val jsonArray = jsonObject.getJSONArray("items")
            val items = RemoteResponseParser.parseWishlistItems(jsonArray)
            Result.success(items)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero degli elementi della wishlist", e)
            Result.failure(e)
        }
    }

    suspend fun addToWishlist(email: String, productId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "addToWishlist",
                "user_email" to email,
                "productId" to productId.toString()
            )
            val response = makeRequest("wishlist_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante l'aggiunta alla wishlist", e)
            Result.failure(e)
        }
    }

    suspend fun removeFromWishlist(email: String, productId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "removeFromWishlist",
                "user_email" to email,
                "productId" to productId.toString()
            )
            val response = makeRequest("wishlist_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante la rimozione dalla wishlist", e)
            Result.failure(e)
        }
    }

    suspend fun isInWishlist(email: String, productId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "isInWishlist",
                "user_email" to email,
                "productId" to productId.toString()
            )
            val response = makeRequest("wishlist_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val isInWishlist = jsonObject.optBoolean("isInWishlist", false)
            Result.success(isInWishlist)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il controllo della wishlist", e)
            Result.failure(e)
        }
    }

    // NOTIFICHE
    suspend fun getNotifications(email: String): Result<List<Notification>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getUserNotifications",
                "email" to email
            )
            val response = makeRequest("notifications_handler.php", params)
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONArray("notifications")
            val notifications = RemoteResponseParser.parseNotifications(jsonArray)
            Result.success(notifications)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero delle notifiche", e)
            Result.failure(e)
        }
    }

    suspend fun markNotificationsAsRead(email: String, notificationIds: Array<Int>): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "markNotificationsAsRead",
                "email" to email,
                "notificationIds" to notificationIds.joinToString(",", prefix = "[", postfix = "]")
            )
            val response = makeRequest("notifications_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante la marcatura delle notifiche come lette", e)
            Result.failure(e)
        }
    }

    // ORDINI
    suspend fun getOrders(email: String, lastAccess: String): Result<List<Order>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getOrders",
                "email" to email,
                "last_access" to lastAccess
            )
            val response = makeRequest("order_handler.php", params)
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONArray("orders")
            val orders = RemoteResponseParser.parseOrders(jsonArray)
            Result.success(orders)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero degli ordini", e)
            Result.failure(e)
        }
    }

    suspend fun getOrderDetails(orderId: Int): Result<List<OrderDetails>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getOrderDetails",
                "orderId" to orderId.toString(),
            )
            val response = makeRequest("order_handler.php", params)
            val jsonObject = JSONObject(response)
            val orderDetails = RemoteResponseParser.parseOrderDetails(jsonObject)
            Result.success(orderDetails)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero dei dettagli degli ordini", e)
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
        cap: Int,
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val params = mutableMapOf(
                "action" to "placeOrder",
                "email" to email,
                "total" to total.toString(),
                "paymentMethod" to paymentMethod,
                "shippingType" to shippingType,
                "isGift" to (if (isGift) "1" else "0"),
                "giftFirstName" to (giftFirstName ?: ""),
                "giftLastName" to (giftLastName ?: ""),
                "street" to street,
                "city" to city,
                "civic" to civic.toString(),
                "cap" to cap.toString()
            )

            if (isGift) {
                giftFirstName?.let { params["giftFirstName"] = it }
                giftLastName?.let { params["giftLastName"] = it }
            }

            val response = makeRequest("order_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val orderId = jsonObject.optInt("orderId", -1)
            if (orderId > 0) {
                Result.success(orderId)
            } else {
                Result.failure(Exception("Errore durante la creazione dell'ordine"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il piazzamento dell'ordine", e)
            Result.failure(e)
        }
    }

    suspend fun getOrderTracking(orderId: Int): Result<List<TrackingShipping>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getOrderTracking",
                "orderId" to orderId.toString()
            )
            val response = makeRequest("tracking_handler.php", params)
            val jsonObject = JSONObject(response)
            val trackingObject = jsonObject.getJSONObject("tracking")
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val tracking = RemoteResponseParser.parseOrderTracking(trackingObject)
            Result.success(tracking)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero del tracking dell'ordine", e)
            Result.failure(e)
        }
    }

    // RECENSIONI
    suspend fun addReview(
        email: String,
        productId: Int,
        rating: Double,
        comment: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "addReview",
                "email" to email,
                "productId" to productId.toString(),
                "rating" to rating.toString(),
                "comment" to comment
            )
            val response = makeRequest("review_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante l'aggiunta della recensione", e)
            Result.failure(e)
        }
    }

    suspend fun deleteReview(
        email: String,
        productId: Int
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "deleteReview",
                "email" to email,
                "productId" to productId.toString()
            )
            val response = makeRequest("review_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante la cancellazione della recensione", e)
            Result.failure(e)
        }
    }

    suspend fun getReviews(productId: Int): Result<List<ReviewWithUserInfo>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getProductReviews",
                "productId" to productId.toString()
            )
            val response = makeRequest("product_handler.php", params)
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONArray("reviews")
            val reviews = RemoteResponseParser.parseReviews(jsonArray)
            Result.success(reviews)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero delle recensioni", e)
            Result.failure(e)
        }
    }

    // AUTENTICAZIONE
    suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "loginUser",
                "email" to email,
                "password" to password
            )
            val response = makeRequest("auth_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val userObject = jsonObject.getJSONObject("user")
            val user = RemoteResponseParser.parseUserProfile(userObject)
            Result.success(user)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il login", e)
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(
        email: String, name: String,
        surname: String, idToken: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "loginWithGoogle",
                "email" to email,
                "name" to name,
                "surname" to surname,
                "idToken" to idToken
            )
            val response = makeRequest("auth_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val userObject = jsonObject.getJSONObject("user")
            val user = RemoteResponseParser.parseUserProfile(userObject)
            Result.success(user)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il login con Google", e)
            Result.failure(e)
        }
    }

    suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mutableMapOf(
                "action" to "registerUser",
                "email" to email,
                "firstName" to firstName,
                "lastName" to lastName,
                "password" to password
            )

            val response = makeRequest("auth_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante la registrazione", e)
            Result.failure(e)
        }
    }

    suspend fun changePassword(
        email: String,
        password: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "changePassword",
                "email" to email,
                "password" to password
            )
            val response = makeRequest("auth_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il cambio della password", e)
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(email: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getUserProfile",
                "email" to email
            )
            val response = makeRequest("auth_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val userInfo = jsonObject.getJSONObject("profile")
            val user = RemoteResponseParser.parseUserProfile(userInfo)
            Result.success(user)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero del profilo utente", e)
            Result.failure(e)
        }
    }

    suspend fun getUserAddress(email: String): Result<List<Address>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getUserAddress",
                "email" to email
            )
            val response = makeRequest("auth_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val jsonArray = jsonObject.getJSONArray("address")
            val addressList = RemoteResponseParser.parseAddressList(jsonArray)
            addressList.sortedBy { !it.default } // before the default address
            Result.success(addressList)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero del profilo utente", e)
            Result.failure(e)
        }
    }

    suspend fun addUserAddress(
        email: String, street: String, number: String,
        cap: String, city: String, province: String,
        nation: String, default: Boolean
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "addUserAddress",
                "email" to email,
                "via" to street,
                "civico" to number,
                "cap" to cap,
                "citta" to city,
                "provincia" to province,
                "nazione" to nation,
                "predefinito" to (if (default) "1" else "0"),
            )
            val response = makeRequest("auth_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante aggiunta di indirizzo", e)
            Result.failure(e)
        }
    }

    suspend fun deleteUserAddress(
        email: String, street: String, number: String,
        cap: String, city: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "removeUserAddress",
                "email" to email,
                "via" to street,
                "civico" to number,
                "cap" to cap,
                "citta" to city
            )
            val response = makeRequest("auth_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante rimozione di indirizzo", e)
            Result.failure(e)
        }
    }

    suspend fun getUserPaymentMethod(email: String): Result<List<PaymentMethodInfo>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getUserPayMethods",
                "email" to email
            )
            val response = makeRequest("auth_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val jsonArray = jsonObject.getJSONArray("paymethods")
            val payMethods = RemoteResponseParser.parsePaymentMethodList(jsonArray)
            Result.success(payMethods)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero metodi pagamento di utente", e)
            Result.failure(e)
        }
    }

    suspend fun addUserPaymentMethod(
        userEmail: String, paypalEmail: String, creditCardBrand: String,
        creditCardLast4: String, creditCardExpMonth: Int, creditCardExpYear: Int
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "addUserPayMethod",
                "userEmail" to userEmail,
                "paypalEmail" to paypalEmail,
                "creditCardBrand" to creditCardBrand,
                "creditCardLast4" to creditCardLast4,
                "creditCardExpMonth" to creditCardExpMonth.toString(),
                "creditCardExpYear" to creditCardExpYear.toString()
            )
            val response = makeRequest("auth_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante aggiunta di metodo pagamento", e)
            Result.failure(e)
        }
    }

    suspend fun deleteUserPaymentMethod(id: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "removeUserPayMethod",
                "id" to id.toString()
            )
            val response = makeRequest("auth_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante rimozione di metodo pagamento", e)
            Result.failure(e)
        }
    }

    suspend fun updateUserImage(email: String, imgFile: ByteArray, mimeType: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/uploadUserImg.php"
            val response = httpClient.post(url) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("img", imgFile, Headers.build {
                                append(HttpHeaders.ContentType, mimeType)
                                append(HttpHeaders.ContentDisposition, "filename=\"img.png\"")
                            })
                            append("email", email)
                        }
                    )
                )
            }.bodyAsText()
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            // result = /userImg/user_<uniqueid>.png
            val result = jsonObject.optString("image_path", "")
            Result.success("$baseUrl$result")
        } catch (e: Exception) {
            Log.e(tag, "Errore durante upload immagine utente", e)
            Result.failure(e)
        }
    }

    suspend fun isUserRegistered(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "isUserRegistered",
                "email" to email
            )
            val response = makeRequest("auth_handler.php", params)
            val jsonObject = JSONObject(response)
            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(
                    Exception(
                        RemoteResponseParser.parseError(
                            jsonObject
                        )
                    )
                )
            }
            Result.success(true)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il controllo della registrazione dell'utente", e)
            Result.failure(e)
        }
    }

    suspend fun downloadImagesFromUrls(urls: List<String>): Result<List<Pair<String, ByteArray>>> = withContext(Dispatchers.IO) {
        try {
            val imageDataList = urls.map { url ->
                val response: HttpResponse = httpClient.get(url)

                if (response.status.isSuccess()) {
                    val bytes = response.readBytes()
                    url to bytes
                } else {
                    throw IOException("Errore nel caricamento da $url: ${response.status}")
                }
            }
            Result.success(imageDataList)
        } catch (e: Exception) {
            Log.e("RemoteRepository", "Errore nella richiesta: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun sendMailWithOTP(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "sendOTP",
                "email" to email
            )
            val response = makeRequest("auth_handler.php", params)

            if (!RemoteResponseParser.parseSuccess(JSONObject(response))) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(JSONObject(response))))
            }
            Result.success(true)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante l'invio dell'OTP", e)
            throw e
        }
    }

    suspend fun verifyOTP(email: String, otp: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "verifyOTP",
                "email" to email,
                "otp" to otp
            )
            val response = makeRequest("auth_handler.php", params)
            val jsonObject = JSONObject(response)

            if (!RemoteResponseParser.parseSuccess(jsonObject)) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }
            Result.success(true)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante la verifica dell'OTP", e)
            Result.failure(e)
        }
    }

    // Funzione helper per effettuare richieste HTTP
    private suspend fun makeRequest(endpoint: String, params: Map<String, String> = emptyMap()): String {
        try {
            val response: HttpResponse = httpClient.post("$baseUrl/$endpoint") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(FormDataContent(Parameters.build {
                    params.forEach { (key, value) -> append(key, value) }
                }))
            }

            return response.bodyAsText()
        } catch (e: Exception) {
            Log.e("RemoteRepository", "Errore nella richiesta: ${e.message}")
            return "{\"error\":\"${e.message}\"}"
        }
    }
}