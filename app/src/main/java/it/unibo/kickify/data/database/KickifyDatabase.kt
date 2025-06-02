package it.unibo.kickify.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

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
        Image::class
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
}
