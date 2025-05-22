package it.unibo.kickify

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import it.unibo.kickify.data.repositories.SettingsRepository
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import it.unibo.kickify.camerax.CameraXUtils
import it.unibo.kickify.data.database.KickifyDatabase
import it.unibo.kickify.data.repositories.AppRepository
import it.unibo.kickify.data.repositories.local.CartRepository
import it.unibo.kickify.data.repositories.local.OrderRepository
import it.unibo.kickify.data.repositories.local.ProductRepository
import it.unibo.kickify.data.repositories.RemoteRepository
import it.unibo.kickify.data.repositories.local.UserRepository
import it.unibo.kickify.data.repositories.local.WishlistRepository
import it.unibo.kickify.data.repositories.local.NotificationRepository
import it.unibo.kickify.data.repositories.local.ReviewRepository
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val appModule = module {
    single { get<Context>().dataStore }

    single {
        Room.databaseBuilder(
            androidContext(),
            KickifyDatabase::class.java,
            "kickify"
        ).fallbackToDestructiveMigration(false).build()
    }

    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    single { CameraXUtils(androidContext()) }

    single { PushNotificationManager(androidContext()) }

    single { ProductRepository(productDao = get<KickifyDatabase>().productDao()) }
    single { UserRepository(userDao = get<KickifyDatabase>().userDao()) }
    single { CartRepository(cartDao = get<KickifyDatabase>().cartDao()) }
    single { OrderRepository(orderDao = get<KickifyDatabase>().orderDao(), database = get()) }
    single { WishlistRepository(wishlistDao = get<KickifyDatabase>().wishlistDao()) }
    single { ReviewRepository(reviewDao = get<KickifyDatabase>().reviewDao()) }
    single { NotificationRepository(notificationDao = get<KickifyDatabase>().notificationDao()) }
    single { SettingsRepository(dataStore = get()) }
    single { RemoteRepository(httpClient = get()) }
    single { AppRepository(get(), get(), get(), get(), get(), get(), get(), get()) }

    viewModel { SettingsViewModel(get()) }
}