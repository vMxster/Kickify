package it.unibo.kickify.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import it.unibo.kickify.data.models.Theme
import it.unibo.kickify.ui.screens.settings.SettingsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val USER_ID = stringPreferencesKey("userid")
        private val USER_NAME = stringPreferencesKey("username")
        private val THEME_KEY = stringPreferencesKey("theme")
        private val BIOMETRIC_LOGIN = booleanPreferencesKey("biometric_login")
        private val LAST_ACCESS_KEY = longPreferencesKey("last_access")
        private val ENABLED_LOCATION = booleanPreferencesKey("enabled_location")
    }

    // get userid
    val userID = dataStore.data.map { it[USER_ID] ?: "" }

    // set userid
    suspend fun setUserID(userID: String) = dataStore.edit { it[USER_ID] = userID }

    // get username
    val username = dataStore.data.map { it[USER_NAME] ?: "" }

    // set username
    suspend fun setUserName(userName: String) = dataStore.edit { it[USER_NAME] = userName }

    // get theme
    val theme = dataStore.data
        .map { preferences ->
            try {
                Theme.valueOf(preferences[THEME_KEY] ?: "System")
            } catch (_: Exception) {
                Theme.System
            }
        }

    // set theme
    suspend fun setTheme(theme: Theme) = dataStore.edit { preferences ->
        preferences[THEME_KEY] = theme.toString()
    }

    // get biometric login
    val biometricLogin : Flow<Boolean> = dataStore.data.map {
        it[BIOMETRIC_LOGIN] ?: false
    }

    // set biometric login
    suspend fun setBiometricLogin(enabled: Boolean) = dataStore.edit {
        it[BIOMETRIC_LOGIN] = enabled
    }

    // get last access
    val lastAccess: Flow<Long> = dataStore.data.map { preferences ->
        preferences[LAST_ACCESS_KEY] ?: 0L
    }

    // set last access
    suspend fun setLastAccess(timestamp: Long) = dataStore.edit { preferences ->
        preferences[LAST_ACCESS_KEY] = timestamp
    }

    // get enabled location
    val locationEnabled : Flow<Boolean> = dataStore.data.map {
        it[ENABLED_LOCATION] ?: false
    }

    // set enabled location
    suspend fun setLocationEnabled(enabled: Boolean) = dataStore.edit {
        it[ENABLED_LOCATION] = enabled
    }

    val settingsFlow = dataStore.data.map { prefs ->
        SettingsState(
            theme = Theme.valueOf(prefs[THEME_KEY] ?: Theme.System.toString()),
            userID = prefs[USER_ID] ?: "",
            username = prefs[USER_NAME] ?: "",
            enabledBiometricLogin = prefs[BIOMETRIC_LOGIN] ?: false,
            enabledLocationServices = prefs[ENABLED_LOCATION] ?: false,
            lastAccess = prefs[LAST_ACCESS_KEY] ?: 0L
        )
    }

    suspend fun updateSettings(settingState: SettingsState){
        dataStore.edit { prefs ->
            prefs[THEME_KEY] = settingState.theme.toString()
            prefs[USER_ID] = settingState.userID
            prefs[USER_NAME] = settingState.username
            prefs[BIOMETRIC_LOGIN] = settingState.enabledBiometricLogin
            prefs[ENABLED_LOCATION] = settingState.enabledLocationServices
            prefs[LAST_ACCESS_KEY] = settingState.lastAccess
        }
    }
}
