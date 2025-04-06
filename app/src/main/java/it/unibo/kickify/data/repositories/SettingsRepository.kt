package it.unibo.kickify.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val USER_ID = stringPreferencesKey("userid")
        private val USER_NAME = stringPreferencesKey("username")
        private val THEME_KEY = stringPreferencesKey("theme")
    }

    // get userid
    val userID = dataStore.data.map { it[USER_ID] ?: "" }

    // set userid
    suspend fun setUserID(userID: String) = dataStore.edit { it[USER_ID] = userID }

    // get username
    val username = dataStore.data.map { it[USER_NAME] ?: "" }

    // set userid
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
}
