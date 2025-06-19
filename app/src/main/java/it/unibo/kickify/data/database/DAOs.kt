package it.unibo.kickify.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ProductDao {
    @Query("""
        SELECT * FROM PRODOTTO p 
        JOIN VARIANTE v ON p.ID_Prodotto = v.ID_Prodotto 
        WHERE p.ID_Prodotto = :productId
    """)
    suspend fun getProductWithVariants(productId: Int): CompleteProduct?

    @Query("""
        SELECT * FROM VARIANTE v 
        WHERE v.ID_Prodotto = :productId
    """)
    suspend fun getVariantsByProductId(productId: Int): List<Version>

    @Query("""
        SELECT * FROM PRODOTTO p 
        JOIN IMMAGINE i ON p.ID_Prodotto = i.ID_Prodotto 
        WHERE p.ID_Prodotto = :productId
        ORDER BY i.Numero
    """)
    suspend fun getProductImages(productId: Int): List<Image>

    @Query("""
        SELECT * FROM PRODOTTO 
        WHERE Genere = :genre 
        AND Tipo = :type
    """)
    suspend fun getProductsByGenreAndType(genre: String, type: String): List<Product>

    @Query("""
        SELECT DISTINCT Colore FROM VARIANTE 
        WHERE ID_Prodotto = :productId 
        AND Taglia = :size 
        AND Quantita > 0 ORDER BY Colore
    """)
    suspend fun getColorsBySize(productId: Int, size: Double): List<String>

    @Query("""
        SELECT DISTINCT Taglia FROM VARIANTE 
        WHERE ID_Prodotto = :productId 
        AND Colore = :color 
        AND Quantita > 0 ORDER BY Taglia
    """)
    suspend fun getSizesByColor(productId: Int, color: String): List<Double>

    @Query("""
        SELECT Quantita FROM VARIANTE 
        WHERE ID_Prodotto = :productId 
        AND Colore = :color 
        AND Taglia = :size
    """)
    suspend fun getQuantity(productId: Int, color: String, size: Double): Int

    @Query("""
        SELECT * FROM PRODOTTO p, IMMAGINE i 
        WHERE p.ID_Prodotto = i.ID_Prodotto 
        AND i.Numero = 1 ORDER BY p.Data_Aggiunta DESC
    """)
    suspend fun getProductsWithImage(): Map<Product, Image>

    @Query("""
        SELECT * FROM VARIANTE
        WHERE ID_Prodotto = :productId
    """)
    suspend fun getProductVariants(productId: Int): List<Version>

    @Transaction
    suspend fun getProductData(productId: Int, userEmail: String): ProductDetail? {
        val product = getProductBase(productId) ?: return null
        val variants = getProductVariants(productId)
        val reviews = getProductReviews(productId)
        val inWishlist = isProductInWishlist(productId, userEmail)
        val cartInfo = getProductCartInfo(productId, userEmail)

        return ProductDetail(
            product = product,
            variants = variants,
            reviews = reviews,
            inWishlist = inWishlist,
            inCart = cartInfo != null,
            cartQuantity = cartInfo?.quantity ?: 0
        )
    }

    @Query("""
        SELECT * FROM PRODOTTO 
        WHERE ID_Prodotto = :productId
    """)
    suspend fun getProductBase(productId: Int): Product?

    @Query("""
        SELECT r.*, u.Nome, u.Cognome 
        FROM RECENSIONE r
        JOIN UTENTE u ON r.Email = u.Email
        WHERE r.ID_Prodotto = :productId
    """)
    suspend fun getProductReviews(productId: Int): List<ReviewWithUser>

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM aggiungere 
            WHERE ID_Prodotto = :productId AND Email = :email
        )
    """)
    suspend fun isProductInWishlist(productId: Int, email: String): Boolean

    @Query("""
        SELECT comp.Quantita as quantity
        FROM CARRELLO c
        JOIN comprendere comp ON c.ID_Carrello = comp.ID_Carrello
        WHERE c.Email = :email AND comp.ID_Prodotto = :productId
        LIMIT 1
    """)
    suspend fun getProductCartInfo(productId: Int, email: String): CartQuantity?

    @Query("""
        SELECT * FROM PRODOTTO_STORICO ps1
        WHERE ps1.Data_Modifica = (
            SELECT MAX(ps2.Data_Modifica) FROM PRODOTTO_STORICO ps2
            WHERE ps2.ID_Prodotto = ps1.ID_Prodotto
        )
    """)
    suspend fun getProductsHistory(): List<HistoryProduct>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductsHistory(remoteHistory: List<HistoryProduct>)

    @Query("""
        SELECT * FROM PRODOTTO 
        ORDER BY Data_Aggiunta DESC
    """)
    suspend fun getNewProducts(): List<Product>

    @Transaction
    @Query("SELECT p.*, i.URL FROM PRODOTTO p LEFT JOIN IMMAGINE i ON p.ID_Prodotto = i.ID_Prodotto " +
            "WHERE i.Numero = 1 AND (p.Nome LIKE :query OR p.Marca LIKE :query) GROUP BY p.ID_Prodotto")
    suspend fun searchProducts(query: String): List<ProductWithImage>
}

@Dao
interface ProductStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProductState(productState: ProductState)
}

@Dao
interface VersionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductVariant(version: Version)
}

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: Image)
}

@Dao
interface UserDao {
    @Query("""
        SELECT * FROM UTENTE WHERE Email = :email
    """)
    suspend fun getUserProfile(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerUser(user: User): Long

    @Query("""
        SELECT * FROM UTENTE WHERE Email = :email AND Password = :password
    """)
    suspend fun loginUser(email: String, password: String): User?

    @Query("""
        UPDATE UTENTE SET Password = :password WHERE Email = :email
    """)
    suspend fun changePassword(email: String, password: String): Int

    @Query("""
        SELECT Email FROM UTENTE WHERE Email = :email
    """)
    suspend fun checkUserExists(email: String): String?
}

@Dao
interface UserOAuthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserOAuth(userOAuth: UserOAuth): Long

    @Query("""
        SELECT * FROM UTENTE_OAUTH WHERE Email = :email
    """)
    suspend fun getUserOAuth(email: String): UserOAuth?

    @Query("""
        SELECT Email FROM UTENTE_OAUTH WHERE Email = :email
    """)
    suspend fun checkUserOAuthExists(email: String): String?
}

@Dao
interface CartDao {
    @Query("""
        SELECT * FROM CARRELLO WHERE Email = :email
    """)
    suspend fun getCartByEmail(email: String): Cart

    @Transaction
    @Query("""
        UPDATE CARRELLO SET Valore_Totale = (SELECT SUM(c.Quantita * p.Prezzo) 
            FROM comprendere c JOIN PRODOTTO p ON c.ID_Prodotto = p.ID_Prodotto 
            WHERE c.ID_Carrello = :cartId) 
        WHERE ID_Carrello = :cartId
    """)
    suspend fun updateCartTotal(cartId: Int): Int

    @Query("""
        UPDATE CARRELLO SET Valore_Totale = 0 
        WHERE ID_Carrello = :cartId
    """)
    suspend fun clearCart(cartId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cart: Cart)
}

@Dao
interface ProductCartDao {
    @Query("""
        SELECT c.*, p.Nome, p.Prezzo, p.Genere 
        FROM comprendere c JOIN PRODOTTO p ON c.ID_Prodotto = p.ID_Prodotto 
        WHERE c.ID_Carrello = :cartId
    """)
    suspend fun getCartItems(cartId: Int): List<CartWithProductInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(item: CartProduct)

    @Query("""
        UPDATE comprendere SET Quantita = :quantity 
        WHERE ID_Carrello = :cartId AND ID_Prodotto = :productId 
        AND Colore = :color AND Taglia = :size
    """)
    suspend fun updateCartItemQuantity(cartId: Int?, productId: Int, color: String, size: Double, quantity: Int): Int

    @Query("""
    DELETE FROM comprendere 
    WHERE ID_Carrello = :cartId AND ID_Prodotto = :productId 
    AND Colore = :color AND Taglia = :size
""")
    suspend fun removeFromCart(cartId: Int, productId: Int, color: String, size: Double): Int

    @Query("""
        DELETE FROM comprendere 
        WHERE ID_Carrello = :cartId
    """)
    suspend fun clearCart(cartId: Int)
}

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderProduct(orderProduct: OrderProduct)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackingInfo(trackingShipping: TrackingShipping)

    @Query("""
        SELECT * FROM ORDINE o 
        WHERE o.Email = :email 
        ORDER BY o.Data_Ordine DESC
    """)
    suspend fun getOrders(email: String): List<Order>

    @Query("""
        SELECT o.Spe_Via, o.Spe_NumeroCivico, o.Spe_CAP, o.Spe_Citta 
        FROM ORDINE o 
        WHERE o.ID_Ordine = :orderId 
        ORDER BY o.Data_Ordine DESC
    """)
    suspend fun getOrderAddress(orderId: Int): OrderAddress

    @Query("""
        SELECT 
            o.ID_Ordine,
            o.Data_Ordine,
            o.Costo_Totale,
            o.Metodo_Pagamento,
            o.Regalo,
            o.Tipo_Spedizione AS Tipo,
            
            p.ID_Prodotto,
            p.Nome,
            p.Genere,

            po.Prezzo_Acquisto,
            po.Quantita,
            po.Taglia,
            po.Colore,

            (
              SELECT COUNT(*) 
              FROM Tracking_Spedizione ts
              WHERE ts.ID_Ordine = o.ID_Ordine 
                AND ts.Stato = 'Delivered'
                AND ts.Arrivo_Effettivo IS NOT NULL
            ) > 0 AS delivered_flag

        FROM ORDINE o
        JOIN PRODOTTO_ORDINE po ON o.ID_Ordine = po.ID_Ordine
        JOIN PRODOTTO p ON po.ID_Prodotto = p.ID_Prodotto
        WHERE o.Email = :email
        ORDER BY o.Data_Ordine DESC, p.ID_Prodotto ASC
    """)
    suspend fun getOrdersWithProducts(email: String): List<OrderProductDetails>

    @Transaction
    suspend fun getOrderTracking(orderId: Int): OrderDetailedTracking {
        val rawData = getOrderTrackingRawData(orderId)

        if (rawData.isEmpty()) return OrderDetailedTracking(
            orderInfo = null,
            trackingStates = emptyList(),
            products = emptyList()
        )

        val first = rawData.first()
        val orderInfo = OrderBasicInfo(
            shippingType = first.shippingType,
            orderDate = first.orderDate,
            currentStatus = first.status ?: "",
            currentLocation = first.location ?: "",
            estimatedArrival = first.estimatedArrival ?: ""
        )

        val states = listOf("Placed", "In progress", "Shipped", "Delivered")
        val trackingStates = states.map { state ->
            val matchingStatus = rawData.find { it.status == state }

            TrackingState(
                status = state,
                location = matchingStatus?.location ?: "",
                timestamp = matchingStatus?.timestamp ?: "",
                estimatedArrival = matchingStatus?.estimatedArrival ?: "",
                actualArrival = matchingStatus?.actualArrival
            )
        }

        val uniqueProducts = mutableMapOf<String, OrderProductDetail>()
        rawData.forEach { row ->
            val productKey = "${row.productId}_${row.size}_${row.color}"
            if (!uniqueProducts.containsKey(productKey)) {
                uniqueProducts[productKey] = OrderProductDetail(
                    image = "${row.name}_1",
                    name = row.name,
                    size = row.size,
                    quantity = row.quantity,
                    color = row.color,
                    price = row.price,
                    originalPrice = row.originalPrice
                )
            }
        }

        return OrderDetailedTracking(
            orderInfo = orderInfo,
            trackingStates = trackingStates,
            products = uniqueProducts.values.toList()
        )
    }

    @Query("""
        SELECT 
            o.ID_Ordine,
            o.Tipo_Spedizione, 
            o.Data_Ordine, 
            ts.Posizione, 
            ts.Stato, 
            ts.Arrivo_Effettivo, 
            ts.Arrivo_Stimato, 
            ts.Timestamp_Aggiornamento, 
            p.ID_Prodotto, 
            p.Nome, 
            p.Prezzo, 
            po.Prezzo_Acquisto, 
            po.Colore, 
            po.Taglia, 
            po.Quantita 
        FROM ORDINE o
        LEFT JOIN Tracking_Spedizione ts ON o.ID_Ordine = ts.ID_Ordine
        JOIN PRODOTTO_ORDINE po ON o.ID_Ordine = po.ID_Ordine
        JOIN PRODOTTO p ON po.ID_Prodotto = p.ID_Prodotto
        WHERE o.ID_Ordine = :orderId
        ORDER BY ts.Timestamp_Aggiornamento DESC
    """)
    suspend fun getOrderTrackingRawData(orderId: Int): List<OrderTrackingRowData>
}

@Dao
interface WishlistDao {
    @Query("""
        SELECT p.* FROM PRODOTTO p 
        JOIN aggiungere a ON p.ID_Prodotto = a.ID_Prodotto 
        WHERE a.Email = :email ORDER BY p.Nome
    """)
    suspend fun getWishlistItems(email: String): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(wishlistProduct: WishlistProduct): Long

    @Query("""
        DELETE FROM aggiungere 
        WHERE Email = :email 
        AND ID_Prodotto = :productId
    """)
    suspend fun removeFromWishlist(email: String, productId: Int): Int

    @Query("""
        DELETE FROM aggiungere 
        WHERE Email = :email
    """)
    suspend fun clearWishlist(email: String): Int

    @Query("""
        SELECT COUNT(*) FROM aggiungere 
        WHERE Email = :email
    """)
    suspend fun getWishlistCount(email: String): Int

    @Query("""
        SELECT COUNT(*) FROM aggiungere 
        WHERE Email = :email 
        AND ID_Prodotto = :productId
    """)
    suspend fun isInWishlist(email: String, productId: Int): Int
}

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReview(review: Review): Long

    @Query("""
        SELECT r.*, u.Nome, u.Cognome 
        FROM RECENSIONE r 
        JOIN UTENTE u ON r.Email = u.Email 
        WHERE r.ID_Prodotto = :productId AND r.Data_Recensione > :lastAccess 
        ORDER BY r.Data_Recensione DESC
    """)
    suspend fun getProductReviews(productId: Int, lastAccess: String): List<ReviewWithUserInfo>

    @Query("""
        DELETE FROM RECENSIONE WHERE Email = :email 
        AND ID_Prodotto = :productId
    """)
    suspend fun deleteReview(email: String, productId: Int): Int

    @Query("""
        SELECT AVG(Voto) FROM RECENSIONE 
        WHERE ID_Prodotto = :productId
    """)
    suspend fun getProductRating(productId: Int): Double

    @Query("""
        SELECT COUNT(*) FROM ORDINE o 
        JOIN PRODOTTO_ORDINE po ON o.ID_Ordine = po.ID_Ordine 
        WHERE o.Email = :email AND po.ID_Prodotto = :productId""")
    suspend fun canUserReview(email: String, productId: Int): Int
}

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createNotification(notification: Notification)

    @Query("""
        SELECT n.* FROM NOTIFICA n WHERE n.Email = :email 
        ORDER BY n.Timestamp_Invio DESC
    """)
    suspend fun getUserNotifications(email: String): List<Notification>

    @Query("""
        UPDATE NOTIFICA SET Tipo = 'Read' WHERE Email = :email
    """)
    suspend fun markAllNotificationsAsRead(email: String): Int

    @Query("""
        UPDATE NOTIFICA SET Tipo = 'Read' 
        WHERE Email = :email AND ID_Notifica IN (:notificationIds)
    """)
    suspend fun markNotificationsAsRead(email: String, notificationIds: List<Int>): Int

    @Query("""
        SELECT COUNT(*) FROM NOTIFICA 
        WHERE Email = :email AND Tipo = 'Unread'
    """)
    suspend fun getUnreadNotificationsCount(email: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNotification(notification: Notification)
}

@Dao
interface NotificationStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNotificationState(notificationState: NotificationState)
}