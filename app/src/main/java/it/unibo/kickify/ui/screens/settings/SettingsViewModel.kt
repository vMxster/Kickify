package it.unibo.kickify.ui.screens.settings

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.models.Theme
import it.unibo.kickify.data.repositories.RepositoryHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ThemeState(val theme: Theme)
data class UserIDState(val userID: String)
data class UserNameState(val username: String)
data class BiometricLoginState(val enabled: Boolean)
data class EnabledLocationState(val enabled: Boolean)
data class LastAccessState(val timestamp: Long)

data class SettingsState(
    val theme: Theme = Theme.System,
    val userID: String = "",
    val username: String = "",
    val enabledBiometricLogin: Boolean = false,
    val enabledLocationServices: Boolean = false,
    val lastAccess: Long = 0
)

class SettingsViewModel(
    private val repository: RepositoryHandler
) : ViewModel() {

    private val _settings = MutableStateFlow(SettingsState())
    val settings: StateFlow<SettingsState> = _settings

    init {
        viewModelScope.launch {
            repository.settingsFlow.collect { _settings.value = it }
        }
    }

    fun isStrongAuthenticationAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> true // faceid or fingerprint auth available
            else -> false // no faceid nor fingerprint available
        }
    }

    // get theme from repository
    val getThemeState = repository.theme.map { ThemeState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ThemeState(Theme.System)
    )

    // set theme from repository
    fun changeTheme(theme: Theme) = viewModelScope.launch {
        repository.setTheme(theme)
    }

    // get userid from repository
    val getUserId = repository.userID.map { UserIDState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UserIDState("")
    )

    // set userid from repository
    fun changeUserId(userId: String) = viewModelScope.launch {
        repository.setUserID(userId)
    }

    // get username from repository
    val getUserName = repository.username.map { UserNameState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UserNameState("")
    )

    // set userid from repository
    fun changeUserName(userName: String) = viewModelScope.launch {
        repository.setUserName(userName)
    }

    // get biometric logina enabled from repository
    val getBiometricLoginEnabled = repository.biometricLoginEnabled.map { BiometricLoginState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = BiometricLoginState(false)
    )

    // set biometric login enabled from repository
    fun changeBiometricLoginEnabled(enabled: Boolean) = viewModelScope.launch {
        repository.setBiometricLoginEnabled(enabled)
    }

    // get location enabled from repository
    val getLocationEnabled = repository.locationEnabled.map { EnabledLocationState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = EnabledLocationState(false)
    )

    // set location enabled from repository
    fun changeLocationEnabled(enabled: Boolean) = viewModelScope.launch {
        repository.setLocationEnabled(enabled)
    }

    // get last access from repository
    val getLastAccess = repository.lastAccess.map { LastAccessState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LastAccessState(0L)
    )

    // set last access from repository
    fun setLastAccess(timestamp: Long) = viewModelScope.launch {
        repository.setLastAccess(timestamp)
    }
}