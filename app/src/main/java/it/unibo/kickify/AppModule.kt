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
import it.unibo.kickify.camerax.CameraXutils
import it.unibo.kickify.data.database.KickifyDatabase
import it.unibo.kickify.data.repositories.LocalRepository
import it.unibo.kickify.data.repositories.RepositoryHandler
import it.unibo.kickify.data.repositories.RemoteRepository
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

    single { get<KickifyDatabase>().tripsDAO() }

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

    single {
        CameraXutils(androidContext())
    }

    single {
        PushNotificationManager(androidContext())
    }

    single { LocalRepository(dao = get(), contentResolver = androidContext().contentResolver) }
    single { SettingsRepository(dataStore = get()) }
    single { RemoteRepository(httpClient = get()) }
    single { RepositoryHandler(get(), get()) }

    viewModel { SettingsViewModel(get()) }
}