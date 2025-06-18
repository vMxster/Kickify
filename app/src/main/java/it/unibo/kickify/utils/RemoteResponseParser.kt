package it.unibo.kickify.utils

import it.unibo.kickify.data.database.Address
import it.unibo.kickify.data.database.Cart
import it.unibo.kickify.data.database.CartProduct
import it.unibo.kickify.data.database.HistoryProduct
import it.unibo.kickify.data.database.Image
import it.unibo.kickify.data.database.Notification
import it.unibo.kickify.data.database.Order
import it.unibo.kickify.data.database.OrderDetails
import it.unibo.kickify.data.database.OrderProduct
import it.unibo.kickify.data.database.Product
import it.unibo.kickify.data.database.ProductDetails
import it.unibo.kickify.data.database.Review
import it.unibo.kickify.data.database.ReviewWithUserInfo
import it.unibo.kickify.data.database.TrackingShipping
import it.unibo.kickify.data.database.User
import it.unibo.kickify.data.database.Version
import it.unibo.kickify.data.database.WishlistProduct
import it.unibo.kickify.data.models.PaymentMethodInfo
import org.json.JSONArray
import org.json.JSONObject

class RemoteResponseParser {

    companion object {
        // Parser per i prodotti
        fun parseProducts(json: JSONArray): List<Product> {
            val products = mutableListOf<Product>()
            for (i in 0 until json.length()) {
                val productJson = json.getJSONObject(i)
                products.add(parseProduct(productJson))
            }
            return products
        }

        private fun parseProduct(json: JSONObject): Product {
            return Product(
                productId = json.getInt("ID_Prodotto"),
                name = json.getString("Nome"),
                desc = json.getString("Descrizione"),
                brand = json.getString("Marca"),
                type = json.getString("Tipo"),
                genre = json.optString("Genere", ""),
                price = json.getDouble("Prezzo"),
                addDate = json.getString("Data_Aggiunta"),
                state = json.getString("Sta_Tipo")
            )
        }

        fun parseVersions(jsonArray: JSONArray): List<Version> {
            val versions = mutableListOf<Version>()
            for (i in 0 until jsonArray.length()) {
                val versionJson = jsonArray.getJSONObject(i)
                versions.add(
                    Version(
                        productId = versionJson.getInt("ID_Prodotto"),
                        color = versionJson.getString("Colore"),
                        size = versionJson.getDouble("Taglia"),
                        quantity = versionJson.getInt("Quantita")
                    )
                )
            }
            return versions
        }

        fun parseProductDetails(json: JSONObject): ProductDetails {
            val product = parseProduct(json.getJSONObject("product"))

            val variants = mutableListOf<Version>()
            val productId = product.productId
            val variantsJson = json.getJSONObject("variants")
            val keys = variantsJson.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val variantJson = variantsJson.getJSONObject(key)
                variants.add(
                    Version(
                        productId = productId,
                        color = variantJson.getString("Colore"),
                        size = variantJson.getDouble("Taglia"),
                        quantity = variantJson.getInt("Quantita")
                    )
                )
            }

            val reviews = parseReviews(json.getJSONArray("reviews"))

            return ProductDetails(
                product = product,
                variants = variants,
                reviews = reviews,
                inWishlist = json.getBoolean("inWishlist"),
                inCart = json.getBoolean("inCart"),
                cartQuantity = json.getInt("cartQuantity")
            )
        }

        // Parser per il carrello
        fun parseCartItems(json: JSONArray): List<CartProduct> {
            val items = mutableListOf<CartProduct>()
            for (i in 0 until json.length()) {
                val itemJson = json.getJSONObject(i)
                items.add(
                    CartProduct(
                        cartId = itemJson.getInt("ID_Carrello"),
                        productId = itemJson.getInt("ID_Prodotto"),
                        color = itemJson.getString("Colore"),
                        size = itemJson.getDouble("Taglia"),
                        quantity = itemJson.getInt("Quantita"),
                    )
                )
            }
            return items
        }

        fun parseCart(json: JSONObject): Cart {
            val cartData = json.getJSONObject("cart")
            return Cart(
                cartId = cartData.getInt("ID_Carrello"),
                email = cartData.getString("Email"),
                totalValue = json.getDouble("cartTotal"),
                modifyDate = cartData.getString("Data_Modifica"),
            )
        }

        // Parser per la wishlist
        fun parseWishlistItems(json: JSONArray): List<WishlistProduct> {
            val items = mutableListOf<WishlistProduct>()
            for (i in 0 until json.length()) {
                val itemJson = json.getJSONObject(i)
                items.add(
                    WishlistProduct(
                        email = itemJson.optString("Email", ""),
                        productId = itemJson.getInt("ID_Prodotto")
                    )
                )
            }
            return items
        }

        // Parser per lista indirizzi
        fun parseAddressList(json: JSONArray): List<Address> {
            val items = mutableListOf<Address>()
            for (i in 0 until json.length()) {
                val itemJson = json.getJSONObject(i)
                items.add(
                    Address(
                        email = itemJson.getString("Email"),
                        street = itemJson.getString("Via"),
                        civic = itemJson.getString("NumeroCivico"),
                        cap = itemJson.getString("CAP"),
                        city = itemJson.getString("Citta"),
                        province = itemJson.getString("Provincia"),
                        nation = itemJson.getString("Nazione"),
                        default = itemJson.getInt("Predefinito") == 1
                    )
                )
            }
            return items
        }

        // Parser per lista metodi pagamento
        fun parsePaymentMethodList(json: JSONArray): List<PaymentMethodInfo> {
            val items = mutableListOf<PaymentMethodInfo>()
            for (i in 0 until json.length()) {
                val itemJson = json.getJSONObject(i)

                // add paypal account
                if(itemJson.optString("PayPal_Email") != "null"){
                    items.add(
                        PaymentMethodInfo.PayPal(
                            id = itemJson.getInt("Id"),
                            email = itemJson.optString("PayPal_Email"))
                    )
                } else {
                    items.add(
                        PaymentMethodInfo.CreditCard(
                            id = itemJson.getInt("Id"),
                            brand = itemJson.optString("CreditCard_brand"),
                            last4 = itemJson.optString("CreditCard_Last4"),
                            expirationMonth = itemJson.optInt("CreditCard_ExpMonth"),
                            expirationYear = itemJson.optInt("CreditCard_ExpYear")
                        )
                    )
                }
            }
            return items
        }

        // Parser per gli ordini
        fun parseOrderDetails(json: JSONObject): List<OrderDetails> {
            val orderDetailsList = mutableListOf<OrderDetails>()
            val orderArray = json.getJSONArray("orderDetails")

            for (i in 0 until orderArray.length()) {
                val orderJson = orderArray.getJSONObject(i)
                val order = Order(
                    orderId = orderJson.getInt("ID_Ordine"),
                    email = orderJson.optString("Email", ""),
                    shippingType = orderJson.getString("Tipo"),
                    orderDate = orderJson.getString("Data_Ordine"),
                    totalCost = orderJson.getDouble("Costo_Totale"),
                    paymentMethod = orderJson.getString("Metodo_Pagamento"),
                    isPresent = orderJson.optInt("Regalo", 0) == 1,
                    nomeDestinatario = orderJson.optString("Nome_Destinatario", ""),
                    cognomeDestinatario = orderJson.optString("Cognome_Destinatario", ""),
                    shippingEmail = orderJson.optString("Spe_Email", ""),
                    shippingStreet = orderJson.optString("Spe_Via", ""),
                    shippingCivic = orderJson.optInt("Spe_NumeroCivico", 0),
                    shippingCap = orderJson.optInt("Spe_CAP", 0),
                    shippingCity = orderJson.optString("Spe_Citta", "")
                )
                val isDelivered = orderJson.optBoolean("tracking_delivered", false)

                val productsArray = orderJson.getJSONArray("products")
                val products = mutableListOf<OrderProduct>()
                for (j in 0 until productsArray.length()) {
                    val productJson = productsArray.getJSONObject(j)
                    val product = OrderProduct(
                        productId = productJson.getInt("ID_Prodotto"),
                        orderId = productJson.getInt("ID_Ordine"),
                        color = productJson.getString("Colore"),
                        size = productJson.getDouble("Taglia"),
                        quantity = productJson.getInt("Quantita"),
                        purchasePrice = productJson.getDouble("Prezzo"),
                    )
                    products.add(product)
                }
                orderDetailsList.add(OrderDetails(order, isDelivered, products))
            }
            return orderDetailsList
        }

        fun parseOrders(json: JSONArray): List<Order> {
            val orders = mutableListOf<Order>()
            for (i in 0 until json.length()) {
                val orderJson = json.getJSONObject(i)
                orders.add(
                    Order(
                        orderId = orderJson.getInt("ID_Ordine"),
                        email = orderJson.getString("Email"),
                        shippingType = orderJson.getString("Tipo_Spedizione"),
                        orderDate = orderJson.getString("Data_Ordine"),
                        totalCost = orderJson.getDouble("Costo_Totale"),
                        paymentMethod = orderJson.getString("Metodo_Pagamento"),
                        isPresent = orderJson.getInt("Regalo") == 1,
                        nomeDestinatario = orderJson.optString("Nome_Destinatario"),
                        cognomeDestinatario = orderJson.optString("Cognome_Destinatario"),
                        shippingEmail = orderJson.optString("Spe_Email"),
                        shippingStreet = orderJson.optString("Spe_Via"),
                        shippingCivic = orderJson.optInt("Spe_NumeroCivico"),
                        shippingCap = orderJson.optInt("Spe_CAP"),
                        shippingCity = orderJson.optString("Spe_Citta")
                    )
                )
            }
            return orders
        }

        // Parser per il tracking dell'ordine
        fun parseOrderTracking(json: JSONObject): List<TrackingShipping> {
            val trackingStates = mutableListOf<TrackingShipping>()
            val statesJson = json.getJSONArray("tracking_states")
            for (i in 0 until statesJson.length()) {
                val stateJson = statesJson.getJSONObject(i)
                if (!stateJson.isNull("status")) {
                    trackingStates.add(
                        TrackingShipping(
                            orderId = json.optInt("order_id"),
                            state = stateJson.optString("status"),
                            position = stateJson.optString("location"),
                            updateTimestamp = stateJson.optString("timestamp"),
                            estimatedArrival = stateJson.optString("estimated_arrival"),
                            effectiveArrival = stateJson.optString("actual_arrival", "")
                        )
                    )
                }
            }

            return trackingStates
        }

        // Parser per le recensioni
        fun parseReviews(json: JSONArray): List<ReviewWithUserInfo> {
            val reviews = mutableListOf<ReviewWithUserInfo>()
            for (i in 0 until json.length()) {
                val reviewJson = json.getJSONObject(i)
                reviews.add(
                    ReviewWithUserInfo(
                        Review(
                            productId = reviewJson.optInt("ID_Prodotto", 0),
                            email = reviewJson.getString("Email"),
                            vote = reviewJson.getDouble("Punteggio"),
                            comment = reviewJson.optString("Descrizione", ""),
                            reviewDate = reviewJson.getString("Data_Recensione"),
                        ),
                        name = reviewJson.optString("Nome"),
                        surname = reviewJson.optString("Cognome")
                    )
                )
            }
            return reviews
        }

        // Parser per le notifiche
        fun parseNotifications(json: JSONArray): List<Notification> {
            val notifications = mutableListOf<Notification>()
            for (i in 0 until json.length()) {
                val notificationJson = json.getJSONObject(i)
                notifications.add(
                    Notification(
                        notificationId = notificationJson.getInt("ID_Notifica"),
                        type = notificationJson.getString("TipoNotifica"),
                        message = notificationJson.getString("Messaggio"),
                        date = notificationJson.getString("Timestamp_Invio"),
                        state = notificationJson.getString("Tipo"),
                        email = notificationJson.getString("Email"),
                    )
                )
            }
            return notifications
        }

        fun parseProductHistory(jsonObject: JSONObject): List<HistoryProduct> {
            val historyProducts = mutableListOf<HistoryProduct>()
            val historyArray = jsonObject.getJSONArray("history")
            for (i in 0 until historyArray.length()) {
                val productJson = historyArray.getJSONObject(i)
                historyProducts.add(
                    HistoryProduct(
                        productId = productJson.getInt("ID_Prodotto"),
                        price = productJson.getDouble("Prezzo"),
                        modifyDate = productJson.getString("Data_Modifica"),
                    )
                )
            }
            return historyProducts
        }

        fun parseProductRating(jsonObject: JSONObject): Double {
            return jsonObject.getDouble("rating")
        }

        fun parseProductsWithImages(jsonObject: JSONObject): List<Image> {
            val images = mutableListOf<Image>()
            if (jsonObject.has("productsImages")) {
                val imagesArray = jsonObject.getJSONArray("productsImages")

                for (i in 0 until imagesArray.length()) {
                    val itemJson = imagesArray.getJSONObject(i)
                    val image = Image(
                        productId = itemJson.getInt("ID_Prodotto"),
                        url = itemJson.getString("URL"),
                        number = itemJson.getInt("Numero")
                    )
                    images.add(image)
                }
            }

            return images
        }

        // Parser per i dati utente
        fun parseUserProfile(json: JSONObject): User {
            return User(
                email = json.getString("Email"),
                name = json.getString("Nome"),
                surname = json.getString("Cognome"),
                password = json.optString("Password"),
                phone = json.optString("Telefono", ""),
                registrationDate = json.optString("Data_Registrazione", ""),
                newsletterPreferences = json.optBoolean("Preferenze_Newsletter", false),
                urlPhoto = json.optString("URL_Foto", ""),
                role = json.getString("Ruolo")
            )
        }

        // Parser per risultati generici
        fun parseSuccess(json: JSONObject): Boolean {
            return json.optBoolean("success", false)
        }

        fun parseError(json: JSONObject): String {
            return json.optString("message", "Errore Sconosciuto")
        }
    }
}