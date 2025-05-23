package it.unibo.kickify.ui.screens.settings

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.models.Theme
import it.unibo.kickify.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val userId = repository.userID.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ""
    )

    val userName = repository.username.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ""
    )

    val userImg = repository.userImgFilename.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ""
    )

    val theme = repository.theme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Theme.System
    )

    val biometricLogin = repository.biometricLogin.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )

    val lastAccess = repository.lastAccess.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = "0"
    )

    val enabledLocation = repository.locationEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )

    val enabledPushNotification = repository.pushNotificationEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )

    val appLanguage = repository.appLanguage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ""
    )


    fun setUserId(value: String) = viewModelScope.launch {
        repository.setUserID(value)
    }

    fun setUserName(value: String) = viewModelScope.launch {
        repository.setUserName(value)
    }

    fun setUserImg(value: String) = viewModelScope.launch {
        repository.setUserImgFilename(value)
    }

    fun setTheme(theme: Theme) = viewModelScope.launch {
        repository.setTheme(theme)
    }

    fun setBiometricLogin(value: Boolean) = viewModelScope.launch {
        repository.setBiometricLogin(value)
    }

    fun setLastAccess(value: String) = viewModelScope.launch {
        repository.setLastAccess(value)
    }

    fun setLastAccessNow() = viewModelScope.launch {
        val currentTime = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        repository.setLastAccess( formatter.format(currentTime))
    }

    fun setEnabledLocation(value: Boolean) = viewModelScope.launch {
        repository.setLocationEnabled(value)
    }

    fun setEnabledPushNotification(value: Boolean) = viewModelScope.launch {
        repository.setPushNotificationEnabled(value)
    }

    fun setAppLanguage(appLanguageId: String) = viewModelScope.launch {
        repository.setAppLanguage(appLanguageId)
    }

    fun setUserAccount(userid: String, username: String) = viewModelScope.launch {
        this@SettingsViewModel.setUserId(userid)
        this@SettingsViewModel.setUserName(username)
        this@SettingsViewModel.setLastAccessNow()
    }

    fun removeUserAccount() = viewModelScope.launch {
        this@SettingsViewModel.setUserId("")
        this@SettingsViewModel.setUserName("")
        this@SettingsViewModel.setUserImg("")
        this@SettingsViewModel.setTheme(Theme.System)
        this@SettingsViewModel.setBiometricLogin(false)
        this@SettingsViewModel.setLastAccess("")
        this@SettingsViewModel.setEnabledLocation(false)
        this@SettingsViewModel.setEnabledPushNotification(false)
        this@SettingsViewModel.setAppLanguage("en")
    }


    fun isStrongAuthenticationAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        ) {
            BiometricManager.BIOMETRIC_SUCCESS -> true // faceid or fingerprint auth available
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> true // available but not configured
            else -> false // no faceid nor fingerprint available
        }
    }

    fun isUserLoggedIn(): Boolean{
        return this.userId.value != "" && this.userName.value != ""
    }
}