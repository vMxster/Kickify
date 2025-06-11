package it.unibo.kickify.ui.screens.productList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.database.CompleteProduct
import it.unibo.kickify.data.database.Image
import it.unibo.kickify.data.database.Product
import it.unibo.kickify.data.database.ProductDetails
import it.unibo.kickify.data.database.ProductWithImage
import it.unibo.kickify.data.database.ReviewWithUserInfo
import it.unibo.kickify.data.database.Version
import it.unibo.kickify.data.repositories.AppRepository
import it.unibo.kickify.utils.DatabaseReadyManager
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val repository: AppRepository,
    private val databaseReadyManager: DatabaseReadyManager
) : ViewModel() {

    // Prodotti generici per ProductListScreen
    private val _products = MutableStateFlow<Result<List<Pair<Product, Image>>>>(Result.success(emptyList()))
    val products: StateFlow<Result<List<Pair<Product, Image>>>> = _products

    // Prodotti specifici per HomeScreen
    private val _popularProducts = MutableStateFlow<Result<List<Product>>>(Result.success(emptyList()))
    val popularProducts: StateFlow<Result<List<Product>>> = _popularProducts

    private val _newProducts = MutableStateFlow<Result<List<Product>>>(Result.success(emptyList()))
    val newProducts: StateFlow<Result<List<Product>>> = _newProducts

    private val _discountedProducts = MutableStateFlow<Result<List<Product>>>(Result.success(emptyList()))
    val discountedProducts: StateFlow<Result<List<Product>>> = _discountedProducts

    // Dettagli prodotto per ProductDetailsScreen
    private val _productDetails = MutableStateFlow<Result<CompleteProduct>?>(null)
    val productDetails: StateFlow<Result<CompleteProduct>?> = _productDetails

    private val _productVariants = MutableStateFlow<Result<Map<Int, List<Version>>>>(Result.success(emptyMap()))
    val productVariants: StateFlow<Result<Map<Int, List<Version>>>> = _productVariants

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

    init {
        viewModelScope.launch {
            databaseReadyManager.isDatabaseReady.collect { isReady ->
                if (isReady) {
                    loadProducts()
                    launch { loadVersions() }
                    launch { loadPopularProducts() }
                    launch { loadNewProducts() }
                    launch { loadDiscountedProducts() }
                    this.cancel()
                }
            }
        }
    }

    private fun loadProducts() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getProducts()
            if (result.isSuccess) {
                val mapValues = result.getOrNull() ?: emptyMap()
                val listValues = mapValues.map { (product, image) -> Pair(product, image) }
                _products.value = Result.success(listValues)
            } else {
                _products.value = Result.failure(result.exceptionOrNull() ?: Exception("Errore caricamento prodotti"))
            }
        }
        _isLoading.value = false
    }

    private fun loadVersions() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getVersions()
            if (result.isSuccess) {
                val mapValues = result.getOrNull() ?: emptyMap()
                _productVariants.value = Result.success(mapValues)
            } else {
                _products.value = Result.failure(result.exceptionOrNull() ?: Exception("Errore caricamento prodotti"))
            }
        }
        _isLoading.value = false
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
}