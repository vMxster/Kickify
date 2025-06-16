package it.unibo.kickify

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import it.unibo.kickify.camerax.CameraXUtils
import it.unibo.kickify.data.database.KickifyDatabase
import it.unibo.kickify.data.repositories.AchievementsRepository
import it.unibo.kickify.data.repositories.AppRepository
import it.unibo.kickify.data.repositories.RemoteRepository
import it.unibo.kickify.data.repositories.SettingsRepository
import it.unibo.kickify.data.repositories.local.CartRepository
import it.unibo.kickify.data.repositories.local.ImageRepository
import it.unibo.kickify.data.repositories.local.OAuthUserRepository
import it.unibo.kickify.data.repositories.local.OrderRepository
import it.unibo.kickify.data.repositories.local.ProductCartRepository
import it.unibo.kickify.data.repositories.local.ProductRepository
import it.unibo.kickify.data.repositories.local.ReviewRepository
import it.unibo.kickify.data.repositories.local.UserRepository
import it.unibo.kickify.data.repositories.local.VersionRepository
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import it.unibo.kickify.ui.screens.cart.CartViewModel
import it.unibo.kickify.ui.screens.forgotPassword.ForgotPasswordOTPViewModel
import it.unibo.kickify.ui.screens.login.LoginViewModel
import it.unibo.kickify.ui.screens.notifications.NotificationViewModel
import it.unibo.kickify.ui.screens.orders.OrdersViewModel
import it.unibo.kickify.ui.screens.products.ProductsViewModel
import it.unibo.kickify.ui.screens.profile.ProfileViewModel
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import it.unibo.kickify.ui.screens.wishlist.WishlistViewModel
import it.unibo.kickify.utils.DatabaseReadyManager
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val appModule = module {
    single { get<Context>().dataStore }

    single { DatabaseReadyManager() }

    single(createdAtStart = true) {
        val dbManager = get<DatabaseReadyManager>()
        val db = KickifyDatabase.getInstance(androidContext())
        db.openHelper.readableDatabase
        dbManager.setDatabaseReady()
        db
    }

    single {
        HttpClient(OkHttp) {
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }
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

    single { ProductCartRepository(productCartDao = get<KickifyDatabase>().productCartDao()) }
    single { ImageRepository(imageDao = get<KickifyDatabase>().imageDao()) }
    single { UserRepository(userDao = get<KickifyDatabase>().userDao()) }
    single { CartRepository(cartDao = get<KickifyDatabase>().cartDao()) }
    single { OrderRepository(orderDao = get<KickifyDatabase>().orderDao()) }
    single { ReviewRepository(reviewDao = get<KickifyDatabase>().reviewDao()) }
    single { VersionRepository(versionDao = get<KickifyDatabase>().versionDao()) }
    single { SettingsRepository(dataStore = get()) }
    single { AchievementsRepository(dataStore = get()) }
    single { RemoteRepository(httpClient = get()) }
    single { ProductRepository(productDao = get<KickifyDatabase>().productDao()) }
    single { OAuthUserRepository(oauthDao = get<KickifyDatabase>().oauthDao()) }
    single {
        AppRepository(
            get(), get(), get(),
            get(), get(), get(),
            get(), get(), get(),
            get(), get(), get())
    }

    viewModel { SettingsViewModel(get(), get()) }
    viewModel { AchievementsViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ProductsViewModel(get(), get()) }
    viewModel { WishlistViewModel(get()) }
    viewModel { NotificationViewModel(get()) }
    viewModel { ForgotPasswordOTPViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { OrdersViewModel(get()) }
    viewModel { CartViewModel(get(), get()) }
}