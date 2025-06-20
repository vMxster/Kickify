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

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _loggedInUser = MutableStateFlow<User?>(null)
    val loggedInUser: StateFlow<User?> = _loggedInUser.asStateFlow()

    private val _registerSuccessful = MutableStateFlow(false)
    val registeredSuccessful: StateFlow<Boolean> = _registerSuccessful.asStateFlow()

    fun login(email: String, password: String) {
        _message.value = null
        _isLoading.value = true
        _isLoggedIn.value = false

        viewModelScope.launch {
            try {
                val result = appRepository.login(email, password)
                result.onSuccess { user ->
                    _isLoggedIn.value = true
                    _message.value = null
                    _loggedInUser.value = user

                }.onFailure { exception ->
                    _isLoggedIn.value = false
                    _loggedInUser.value = null
                    _message.value = exception.message ?: "Unknown login error"
                }
            } catch (e: Exception) {
                _isLoggedIn.value = false
                _loggedInUser.value = null
                _message.value = e.message ?: "Unexpected error."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(
        email: String, firstName: String,
        lastName: String, password: String
    ) {
        _message.value = null
        _isLoading.value = true
        _isLoggedIn.value = false
        _registerSuccessful.value = false

        viewModelScope.launch {
            try {
                val result = appRepository.register(email, firstName, lastName, password)
                result.onSuccess {
                    _isLoggedIn.value = true
                    _message.value = "Registration successful"
                    _registerSuccessful.value = true

                }.onFailure { exception ->
                    _isLoggedIn.value = false
                    _loggedInUser.value = null
                    _registerSuccessful.value = false
                    _message.value = exception.message ?: "Unknown login error"
                }
            } catch (e: Exception) {
                _isLoggedIn.value = false
                _loggedInUser.value = null
                _registerSuccessful.value = false
                _message.value = e.message ?: "Unexpected error."
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun loginWithGoogle(idToken: String) {
        _message.value = null
        _isLoading.value = true
        _isLoggedIn.value = false

        viewModelScope.launch {
            try {

                // Decodificare il token JWT per estrarre le informazioni
                val tokenParts = idToken.split(".")
                if (tokenParts.size != 3) {
                    _message.value = "Formato del token non valido"
                    return@launch
                }

                // Decodifica della parte payload del token
                val payload = tokenParts[1].padEnd((tokenParts[1].length + 3) / 4 * 4, '=')
                val decodedBytes = android.util.Base64.decode(payload, android.util.Base64.URL_SAFE)
                val decodedPayload = String(decodedBytes)
                val jsonPayload = JSONObject(decodedPayload)

                // Estrai i dati dell'utente dal payload
                val email = jsonPayload.optString("email", "").lowercase()
                val name = jsonPayload.optString("given_name", "")
                val surname = jsonPayload.optString("family_name", "")

                if (email.isEmpty() || name.isEmpty() || surname.isEmpty()) {
                    _isLoading.value = false
                    _isLoggedIn.value = false
                    _message.value = "Dati insufficienti dal token Google"
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
                    _message.value = null
                    _loggedInUser.value = user
                }.onFailure { exception ->
                    Log.e("LoginViewModel", "Fallimento login con Google: ${exception.message}")
                    _isLoggedIn.value = false
                    _loggedInUser.value = null
                    _message.value = exception.message ?: "Unknown login error"
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Eccezione in loginWithGoogle: ${e.message}")
                _isLoggedIn.value = false
                _loggedInUser.value = null
                _message.value = e.message ?: "Errore imprevisto"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun dismissMessage() {
        _message.value = null
        _registerSuccessful.value = false
    }

    fun clearData(){
        _isLoading.value = false
        _isLoggedIn.value = false
        _message.value = null
        _loggedInUser.value = null
        _registerSuccessful.value = false
    }
}