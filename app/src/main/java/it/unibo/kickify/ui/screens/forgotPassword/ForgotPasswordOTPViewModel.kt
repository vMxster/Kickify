package it.unibo.kickify.ui.screens.forgotPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.repositories.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordOTPViewModel(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private var userEmail: String = ""

    fun isValidEmail(email: String){
        _isLoading.value = true
        _errorMessage.value = null
        _successMessage.value = null

        viewModelScope.launch {
            val result = appRepository.isUserRegistered(email)
            result.onSuccess { res ->
                if(res) {
                    _successMessage.value = "Email valida."
                    userEmail = email
                } else {
                    _errorMessage.value = "Non esistono account registrati con questa email. Riprova."
                }
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Errore durante verifica di email. OTP non inviato"
            }
        }
        _isLoading.value = false
    }

    fun sendOtp() {
        _isLoading.value = true
        _errorMessage.value = null
        _successMessage.value = null

        viewModelScope.launch {
            val result = appRepository.sendMailWithOTP(userEmail)
            result.onSuccess {
                _successMessage.value = "OTP inviato con successo alla tua email."
            }.onFailure{ exception ->
                _errorMessage.value = exception.message ?: "Errore durante l'invio dell'OTP."
            }
            println("otp success: ${_successMessage.value} - error: ${_errorMessage.value}")
        }
        _isLoading.value = false
    }

    fun verifyOtp(otp: String) {
        _isLoading.value = true
        _errorMessage.value = null
        _successMessage.value = null

        viewModelScope.launch {
            val result = appRepository.verifyOTP(userEmail, otp)
            result.onSuccess{
                _successMessage.value = "OTP verificato con successo."
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: "OTP non valido o scaduto."
            }
        }
        _isLoading.value = false
    }

    fun resetPassword(newPassword: String) {
        _isLoading.value = true
        _errorMessage.value = null
        _successMessage.value = null

        viewModelScope.launch {
            val result = appRepository.changePassword(userEmail, newPassword)
            result.onSuccess{
                _successMessage.value = "Password cambiata con successo. Effettua il login."
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Errore durante il cambio password."
            }
        }
        _isLoading.value = false
    }
}