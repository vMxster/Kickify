package it.unibo.kickify.data.repositories

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import it.unibo.kickify.data.database.Cart
import it.unibo.kickify.data.database.CartProduct
import it.unibo.kickify.data.database.HistoryProduct
import it.unibo.kickify.data.database.Image
import it.unibo.kickify.data.database.Notification
import it.unibo.kickify.data.database.Order
import it.unibo.kickify.data.database.OrderDetails
import it.unibo.kickify.data.database.Product
import it.unibo.kickify.data.database.ProductDetails
import it.unibo.kickify.data.database.Review
import it.unibo.kickify.data.database.TrackingShipping
import it.unibo.kickify.data.database.User
import it.unibo.kickify.data.database.WishlistProduct
import it.unibo.kickify.utils.RemoteResponseParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.emptyMap
import kotlin.collections.forEach
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.collections.mapOf
import kotlin.collections.mutableMapOf
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
            val jsonArray = JSONArray(response)
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
            val jsonArray = JSONArray(response)
            val images = RemoteResponseParser.parseProductsWithImages(jsonArray)
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
            val productData = jsonObject.getJSONObject("productData")
            val product = RemoteResponseParser.parseProductDetails(productData)
            Result.success(product)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero dei dati del prodotto $productId", e)
            Result.failure(e)
        }
    }

    suspend fun getProductHistory(productId: Int, lastAccess: String): Result<List<HistoryProduct>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getProductHistory",
                "productId" to productId.toString(),
                "last_access" to lastAccess
            )
            val response = makeRequest("product_handler.php", params)
            val jsonArray = JSONArray(response)
            val history = RemoteResponseParser.parseProductHistory(jsonArray)
            Result.success(history)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero della cronologia del prodotto $productId", e)
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
            val jsonArray = JSONArray(response)
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
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante la rimozione dalla wishlist", e)
            Result.failure(e)
        }
    }

    suspend fun clearWishlist(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "clearWishlist",
                "user_email" to email
            )
            val response = makeRequest("wishlist_handler.php", params)
            val jsonObject = JSONObject(response)
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante la pulizia della wishlist", e)
            Result.failure(e)
        }
    }

    // NOTIFICHE
    suspend fun getNotifications(email: String, lastAccess: String): Result<List<Notification>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getUserNotifications",
                "email" to email,
                "last_access" to lastAccess
            )
            val response = makeRequest("notification_handler.php", params)
            val jsonArray = JSONArray(response)
            val notifications = RemoteResponseParser.parseNotifications(jsonArray)
            Result.success(notifications)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero delle notifiche", e)
            Result.failure(e)
        }
    }

    suspend fun createNotification(
        email: String,
        message: String,
        type: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mutableMapOf(
                "action" to "createNotification",
                "email" to email,
                "message" to message,
                "type" to type
            )
            val response = makeRequest("notification_handler.php", params)
            val jsonObject = JSONObject(response)
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante la creazione della notifica", e)
            Result.failure(e)
        }
    }

    suspend fun markNotificationsAsRead(email: String, notificationIds: Array<Int>): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "markNotificationsAsRead",
                "email" to email,
                "notificationIds" to notificationIds.joinToString(",")
            )
            val response = makeRequest("notification_handler.php", params)
            val jsonObject = JSONObject(response)
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
            val jsonArray = JSONArray(response)
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
            val jsonArray = JSONArray(response)
            val orderDetails = RemoteResponseParser.parseOrderDetails(jsonArray)
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
            val tracking = RemoteResponseParser.parseOrderTracking(jsonObject)
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
            val success = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(success)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante la cancellazione della recensione", e)
            Result.failure(e)
        }
    }

    suspend fun getReviews(productId: Int, lastAccess: String): Result<List<Review>> = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "action" to "getProductReviews",
                "productId" to productId.toString(),
                "last_access" to lastAccess
            )
            val response = makeRequest("product_handler.php", params)
            val jsonArray = JSONArray(response)
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

            if (jsonObject.has("error")) {
                return@withContext Result.failure(Exception(RemoteResponseParser.parseError(jsonObject)))
            }

            val user = RemoteResponseParser.parseUserProfile(jsonObject)
            Result.success(user)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il login", e)
            Result.failure(e)
        }
    }

    suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        newsletter: Boolean,
        phone: String? = null
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val params = mutableMapOf(
                "action" to "registerUser",
                "email" to email,
                "firstName" to firstName,
                "lastName" to lastName,
                "password" to password,
                "newsletter" to (if (newsletter) "Y" else "N")
            )

            phone?.let { params["phone"] = it }

            val response = makeRequest("auth_handler.php", params)
            val jsonObject = JSONObject(response)
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
            val user = RemoteResponseParser.parseUserProfile(jsonObject)
            Result.success(user)
        } catch (e: Exception) {
            Log.e(tag, "Errore durante il recupero del profilo utente", e)
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
            val isRegistered = RemoteResponseParser.parseSuccess(jsonObject)
            Result.success(isRegistered)
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