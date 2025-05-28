package it.unibo.kickify.ui.screens.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.database.ProductDetails
import it.unibo.kickify.data.repositories.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _wishlistState = MutableStateFlow<Result<List<ProductDetails>>>(Result.success(emptyList()))
    val wishlistState : StateFlow<Result<List<ProductDetails>>> = _wishlistState.asStateFlow()

    fun fetchWishlist(email: String) {
        viewModelScope.launch {
            val result = repository.getWishlistItems(email)
            if(result.isSuccess){
                val prodIds = result.getOrNull() ?: emptyList()
                val detailedProducts = prodIds.mapNotNull { wishlistItem ->
                    repository.getProductData(wishlistItem.productId, email).getOrNull()
                }
                _wishlistState.value = Result.success(detailedProducts)
            } else {
                _wishlistState.value = Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
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