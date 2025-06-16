package it.unibo.kickify.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import it.unibo.kickify.data.models.PaymentMethodInfo
import it.unibo.kickify.data.models.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val USER_ID = stringPreferencesKey("userid")
        private val USER_NAME = stringPreferencesKey("username")
        private val USER_IMG = stringPreferencesKey("userImgFilename")
        private val LOGGEDIN = booleanPreferencesKey("loggedIn")
        private val THEME = stringPreferencesKey("theme")
        private val ENABLED_BIOMETRIC_LOGIN = booleanPreferencesKey("biometric_login")
        private val ENABLED_LOCATION = booleanPreferencesKey("enabled_location")
        private val ENABLED_PUSH_NOTIFICATION = booleanPreferencesKey("enabled_pushNotification")
        private val APP_LANG = stringPreferencesKey("app_lang")
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("completed_onboarding")
        private val LAST_ACCESS = stringPreferencesKey("last_access")
        private val PAYMENT_METHODS = stringPreferencesKey("payment_methods_info")
    }

    // get userid
    val userID = dataStore.data.map { it[USER_ID] ?: "" }

    // set userid
    suspend fun setUserID(userID: String) = dataStore.edit { it[USER_ID] = userID }

    // get username
    val username = dataStore.data.map { it[USER_NAME] ?: "" }

    // set username
    suspend fun setUserName(userName: String) = dataStore.edit { it[USER_NAME] = userName }

    // get userImgFilename
    val userImgFilename = dataStore.data.map { it[USER_IMG] ?: "" }

    // set userImgFilename
    suspend fun setUserImgFilename(userImgFilename: String) = dataStore.edit { it[USER_IMG] = userImgFilename }

    // get is used logged in
    val userLoggedIn : Flow<Boolean> = dataStore.data.map {
        it[LOGGEDIN] ?: false
    }

    // set is user logged in
    suspend fun setUserLoggedIn(loggedIn: Boolean) = dataStore.edit {
        it[LOGGEDIN] = loggedIn
    }

    // get theme
    val theme = dataStore.data
        .map { preferences ->
            try {
                Theme.valueOf(preferences[THEME] ?: "System")
            } catch (_: Exception) {
                Theme.System
            }
        }

    // set theme
    suspend fun setTheme(theme: Theme) = dataStore.edit { preferences ->
        preferences[THEME] = theme.toString()
    }

    // get biometric login
    val biometricLogin : Flow<Boolean> = dataStore.data.map {
        it[ENABLED_BIOMETRIC_LOGIN] ?: false
    }

    // set biometric login
    suspend fun setBiometricLogin(enabled: Boolean) = dataStore.edit {
        it[ENABLED_BIOMETRIC_LOGIN] = enabled
    }

    // get enabled location
    val locationEnabled : Flow<Boolean> = dataStore.data.map {
        it[ENABLED_LOCATION] ?: false
    }

    // set enabled location
    suspend fun setLocationEnabled(enabled: Boolean) = dataStore.edit {
        it[ENABLED_LOCATION] = enabled
    }

    // get enabled push notification
    val pushNotificationEnabled : Flow<Boolean> = dataStore.data.map {
        it[ENABLED_PUSH_NOTIFICATION] ?: false
    }

    // set enabled push notification
    suspend fun setPushNotificationEnabled(enabled: Boolean) = dataStore.edit {
        it[ENABLED_PUSH_NOTIFICATION] = enabled
    }

    // get last access
    val lastAccess: Flow<String> = dataStore.data.map { preferences ->
        preferences[LAST_ACCESS] ?: "1970-01-01 00:00:00"
    }

    // set last access
    suspend fun setLastAccess() = dataStore.edit { preferences ->
        val currentTime = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        preferences[LAST_ACCESS] = formatter.format(currentTime)
    }

    // get app language
    val appLanguage = dataStore.data.map { it[APP_LANG] ?: "en" }

    // set app language id
    suspend fun setAppLanguage(appLanguageId: String) = dataStore.edit { it[APP_LANG] = appLanguageId }

    // get completed onboarding
    val onboardingCompleted : Flow<Boolean> = dataStore.data.map {
        it[ONBOARDING_COMPLETED] ?: false
    }

    // set completed onboarding
    suspend fun setOnboardingCompleted(completed: Boolean) = dataStore.edit {
        it[ONBOARDING_COMPLETED] = completed
    }

    private val jsonSerializer = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
    }

    suspend fun savePaymentMethods(paymentMethodsList: List<PaymentMethodInfo>?) = dataStore.edit {
        if (paymentMethodsList.isNullOrEmpty()) {
            it.remove(PAYMENT_METHODS)
        } else {
            val jsonString = jsonSerializer.encodeToString(
                ListSerializer(PaymentMethodInfo.serializer()), paymentMethodsList)
            it[PAYMENT_METHODS] = jsonString
        }
    }

    fun getPaymentMethods(): Flow<List<PaymentMethodInfo>> = dataStore.data.map {
        val jsonString = it[PAYMENT_METHODS]
        if (jsonString == null) {
            emptyList() // no payment methods saved
        } else {
            try {
                jsonSerializer.decodeFromString(
                    ListSerializer(PaymentMethodInfo.serializer()), jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun removeUserAccount() {
        dataStore.edit { it.clear() }
    }
}