package it.unibo.kickify.ui.screens.products

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.database.CompleteProduct
import it.unibo.kickify.data.database.HistoryProduct
import it.unibo.kickify.data.database.Image
import it.unibo.kickify.data.database.Product
import it.unibo.kickify.data.database.ProductDetails
import it.unibo.kickify.data.database.ProductWithImage
import it.unibo.kickify.data.database.ReviewWithUserInfo
import it.unibo.kickify.data.database.Version
import it.unibo.kickify.data.repositories.AppRepository
import it.unibo.kickify.utils.DatabaseReadyManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val repository: AppRepository,
    private val databaseReadyManager: DatabaseReadyManager
) : ViewModel() {

    // Prodotti generici
    private val _products = MutableStateFlow<Result<Map<Product, Image>>>(Result.success(emptyMap()))
    val products: StateFlow<Result<Map<Product, Image>>> = _products

    // Prodotti Popolari
    private val _popularProducts = MutableStateFlow<Result<List<Product>>>(Result.success(emptyList()))
    val popularProducts: StateFlow<Result<List<Product>>> = _popularProducts

    // Prodotti Nuovi
    private val _newProducts = MutableStateFlow<Result<List<Product>>>(Result.success(emptyList()))
    val newProducts: StateFlow<Result<List<Product>>> = _newProducts

    // Prodotti Scontati
    private val _discountedProducts = MutableStateFlow<Result<List<Product>>>(Result.success(emptyList()))
    val discountedProducts: StateFlow<Result<List<Product>>> = _discountedProducts

    // Dettagli prodotto
    private val _productDetails = MutableStateFlow<Result<CompleteProduct>?>(null)
    val productDetails: StateFlow<Result<CompleteProduct>?> = _productDetails

    // Varianti prodotto
    private val _productVariants = MutableStateFlow<Result<Map<Int, List<Version>>>>(Result.success(emptyMap()))
    val productVariants: StateFlow<Result<Map<Int, List<Version>>>> = _productVariants

    // Cronologia prodotti
    private val _productHistory = MutableStateFlow<Result<List<HistoryProduct>>>(Result.success(emptyList()))
    val productHistory: StateFlow<Result<List<HistoryProduct>>> = _productHistory

    private val _productDataAndReviews = MutableStateFlow<Result<ProductDetails>?>(null)
    val productDataAndReviews: StateFlow<Result<ProductDetails>?> =_productDataAndReviews

    private val _productReviews = MutableStateFlow<Result<List<ReviewWithUserInfo>>?>(null)
    val productReviews: StateFlow<Result<List<ReviewWithUserInfo>>?> = _productReviews

    private val _productImages = MutableStateFlow<List<Image>>(listOf())
    val productImages: StateFlow<List<Image>> = _productImages

    // Stati di caricamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchResults = MutableStateFlow<Result<List<ProductWithImage>>>(Result.success(emptyList()))
    val searchResults: StateFlow<Result<List<ProductWithImage>>> = _searchResults

    // Stato per il Prodotto in Wishlist
    private val _isInWishlist = MutableStateFlow(false)
    val isInWishlist: StateFlow<Boolean> = _isInWishlist

    init {
        viewModelScope.launch {
            databaseReadyManager.isDatabaseReady.collectLatest { isReady ->
                if (isReady) {
                    loadAllInitialData()
                }
            }
        }
    }

    private fun loadProductsHistory() {
        viewModelScope.launch {
            try {
                repository.getProductsHistory().also { result ->
                    if (result.isSuccess) {
                        _productHistory.value = Result.success(result.getOrNull() ?: emptyList())
                    } else {
                        _productHistory.value = Result.failure(result.exceptionOrNull() ?: Exception("Errore caricamento cronologia prodotti"))
                    }
                }
            } catch (e: Exception) {
                _productHistory.value = Result.failure(e)
            }
        }
    }

    private fun loadAllInitialData() {
        _isLoading.value = true
        viewModelScope.launch {
            launch { loadProducts() }.join()

            val jobs = mutableListOf<Job>()
            jobs.add(launch { loadVersions() })
            jobs.add(launch { loadProductsHistory() })
            jobs.add(launch { loadPopularProducts() })
            jobs.add(launch { loadNewProducts() })
            jobs.add(launch { loadDiscountedProducts() })
            jobs.forEach { it.join() }

            _isLoading.value = false
        }
    }

    private suspend fun loadProducts() {
        val result = repository.getProducts()
        if (result.isSuccess) {
            val mapValues = result.getOrNull() ?: emptyMap()
            _products.value = Result.success(mapValues)
        } else {
            _products.value = Result.failure(result.exceptionOrNull() ?: Exception("Errore caricamento prodotti"))
        }
    }

    private suspend fun loadVersions() {
        val result = repository.getVersions()
        if (result.isSuccess) {
            val mapValues = result.getOrNull() ?: emptyMap()
            _productVariants.value = Result.success(mapValues)
        } else {
            _productVariants.value = Result.failure(result.exceptionOrNull() ?: Exception("Errore caricamento varianti"))
        }
    }

    private fun loadPopularProducts() {
        viewModelScope.launch {
            repository.getPopularProducts().also { result ->
                _popularProducts.value = result
            }
        }
    }

    private fun loadNewProducts() {
        viewModelScope.launch {
            repository.getNewProducts().also { result ->
                _newProducts.value = result
            }
        }
    }

    private fun loadDiscountedProducts() {
        viewModelScope.launch {
            repository.getDiscountedProducts().also { result ->
                _discountedProducts.value = result
            }
        }
    }

    fun loadProductDetails(productId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getProductWithVariants(productId).also { result ->
                _productDetails.value = result
            }
        }
        _isLoading.value = false
    }

    fun clearProductDetails() {
        _productDetails.value = null
    }

    fun getProductImages(productId: Int){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _productImages.value = repository.getProductImages(productId)
            } catch (e: Exception) {
                _productImages.value = listOf()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getProductData(productId: Int, userEmail: String){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getProductData(productId, userEmail)
                _productDataAndReviews.value = result
            } catch (e: Exception) {
                _productDataAndReviews.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getReviewsOfProduct(productId: Int){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getReviews(productId)
                _productReviews.value = result
            } catch (e: Exception) {
                _productReviews.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Ricerca per Stringa
    fun searchProducts(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.searchProducts(query)
                _searchResults.value = result
            } catch (e: Exception) {
                _searchResults.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun isInWishlist(productId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.isInWishlist(productId)
                _isInWishlist.value = if (result.isSuccess) {
                    result.getOrNull() ?: false
                } else {
                    false
                }
            } catch (e: Exception) {
                _isInWishlist.value = false
            }
        }
    }

    fun toggleWishlist(productId: Int) {
        viewModelScope.launch {
            try {
                if (_isInWishlist.value) {
                    repository.removeFromWishlist(productId)
                } else {
                    repository.addToWishlist(productId)
                }
                _isInWishlist.value = !_isInWishlist.value
            } catch (e: Exception) {
                Log.e("ProductsViewModel", "Errore durante la modifica della wishlist: ${e.message}")
            }
        }
    }
}