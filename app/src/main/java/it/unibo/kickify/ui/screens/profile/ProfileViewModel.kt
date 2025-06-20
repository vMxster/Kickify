package it.unibo.kickify.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.database.Address
import it.unibo.kickify.data.database.User
import it.unibo.kickify.data.models.PaymentMethodInfo
import it.unibo.kickify.data.repositories.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val appRepository: AppRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _addressList = MutableStateFlow<List<Address>>(listOf())
    val addressList: StateFlow<List<Address>> = _addressList.asStateFlow()

    private val _addressListModified = MutableStateFlow(false)
    val addressListModified: StateFlow<Boolean> = _addressListModified.asStateFlow()

    private val _passwordModified = MutableStateFlow(false)
    val passwordModified: StateFlow<Boolean> = _passwordModified.asStateFlow()

    private val _paymentMethods = MutableStateFlow<List<PaymentMethodInfo>>(emptyList())
    val paymentMethods: StateFlow<List<PaymentMethodInfo>> = _paymentMethods.asStateFlow()

    private val _paymentMethodsListModified = MutableStateFlow(false)
    val paymentMethodsListModified: StateFlow<Boolean> = _paymentMethodsListModified.asStateFlow()

    fun getProfile(email: String) {
        _errorMessage.value = null
        _isLoading.value = true
        _user.value = null

        viewModelScope.launch {
            try {
                val result = appRepository.getUserProfile(email)
                result.onSuccess { u ->
                    _user.value = u
                    _errorMessage.value = null

                }.onFailure { exception ->
                    _user.value = null
                    _errorMessage.value = exception.message ?: "Unknown error"
                }
            } catch (e: Exception) {
                _user.value = null
                _errorMessage.value = e.message ?: "Unexpected error."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUserAddress(email: String){
        _errorMessage.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = appRepository.getUserAddress(email)
                result.onSuccess { list ->
                    _addressList.value = list.toList()
                    _errorMessage.value = null

                }.onFailure { exception ->
                    _addressList.value = emptyList()
                    _errorMessage.value = exception.message ?: "Unknown error"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unexpected error."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addUserAddress(
        email: String, street: String, number: String, cap: String,
        city: String, province: String, nation: String, default: Boolean
    ){
        _errorMessage.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = appRepository.addUserAddress(email, street, number, cap, city, province, nation, default)
                result.onSuccess {
                    _errorMessage.value = null
                    _addressListModified.value = true
                    getUserAddress(email) // update address list

                }.onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Unknown error"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unexpected error."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteUserAddress(email: String, street: String, number: String, cap: String, city: String){
        _errorMessage.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = appRepository.deleteUserAddress(email, street, number, cap, city)
                result.onSuccess {
                    _errorMessage.value = null
                    _addressListModified.value = true
                    getUserAddress(email) // update address list

                }.onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Unknown error"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unexpected error."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changePassword(email: String, password: String){
        _errorMessage.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = appRepository.changePassword(email, password)
                result.onSuccess {
                    _errorMessage.value = null
                    _passwordModified.value = true

                }.onFailure { exception ->
                    _passwordModified.value = true
                    _errorMessage.value = exception.message ?: "Unknown error"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unexpected error."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPaymentMethods(email: String){
        _errorMessage.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = appRepository.getUserPaymentMethod(email)
                result.onSuccess { list ->
                    _paymentMethods.value = list.toMutableList()
                    _errorMessage.value = null

                }.onFailure { exception ->
                    _paymentMethods.value = emptyList()
                    _errorMessage.value = exception.message ?: "Unknown error"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unexpected error."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addPaymentMethod(userEmail: String, paymentMethodInfo: PaymentMethodInfo){
        _errorMessage.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                if(paymentMethodInfo is PaymentMethodInfo.PayPal) {
                    val result = appRepository.addUserPaymentMethod(
                        userEmail = userEmail,
                        paypalEmail = paymentMethodInfo.email,
                        creditCardBrand = "",
                        creditCardLast4 = "",
                        creditCardExpMonth = 0,
                        creditCardExpYear = 0
                    )
                    result.onSuccess {
                        _errorMessage.value = null
                        getPaymentMethods(userEmail)
                    }.onFailure { exception ->
                        _errorMessage.value = exception.message ?: "Unknown error"
                    }
                } else if(paymentMethodInfo is PaymentMethodInfo.CreditCard){
                    val result = appRepository.addUserPaymentMethod(
                        userEmail = userEmail,
                        paypalEmail = "",
                        creditCardBrand = paymentMethodInfo.brand,
                        creditCardLast4 = paymentMethodInfo.last4,
                        creditCardExpMonth = paymentMethodInfo.expirationMonth,
                        creditCardExpYear = paymentMethodInfo.expirationYear
                    )
                    result.onSuccess {
                        _errorMessage.value = null
                        _paymentMethodsListModified.value = true
                        getPaymentMethods(userEmail)
                    }.onFailure { exception ->
                        _errorMessage.value = exception.message ?: "Unknown error"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unexpected error."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePaymentMethod(userEmail: String, id: Int){
        _errorMessage.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = appRepository.deleteUserPaymentMethod(id)
                result.onSuccess {
                    _errorMessage.value = null
                    _paymentMethodsListModified.value = true
                    getPaymentMethods(userEmail)
                }.onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Unknown error"
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unexpected error."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetModifiedAddress(){
        _addressListModified.value = false
    }

    fun resetModifiedPayment(){
        _paymentMethodsListModified.value = false
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    fun resetChangedPassword(){
        _passwordModified.value = false
    }
}