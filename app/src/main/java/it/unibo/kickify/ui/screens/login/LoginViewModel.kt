package it.unibo.kickify.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.database.User
import it.unibo.kickify.data.repositories.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

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
        _isLoggedIn.value = false

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

    fun loginWithGoogle(idToken: String) {
        _errorMessage.value = null
        _isLoading.value = true
        _isLoggedIn.value = false

        viewModelScope.launch {
            try {

                // Decodificare il token JWT per estrarre le informazioni
                val tokenParts = idToken.split(".")
                if (tokenParts.size != 3) {
                    _errorMessage.value = "Formato del token non valido"
                    return@launch
                }

                // Decodifica della parte payload del token
                val payload = tokenParts[1].padEnd((tokenParts[1].length + 3) / 4 * 4, '=')
                val decodedBytes = android.util.Base64.decode(payload, android.util.Base64.URL_SAFE)
                val decodedPayload = String(decodedBytes)
                val jsonPayload = JSONObject(decodedPayload)

                // Estrai i dati dell'utente dal payload
                val email = jsonPayload.optString("email", "")
                val name = jsonPayload.optString("given_name", "")
                val surname = jsonPayload.optString("family_name", "")

                if (email.isEmpty() || name.isEmpty() || surname.isEmpty()) {
                    _isLoading.value = false
                    _isLoggedIn.value = false
                    _errorMessage.value = "Dati insufficienti dal token Google"
                    return@launch
                }

                val shortToken = if (idToken.length > 100) {
                    idToken.takeLast(100)
                } else {
                    idToken
                }

                // Procedi con l'autenticazione tramite appRepository
                val result = appRepository.loginWithGoogle(email, name, surname, shortToken)
                result.onSuccess { user ->
                    _isLoggedIn.value = true
                    Log.d("LoginViewModel", "Login con Google riuscito, isLoggedIn: ${_isLoggedIn.value}")
                    _errorMessage.value = null
                    _loggedInUser.value = user
                }.onFailure { exception ->
                    Log.e("LoginViewModel", "Fallimento login con Google: ${exception.message}")
                    _isLoggedIn.value = false
                    _loggedInUser.value = null
                    _errorMessage.value = exception.message ?: "Unknown login error"
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Eccezione in loginWithGoogle: ${e.message}")
                _isLoggedIn.value = false
                _loggedInUser.value = null
                _errorMessage.value = e.message ?: "Errore imprevisto"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    fun clearData(){
        _isLoading.value = false
        _isLoggedIn.value = false
        _errorMessage.value = null
        _loggedInUser.value = null
    }
}