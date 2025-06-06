package it.unibo.kickify.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import it.unibo.kickify.data.repositories.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        Product::class,
        Version::class,
        User::class,
        Cart::class,
        CartProduct::class,
        Order::class,
        OrderProduct::class,
        TrackingShipping::class,
        ProductState::class,
        NotificationState::class,
        Address::class,
        Message::class,
        Notification::class,
        Discount::class,
        HistoryProduct::class,
        Review::class,
        Wishlist::class,
        DiscountUser::class,
        WishlistProduct::class,
        Image::class,
        UserOAuth::class
    ],
    version = 2)
abstract class KickifyDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun reviewDao(): ReviewDao
    abstract fun notificationDao(): NotificationDao
    abstract fun imageDao(): ImageDao
    abstract fun productCartDao(): ProductCartDao
    abstract fun versionDao(): VersionDao
    abstract fun notificationStateDao(): NotificationStateDao
    abstract fun productStateDao(): ProductStateDao
    abstract fun oauthDao(): UserOAuthDao

    companion object {
        @Volatile
        private var INSTANCE: KickifyDatabase? = null

        fun getInstance(context: Context): KickifyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KickifyDatabase::class.java,
                    "kickify_database"
                )
                    .addCallback(KickifyDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class KickifyDatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }

        private suspend fun populateDatabase(database: KickifyDatabase) {
            // Inserimento degli stati del prodotto
            val productStateDao = database.productStateDao()
            productStateDao.addProductState(ProductState(
                type = "Coming",
                desc = "Il prodotto è previsto in arrivo prossimamente"
            ))
            productStateDao.addProductState(ProductState(
                type = "Not Available",
                desc = "Il prodotto non è attualmente disponibile in magazzino"
            ))
            productStateDao.addProductState(ProductState(
                type = "Available",
                desc = "Il prodotto è attualmente disponibile in magazzino"
            ))

            // Inserimento degli stati della notifica
            val notificationStateDao = database.notificationStateDao()
            notificationStateDao.addNotificationState(NotificationState(
                type = "Read",
                desc = "The notification was read!"
            ))
            notificationStateDao.addNotificationState(NotificationState(
                type = "Unread",
                desc = "The notification was not read!"
            ))
        }
    }
}