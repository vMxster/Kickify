package it.unibo.kickify.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.models.Theme
import it.unibo.kickify.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ThemeState(val theme: Theme)
data class UserIDState(val userID: String)
data class UserNameState(val username: String)
data class LoginWithFingerPrintState(val enabled: Boolean)

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

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

    // get loggedinWithFingerprint from repository
    val getLoggedinWithFingerPrint = repository.loginWithFingerPrint.map { LoginWithFingerPrintState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LoginWithFingerPrintState(false)
    )

    // set loggedinWithFingerprint from repository
    fun changeLoggedinWithFingerPrint(enabled: Boolean) = viewModelScope.launch {
        repository.setLoginWithFingerPrint(enabled)
    }
}