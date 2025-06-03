package it.unibo.kickify.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.database.CartWithProductInfo
import it.unibo.kickify.data.repositories.AppRepository
import it.unibo.kickify.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CartViewModel(
    private val appRepository: AppRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartWithProductInfo>>(emptyList())
    val cartItems: StateFlow<List<CartWithProductInfo>> = _cartItems.asStateFlow()

    private val _subTotal = MutableStateFlow(0.0)
    val subTotal: StateFlow<Double> = _subTotal.asStateFlow()

    private val _shippingCost = MutableStateFlow(10.0)
    val shippingCost: StateFlow<Double> = _shippingCost.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    fun loadCart() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val email = settingsRepository.userID.first()
                if (email.isNotEmpty()) {
                    val cartResult = appRepository.getCart(email)
                    if (cartResult.isSuccess) {
                        val cartItemsResult = appRepository.getCartItems(email)
                        if (cartItemsResult.isSuccess) {
                            val items = cartItemsResult.getOrNull() ?: emptyList()
                            _cartItems.value = items
                            calculateTotals()
                        } else {
                            _errorMessage.value = cartItemsResult.exceptionOrNull()?.message
                        }
                    } else {
                        _errorMessage.value = cartResult.exceptionOrNull()?.message
                    }
                } else {
                    _errorMessage.value = "Utente non autenticato"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromCart(productId: Int, color: String, size: Double) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val email = settingsRepository.userID.first()
                if (email.isNotEmpty()) {
                    val result = appRepository.removeFromCart(email, productId, color, size)
                    if (result.isSuccess) {
                        loadCart()
                    } else {
                        _errorMessage.value = result.exceptionOrNull()?.message
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateQuantity(productId: Int, color: String, size: Double, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(productId, color, size)
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val email = settingsRepository.userID.first()
                if (email.isNotEmpty()) {
                    // Prima rimuove l'item, poi lo aggiunge con nuova quantità
                    val removeResult = appRepository.removeFromCart(email, productId, color, size)
                    if (removeResult.isSuccess) {
                        val addResult = appRepository.addToCart(email, productId, color, size, newQuantity)
                        if (addResult.isSuccess) {
                            loadCart()
                        } else {
                            _errorMessage.value = addResult.exceptionOrNull()?.message
                        }
                    } else {
                        _errorMessage.value = removeResult.exceptionOrNull()?.message
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calculateTotals() {
        val subtotal = _cartItems.value.sumOf { cartItem ->
            cartItem.prezzo.times(cartItem.cartProduct.quantity)
        }
        _subTotal.value = subtotal
        _total.value = subtotal + _shippingCost.value
    }

    fun dismissError() {
        _errorMessage.value = null
    }
}