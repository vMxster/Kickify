package it.unibo.kickify.ui.screens.settings

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.models.Theme
import it.unibo.kickify.data.repositories.SettingsRepository
import it.unibo.kickify.ui.AppStartDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow(AppStartDestination.LOADING)
    val startDestination: StateFlow<AppStartDestination> = _startDestination

    init {
        determineStartDestination()
    }

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

    val onboardingCompleted = repository.onboardingCompleted.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )

    private fun setUserId(value: String) = viewModelScope.launch {
        repository.setUserID(value)
    }

    private fun setUserName(value: String) = viewModelScope.launch {
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

    fun setEnabledLocation(value: Boolean) = viewModelScope.launch {
        repository.setLocationEnabled(value)
    }

    fun setEnabledPushNotification(value: Boolean) = viewModelScope.launch {
        repository.setPushNotificationEnabled(value)
    }

    fun setAppLanguage(appLanguageId: String) = viewModelScope.launch {
        repository.setAppLanguage(appLanguageId)
    }

    fun setOnboardingComplete(completed: Boolean) = viewModelScope.launch {
        repository.setOnboardingCompleted(completed)
    }

    fun setUserAccount(userid: String, username: String) = viewModelScope.launch {
        this@SettingsViewModel.setUserId(userid)
        this@SettingsViewModel.setUserName(username)
        //this@SettingsViewModel.setLastAccessNow()
    }

    fun removeUserAccount() = viewModelScope.launch {
       repository.removeUserAccount()
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


    private fun determineStartDestination() {
        viewModelScope.launch {
            val userId = repository.userID.first()
            val onboardingComplete = repository.onboardingCompleted.first()
            val biometricLoginEnabled = repository.biometricLogin.first()

            // first app start, user not logged in and onboarding not completed
            if (userId.isEmpty() && !onboardingComplete) {
                _startDestination.value = AppStartDestination.ONBOARDING

            // user not logged in and onboarding completed
            } else if(userId.isEmpty() && onboardingComplete){
                _startDestination.value = AppStartDestination.LOGIN

            } else {
                // user logged in
                if (biometricLoginEnabled) {
                    _startDestination.value = AppStartDestination.BIOMETRIC_AUTH
                } else {
                    _startDestination.value = AppStartDestination.HOME
                }
            }
        }
    }
}