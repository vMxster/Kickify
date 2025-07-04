package it.unibo.kickify.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation

@Entity(
    tableName = "PRODOTTO",
    foreignKeys = [
        ForeignKey(
            entity = ProductState::class,
            parentColumns = ["Tipo"],
            childColumns = ["Sta_Tipo"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["Sta_Tipo"], unique = false)]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Prodotto")
    val productId: Int,

    @ColumnInfo(name = "Nome")
    val name: String,

    @ColumnInfo(name = "Descrizione")
    val desc: String,

    @ColumnInfo(name = "Marca")
    val brand: String,

    @ColumnInfo(name = "Tipo")
    val type: String,

    @ColumnInfo(name = "Genere")
    val genre: String,

    @ColumnInfo(name = "Prezzo")
    val price: Double,

    @ColumnInfo(name = "Data_Aggiunta")
    val addDate: String, // formato ISO date

    @ColumnInfo(name = "Sta_Tipo")
    val state: String // Available, Out of stock, ecc.
)

@Entity(
    tableName = "VARIANTE",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["ID_Prodotto"],
            childColumns = ["ID_Prodotto"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("ID_Prodotto", unique = false)],
    primaryKeys = ["ID_Prodotto", "Colore", "Taglia"],
)
data class Version(
    @ColumnInfo(name = "ID_Prodotto")
    val productId: Int,

    @ColumnInfo(name = "Colore")
    val color: String,

    @ColumnInfo(name = "Taglia")
    val size: Double,

    @ColumnInfo(name = "Quantita")
    val quantity: Int
)

@Entity(tableName = "UTENTE")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "Email")
    val email: String,

    @ColumnInfo(name = "Nome")
    val name: String,

    @ColumnInfo(name = "Cognome")
    val surname: String,

    @ColumnInfo(name = "Password")
    val password: String?,

    @ColumnInfo(name = "Telefono")
    val phone: String?,

    @ColumnInfo(name = "Data_Registrazione")
    val registrationDate: String,

    @ColumnInfo(name = "Preferenze_Newsletter")
    val newsletterPreferences: Boolean,

    @ColumnInfo(name = "URL_Foto")
    val urlPhoto: String?,

    @ColumnInfo(name = "Ruolo")
    val role: String
)

@Entity(
    tableName = "UTENTE_OAUTH",
    primaryKeys = ["Provider", "Provider_UserID"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["Email"],
            childColumns = ["Email"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("Email", unique = true)]
)
data class UserOAuth(
    @ColumnInfo(name = "Provider")
    val provider: String,

    @ColumnInfo(name = "Provider_UserID")
    val providerUserId: String,

    @ColumnInfo(name = "Data_Link")
    val dataLink: String,

    @ColumnInfo(name = "Email")
    val email: String
)

@Entity(
    tableName = "CARRELLO",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["Email"],
            childColumns = ["Email"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["Email"], unique = true)]
)
data class Cart(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Carrello")
    val cartId: Int,

    @ColumnInfo(name = "Email")
    val email: String,

    @ColumnInfo(name = "Data_Modifica")
    val modifyDate: String, // Formato ISO timestamp

    @ColumnInfo(name = "Valore_Totale")
    val totalValue: Double = 0.0
)

@Entity(
    tableName = "comprendere",
    foreignKeys = [
        ForeignKey(
            entity = Cart::class,
            parentColumns = ["ID_Carrello"],
            childColumns = ["ID_Carrello"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Version::class,
            parentColumns = ["ID_Prodotto", "Colore", "Taglia"],
            childColumns = ["ID_Prodotto", "Colore", "Taglia"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index("ID_Carrello", unique = false),
        Index("ID_Prodotto", unique = false)
              ],
    primaryKeys = ["ID_Carrello", "ID_Prodotto", "Colore", "Taglia"]
)
data class CartProduct(
    @ColumnInfo(name = "ID_Carrello")
    val cartId: Int,

    @ColumnInfo(name = "ID_Prodotto")
    val productId: Int,

    @ColumnInfo(name = "Colore")
    val color: String,

    @ColumnInfo(name = "Taglia")
    val size: Double,

    @ColumnInfo(name = "Quantita")
    val quantity: Int
)

@Entity(
    tableName = "ORDINE",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["Email"],
            childColumns = ["Email"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index("Email", unique = false)
    ]
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Ordine")
    val orderId: Int = 0,

    @ColumnInfo(name = "Data_Ordine")
    val orderDate: String, // Formato ISO timestamp

    @ColumnInfo(name = "Costo_Totale")
    val totalCost: Double,

    @ColumnInfo(name = "Metodo_Pagamento")
    val paymentMethod: String,

    @ColumnInfo(name = "Tipo_Spedizione")
    val shippingType: String,

    @ColumnInfo(name = "Regalo")
    val isPresent: Boolean,

    @ColumnInfo(name = "NomeDestinatario")
    val nomeDestinatario: String?,

    @ColumnInfo(name = "CognomeDestinatario")
    val cognomeDestinatario: String?,

    @ColumnInfo(name = "Email")
    val email: String,

    @ColumnInfo(name = "Spe_Email")
    val shippingEmail: String,

    @ColumnInfo(name = "Spe_Via")
    val shippingStreet: String,

    @ColumnInfo(name = "Spe_NumeroCivico")
    val shippingCivic: Int,

    @ColumnInfo(name = "Spe_CAP")
    val shippingCap: Int,

    @ColumnInfo(name = "Spe_Citta")
    val shippingCity: String
)

@Entity(
    tableName = "PRODOTTO_ORDINE",
    primaryKeys = ["ID_Prodotto", "Colore", "Taglia", "Quantita", "ID_Ordine"],
    foreignKeys = [
        ForeignKey(
            entity = Version::class,
            parentColumns = ["ID_Prodotto", "Colore", "Taglia"],
            childColumns = ["ID_Prodotto", "Colore", "Taglia"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Order::class,
            parentColumns = ["ID_Ordine"],
            childColumns = ["ID_Ordine"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("ID_Prodotto", unique = false), Index("ID_Ordine", unique = false)]
)
data class OrderProduct(
    @ColumnInfo(name = "ID_Prodotto")
    val productId: Int,

    @ColumnInfo(name = "Colore")
    val color: String,

    @ColumnInfo(name = "Taglia")
    val size: Double,

    @ColumnInfo(name = "Quantita")
    val quantity: Int,

    @ColumnInfo(name = "ID_Ordine")
    val orderId: Int,

    @ColumnInfo(name = "Prezzo_Acquisto")
    val purchasePrice: Double
)

@Entity(
    tableName = "Tracking_Spedizione",
    primaryKeys = ["ID_Ordine", "Stato"],
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["ID_Ordine"],
            childColumns = ["ID_Ordine"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("ID_Ordine", unique = false)]
)
data class TrackingShipping(
    @ColumnInfo(name = "ID_Ordine")
    val orderId: Int,

    @ColumnInfo(name = "Posizione")
    val position: String,

    @ColumnInfo(name = "Stato")
    val state: String,

    @ColumnInfo(name = "Arrivo_Effettivo")
    val effectiveArrival: String?,

    @ColumnInfo(name = "Arrivo_Stimato")
    val estimatedArrival: String,

    @ColumnInfo(name = "Timestamp_Aggiornamento")
    val updateTimestamp: String
)

@Entity(tableName = "STATO_PRODOTTO")
data class ProductState(
    @PrimaryKey
    @ColumnInfo(name = "Tipo")
    val type: String,

    @ColumnInfo(name = "Descrizione")
    val desc: String
)

@Entity(tableName = "STATO_NOTIFICA")
data class NotificationState(
    @PrimaryKey
    @ColumnInfo(name = "Tipo")
    val type: String,

    @ColumnInfo(name = "Descrizione")
    val desc: String
)

@Entity(
    tableName = "INDIRIZZO",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["Email"],
            childColumns = ["Email"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    primaryKeys = ["Email", "Via", "NumeroCivico", "CAP", "Citta"],
    indices = [Index(value = ["Email"], unique = false)]
)
data class Address(
    @ColumnInfo(name = "Email")
    val email: String,

    @ColumnInfo(name = "Via")
    val street: String,

    @ColumnInfo(name = "NumeroCivico")
    val civic: String,

    @ColumnInfo(name = "CAP")
    val cap: String,

    @ColumnInfo(name = "Citta")
    val city: String,

    @ColumnInfo(name = "Provincia")
    val province: String,

    @ColumnInfo(name = "Nazione")
    val nation: String,

    @ColumnInfo(name = "Predefinito")
    val default: Boolean
)

@Entity(
    tableName = "MESSAGGIO",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["Email"],
            childColumns = ["Email"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["Email"], unique = false)],
    primaryKeys = ["Email", "Timestamp_Invio"]
)
data class Message(
    @ColumnInfo(name = "Email")
    val email: String,

    @ColumnInfo(name = "Oggetto")
    val mailObject: String,

    @ColumnInfo(name = "Corpo")
    val body: String,

    @ColumnInfo(name = "Timestamp_Invio")
    val date: String // formato ISO timestamp
)

@Entity(
    tableName = "NOTIFICA",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["Email"],
            childColumns = ["Email"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = NotificationState::class,
            parentColumns = ["Tipo"],
            childColumns = ["TipoNotifica"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["Email"], unique = false),
        Index(value = ["TipoNotifica"], unique = false)
              ]
)
data class Notification(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Notifica")
    val notificationId: Int,

    @ColumnInfo(name = "TipoNotifica")
    val type: String,

    @ColumnInfo(name = "Messaggio")
    val message: String,

    @ColumnInfo(name = "Timestamp_Invio")
    val date: String, // formato ISO timestamp

    @ColumnInfo(name = "Tipo")
    val state: String,

    @ColumnInfo(name = "Email")
    val email: String
)

@Entity(
    tableName = "PRODOTTO_STORICO",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["ID_Prodotto"],
            childColumns = ["ID_Prodotto"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("ID_Prodotto", unique = false)],
    primaryKeys = ["ID_Prodotto", "Data_Modifica"]
)
data class HistoryProduct(
    @ColumnInfo(name = "ID_Prodotto")
    val productId: Int,

    @ColumnInfo(name = "Prezzo")
    val price: Double,

    @ColumnInfo(name = "Data_Modifica")
    val modifyDate: String // formato ISO timestamp
)

@Entity(
    tableName = "RECENSIONE",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["ID_Prodotto"],
            childColumns = ["ID_Prodotto"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["Email"],
            childColumns = ["Email"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["ID_Prodotto"], unique = false),
        Index(value = ["Email"], unique = false)
              ],
    primaryKeys = ["ID_Prodotto", "Email"]
)
data class Review(
    @ColumnInfo(name = "ID_Prodotto")
    val productId: Int,

    @ColumnInfo(name = "Email")
    val email: String,

    @ColumnInfo(name = "Voto")
    val vote: Double,

    @ColumnInfo(name = "Commento")
    val comment: String,

    @ColumnInfo(name = "Data_Recensione")
    val reviewDate: String // formato ISO Date
)

@Entity(
    tableName = "WISHLIST",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["Email"],
            childColumns = ["Email"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["Email"], unique = true)]
)
data class Wishlist(
    @PrimaryKey
    @ColumnInfo(name = "Email")
    val email: String,

    @ColumnInfo(name = "Data_Modifica")
    val updateDate: String // formato ISO date
)

@Entity(
    tableName = "aggiungere",
    foreignKeys = [
        ForeignKey(
            entity = Wishlist::class,
            parentColumns = ["Email"],
            childColumns = ["Email"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["ID_Prodotto"],
            childColumns = ["ID_Prodotto"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["Email", "ID_Prodotto"], unique = true),
              ],
    primaryKeys = ["ID_Prodotto", "Email"]
)
data class WishlistProduct(
    @ColumnInfo(name = "ID_Prodotto")
    val productId: Int,

    @ColumnInfo(name = "Email")
    val email: String
)

@Entity(
    tableName = "IMMAGINE",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["ID_Prodotto"],
            childColumns = ["ID_Prodotto"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["ID_Prodotto", "Numero"], unique = true)],
    primaryKeys = ["ID_Prodotto", "Numero"]
)
data class Image(
    @ColumnInfo(name = "ID_Prodotto")
    val productId: Int,

    @ColumnInfo(name = "Numero")
    val number: Int,

    @ColumnInfo(name = "URL")
    var url: String
)

// Relation Classes
data class CartWithProductInfo(
    @Embedded
    val cartProduct: CartProduct,

    @ColumnInfo(name = "Nome")
    val nome: String,

    @ColumnInfo(name = "Prezzo")
    val prezzo: Double,

    @ColumnInfo(name = "Genere")
    val genere: String
)

data class CompleteProduct(
    @Embedded
    val product: Product,

    @Relation(
        parentColumn = "ID_Prodotto",
        entityColumn = "ID_Prodotto"
    )
    val versions: List<Version> = listOf()
)

data class CartQuantity(val quantity: Int)

data class ReviewWithUser(
    @Embedded val review: Review,

    @ColumnInfo(name = "Nome")
    val name: String,

    @ColumnInfo(name = "Cognome")
    val surname: String
)

data class ProductDetail(
    val product: Product,
    val variants: List<Version>,
    val reviews: List<ReviewWithUser>,
    val inWishlist: Boolean,
    val inCart: Boolean,
    val cartQuantity: Int
)

data class OrderDetailedTracking(
    val orderInfo: OrderBasicInfo?,
    val trackingStates: List<TrackingState>,
    val products: List<OrderProductDetail>
)

data class OrderBasicInfo(
    val shippingType: String,
    val orderDate: String,
    val currentStatus: String,
    val currentLocation: String,
    val estimatedArrival: String
)

data class TrackingState(
    val status: String,
    val location: String,
    val timestamp: String,
    val estimatedArrival: String,
    val actualArrival: String?
)

data class OrderProductDetail(
    val image: String,
    val name: String,
    val size: Double,
    val quantity: Int,
    val color: String,
    val price: Double,
    val originalPrice: Double
)

data class OrderTrackingRowData(
    @ColumnInfo(name = "ID_Ordine")
    val orderId: Int,

    @ColumnInfo(name = "Tipo_Spedizione")
    val shippingType: String,

    @ColumnInfo(name = "Data_Ordine")
    val orderDate: String,

    @ColumnInfo(name = "Posizione")
    val location: String?,

    @ColumnInfo(name = "Stato")
    val status: String?,

    @ColumnInfo(name = "Arrivo_Effettivo")
    val actualArrival: String?,

    @ColumnInfo(name = "Arrivo_Stimato")
    val estimatedArrival: String?,

    @ColumnInfo(name = "Timestamp_Aggiornamento")
    val timestamp: String?,

    @ColumnInfo(name = "ID_Prodotto")
    val productId: Int,

    @ColumnInfo(name = "Nome")
    val name: String,

    @ColumnInfo(name = "Prezzo")
    val price: Double,

    @ColumnInfo(name = "Prezzo_Acquisto")
    val originalPrice: Double,

    @ColumnInfo(name = "Colore")
    val color: String,

    @ColumnInfo(name = "Taglia")
    val size: Double,

    @ColumnInfo(name = "Quantita")
    val quantity: Int
)

data class ReviewWithUserInfo(
    @Embedded
    val review: Review,

    @ColumnInfo(name = "Nome")
    val name: String,

    @ColumnInfo(name = "Cognome")
    val surname: String
)

data class OrderProductDetails(
    @ColumnInfo(name = "ID_Ordine")
    val orderId: Int,

    @ColumnInfo(name = "Data_Ordine")
    val orderDate: String,

    @ColumnInfo(name = "Costo_Totale")
    val totalCost: Double,

    @ColumnInfo(name = "Metodo_Pagamento")
    val paymentMethod: String,

    @ColumnInfo(name = "Regalo")
    val isPresent: Boolean,

    @ColumnInfo(name = "Tipo")
    val type: String,

    @ColumnInfo(name = "ID_Prodotto")
    val productId: Int,

    @ColumnInfo(name = "Nome")
    val name: String,

    @ColumnInfo(name = "Genere")
    val genre: String,

    @ColumnInfo(name = "Prezzo_Acquisto")
    val price: Double,

    @ColumnInfo(name = "Quantita")
    val quantity: Int,

    @ColumnInfo(name = "Taglia")
    val size: Double,

    @ColumnInfo(name = "Colore")
    val color: String,

    @ColumnInfo(name = "delivered_flag")
    val isDelivered: Boolean
)

data class ProductDetails(
    val product: Product,
    val variants: List<Version>,
    val reviews: List<ReviewWithUserInfo>,
    val inWishlist: Boolean,
    val inCart: Boolean,
    val cartQuantity: Int
)

data class TrackingStateOrder(
    val orderId: Int,
    val status: String,
    val location: String,
    val timestamp: String,
    val estimatedArrival: String,
    val actualArrival: String?
)

data class TrackingProduct(
    val orderId: Int,
    val image: String,
    val name: String,
    val size: String,
    val quantity: Int,
    val color: String,
    val price: Double,
    val originalPrice: Double
)

data class OrderDetails(
    @Embedded
    val order: Order,

    val isDelivered: Boolean = false,

    val products: List<OrderProduct> = emptyList()
)

data class OrderAddress (
    @ColumnInfo(name = "Spe_Via")
    val shippingStreet: String,

    @ColumnInfo(name = "Spe_NumeroCivico")
    val shippingCivic: Int,

    @ColumnInfo(name = "Spe_CAP")
    val shippingCap: Int,

    @ColumnInfo(name = "Spe_Citta")
    val shippingCity: String
)

data class ProductWithImage(
    @Embedded
    val product: Product,

    @Relation(
        entity = Image::class,
        parentColumn = "ID_Prodotto",
        entityColumn = "ID_Prodotto"
    )
    val images: Image
)