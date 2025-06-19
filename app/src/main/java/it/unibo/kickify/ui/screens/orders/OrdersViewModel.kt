package it.unibo.kickify.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.database.Order
import it.unibo.kickify.data.database.OrderDetailedTracking
import it.unibo.kickify.data.database.OrderProductDetails
import it.unibo.kickify.data.repositories.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _ordersWithProducts = MutableStateFlow<List<OrderProductDetails>>(listOf())
    val ordersWithProducts: StateFlow<List<OrderProductDetails>> = _ordersWithProducts.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(listOf())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _orderTracking = MutableStateFlow<OrderDetailedTracking?>(null)
    val orderTracking: StateFlow<OrderDetailedTracking?> = _orderTracking.asStateFlow()

    fun getOrdersWithProducts(email: String) {
        _errorMessage.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = appRepository.getOrdersWithProducts(email)
                result.onSuccess { list ->
                    _errorMessage.value = null
                    _ordersWithProducts.value = list

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

    fun getOrders(email: String) {
        _errorMessage.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = appRepository.getOrders(email)
                result.onSuccess { list ->
                    _errorMessage.value = null
                    _orders.value = list

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

    fun getOrderTracking(orderId: Int){
        _errorMessage.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = appRepository.getOrderTracking(orderId)
                result.onSuccess { tracking ->
                    _errorMessage.value = null
                    _orderTracking.value = tracking

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

    fun dismissError() {
        _errorMessage.value = null
    }
}