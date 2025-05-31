package it.unibo.kickify.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.database.User
import it.unibo.kickify.data.repositories.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _loggedInUser = MutableStateFlow<User?>(null)
    val loggedInUser: StateFlow<User?> = _loggedInUser.asStateFlow()

    fun login(email: String, password: String) {
        _errorMessage.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = appRepository.login(email, password)
                result.onSuccess { user ->
                    _isLoggedIn.value = true
                    _errorMessage.value = null
                    _loggedInUser.value = user

                }.onFailure { exception ->
                    _isLoggedIn.value = false
                    _loggedInUser.value = null
                    _errorMessage.value = exception.message ?: "Unknown login error"
                }
            } catch (e: Exception) {
                _isLoggedIn.value = false
                _loggedInUser.value = null
                _errorMessage.value = e.message ?: "Unexpected error."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }
}