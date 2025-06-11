package it.unibo.kickify.ui.screens.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.database.WishlistProduct
import it.unibo.kickify.data.repositories.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _wishlistState = MutableStateFlow<List<WishlistProduct>>(emptyList())
    val wishlistState : StateFlow<List<WishlistProduct>> = _wishlistState.asStateFlow()

    fun fetchWishlist(email: String) {
        _errorMessage.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try{
                val result = repository.getWishlistItems(email)
                result.onSuccess { list ->
                    _wishlistState.value = list
                    _errorMessage.value = null

                }.onFailure { exception ->
                    _wishlistState.value = emptyList()
                    _errorMessage.value = exception.message
                }

            } catch (e: Exception) {
                _wishlistState.value = emptyList()
                _errorMessage.value = e.message ?: "Unexpected error."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToWishlist(email: String, prodId: Int){
        viewModelScope.launch {
            val result = repository.addToWishlist(email, prodId)
            if (result.isSuccess) {
                fetchWishlist(email)
            }
        }
    }

    fun removeFromWishlist(email: String, prodId: Int) {
        viewModelScope.launch {
            val result = repository.removeFromWishlist(email, prodId)
            if (result.isSuccess) {
                fetchWishlist(email)
            }
        }
    }
}