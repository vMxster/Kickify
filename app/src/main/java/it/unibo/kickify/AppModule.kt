package it.unibo.kickify

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import it.unibo.kickify.data.repositories.SettingsRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    single { get<Context>().dataStore }

    single { SettingsRepository(get()) }

    //viewModel { SettingsViewModel(get()) }
}