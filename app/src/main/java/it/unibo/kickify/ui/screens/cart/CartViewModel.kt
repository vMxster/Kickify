package it.unibo.kickify.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.database.Address
import it.unibo.kickify.data.database.CartWithProductInfo
import it.unibo.kickify.data.models.PaymentMethodInfo
import it.unibo.kickify.data.repositories.AppRepository
import it.unibo.kickify.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _checkoutCompleted = MutableStateFlow(false)
    val checkOutCompleted: StateFlow<Boolean> = _checkoutCompleted.asStateFlow()

    init {
        viewModelScope.launch {
            _isLoading.value = true
            settingsRepository.userID.collectLatest { userid ->
                _email.value = userid.lowercase()

                if(userid.isNotEmpty()){
                    loadCartInternal(userid.lowercase())
                } else {
                    _cartItems.value = emptyList()
                    _subTotal.value = 0.0
                    _total.value = 0.0
                }
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadCartInternal(email: String) {
        _errorMessage.value = null
        _isLoading.value = true

        try {
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
                _errorMessage.value = "Email utente non disponibile per caricare il carrello."
            }
        } catch (e: Exception) {
            _errorMessage.value = "Errore durante il caricamento del carrello: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun loadCart() {
        viewModelScope.launch {
            if (_email.value.isNotEmpty()) {
                loadCartInternal(_email.value)
            } else {
                _errorMessage.value = "Impossibile ricaricare il carrello: utente non autenticato."
            }
        }
    }

    fun removeFromCart(productId: Int, color: String, size: Double) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                if (_email.value.isNotEmpty()) {
                    val result = appRepository.removeFromCart(_email.value, productId, color, size)
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
                if (_email.value.isNotEmpty()) {
                    // Converti la stringa del colore in un formato accettabile
                    val processedColor = if (color.startsWith("Color(")) {
                        // Estrai i valori RGBA dalla stringa "Color(r, g, b, a, ...)"
                        val components = color.substringAfter("Color(").substringBefore(")").split(",")
                        val r = components.getOrNull(0)?.trim()?.toFloatOrNull() ?: 0f
                        val g = components.getOrNull(1)?.trim()?.toFloatOrNull() ?: 0f
                        val b = components.getOrNull(2)?.trim()?.toFloatOrNull() ?: 0f

                        // Mappa per colori
                        when {
                            r > 0.8f && g < 0.3f && b < 0.3f -> "Red"
                            r < 0.3f && g > 0.8f && b < 0.3f -> "Green"
                            r < 0.3f && g < 0.3f && b > 0.8f -> "Blue"
                            r > 0.8f && g > 0.8f && b < 0.3f -> "Yellow"
                            r > 0.8f && g > 0.8f && b > 0.8f -> "White"
                            r < 0.3f && g < 0.3f && b < 0.3f -> "Black"
                            else -> color
                        }
                    } else {
                        color
                    }

                    // Prima rimuove l'item, poi lo aggiunge con nuova quantità
                    val removeResult = appRepository.removeFromCart(_email.value, productId, processedColor, size)
                    if (removeResult.isSuccess) {
                        val addResult = appRepository.addToCart(_email.value, productId, processedColor, size, newQuantity)
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

    fun placeOrder(address: Address, paymentMethod: PaymentMethodInfo) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = appRepository.placeOrder(
                    street = address.street,
                    city = address.city,
                    civic = address.civic,
                    cap = address.cap,
                    email = _email.value,
                    total = _total.value,
                    paymentMethod = paymentMethod.getType(),
                    shippingType = "Express"
                )
                if (result.isSuccess) {
                    loadCartInternal(_email.value)
                    _checkoutCompleted.value = true
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Errore durante la creazione dell'ordine: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun dismissCompletedCheckout(){
        _checkoutCompleted.value = false
    }

    private fun calculateTotals() {
        var sum = 0.0
        _cartItems.value.forEach { cartItem ->
            sum += cartItem.prezzo * cartItem.cartProduct.quantity
        }
        _subTotal.value = sum
        _total.value = sum + _shippingCost.value
    }

    fun dismissError() {
        _errorMessage.value = null
    }
}