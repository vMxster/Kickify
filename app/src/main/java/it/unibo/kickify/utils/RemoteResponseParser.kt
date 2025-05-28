package it.unibo.kickify.utils

import it.unibo.kickify.data.database.*
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

        fun parseProductDetails(json: JSONObject): ProductDetails {
            val product = parseProduct(json.getJSONObject("product"))

            val variants = mutableListOf<ProductVariant>()
            val variantsJson = json.getJSONObject("variants")
            val keys = variantsJson.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val variantJson = variantsJson.getJSONObject(key)
                variants.add(
                    ProductVariant(
                        color = variantJson.getString("Colore"),
                        size = variantJson.getString("Taglia"),
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
            return Cart(
                cartId = json.getInt("ID_Carrello"),
                email = json.getString("Email"),
                totalValue = json.getDouble("Valore_Totale"),
                modifyDate = json.getString("Data_Modifica")
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

        // Parser per gli ordini
        fun parseOrderDetails(json: JSONArray): List<OrderDetails> {
            val orderDetailsList = mutableListOf<OrderDetails>()
            for (i in 0 until json.length()) {
                val orderJson = json.getJSONObject(i)
                val order = Order(
                    orderId = orderJson.getInt("ID_Ordine"),
                    email = orderJson.getString("Email"),
                    shippingType = orderJson.getString("Tipo"),
                    orderDate = orderJson.getString("Data_Ordine"),
                    totalCost = orderJson.getDouble("Costo_Totale"),
                    paymentMethod = orderJson.getString("Metodo_Pagamento"),
                    isPresent = orderJson.getBoolean("Regalo"),
                    discountId = orderJson.optInt("ID_Sconto", 0),
                    nomeDestinatario = orderJson.optString("Nome_Destinatario", ""),
                    cognomeDestinatario = orderJson.optString("Cognome_Destinatario", ""),
                )
                val isDelivered = orderJson.optBoolean("tracking_delivered", false)

                val productsArray = orderJson.getJSONArray("prodotti")
                val products = mutableListOf<OrderProduct>()
                for (j in 0 until productsArray.length()) {
                    val productJson = productsArray.getJSONObject(j)
                    val product = OrderProduct(
                        productId = productJson.getInt("ID_Prodotto"),
                        orderId = productJson.getInt("ID_Ordine"),
                        color = productJson.getString("Colore"),
                        size = productJson.getDouble("Taglia"),
                        quantity = productJson.getInt("Quantita"),
                        purchasePrice = productJson.getDouble("Prezzo_Acquisto")
                    )
                    products.add(product)
                }
                orderDetailsList.add(OrderDetails(order, isDelivered, products.toList()))
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
                        shippingType = orderJson.getString("Tipo"),
                        orderDate = orderJson.getString("Data_Ordine"),
                        totalCost = orderJson.getDouble("Costo_Totale"),
                        paymentMethod = orderJson.getString("Metodo_Pagamento"),
                        isPresent = orderJson.getBoolean("Regalo"),
                        discountId = orderJson.optInt("ID_Sconto", 0),
                        nomeDestinatario = orderJson.optString("Nome_Destinatario", ""),
                        cognomeDestinatario = orderJson.optString("Cognome_Destinatario", ""),
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
        fun parseReviews(json: JSONArray): List<Review> {
            val reviews = mutableListOf<Review>()
            for (i in 0 until json.length()) {
                val reviewJson = json.getJSONObject(i)
                reviews.add(
                    Review(
                        productId = reviewJson.optInt("ID_Prodotto", 0),
                        email = reviewJson.getString("Email"),
                        vote = reviewJson.getDouble("Punteggio"),
                        comment = reviewJson.optString("Descrizione", ""),
                        reviewDate = reviewJson.getString("Data_Recensione"),
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

        fun parseProductHistory(jsonArray: JSONArray): List<HistoryProduct> {
            val historyProducts = mutableListOf<HistoryProduct>()
            for (i in 0 until jsonArray.length()) {
                val productJson = jsonArray.getJSONObject(i)
                historyProducts.add(
                    HistoryProduct(
                        productId = productJson.getInt("ID_Prodotto"),
                        price = productJson.getDouble("Prezzo"),
                        modifyDate = productJson.getString("Data_Aggiornamento")
                    )
                )
            }
            return historyProducts
        }

        fun parseProductRating(jsonObject: JSONObject): Double {
            return jsonObject.getDouble("rating")
        }

        fun parseProductsWithImages(jsonArray: JSONArray): List<Image> {
            val images = mutableListOf<Image>()
            for (i in 0 until jsonArray.length()) {
                val itemJson = jsonArray.getJSONObject(i)
                val image = Image(
                    productId = itemJson.getInt("ID_Prodotto"),
                    url = itemJson.getString("URL"),
                    number = itemJson.getInt("Numero")
                )
                images.add(image)
            }
            return images
        }

        // Parser per i dati utente
        fun parseUserProfile(json: JSONObject): User {
            return User(
                email = json.getString("Email"),
                name = json.getString("Nome"),
                surname = json.getString("Cognome"),
                password = json.getString("Password"),
                phone = json.optString("Telefono", ""),
                registrationDate = json.getString("Data_Registrazione"),
                newsletterPreferences = json.getBoolean("Preferenze_Newsletter"),
                urlPhoto = json.optString("URL_Foto", ""),
                role = json.getString("Ruolo")
            )
        }

        // Parser per risultati generici
        fun parseSuccess(json: JSONObject): Boolean {
            return json.optBoolean("success", false)
        }

        fun parseError(json: JSONObject): String {
            return json.optString("error", "Errore sconosciuto")
        }
    }
}