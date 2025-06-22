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
import it.unibo.kickify.data.models.ShopCategory
import it.unibo.kickify.data.repositories.AppRepository
import it.unibo.kickify.utils.DatabaseReadyManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchResults = MutableStateFlow<Result<List<ProductWithImage>>>(Result.success(emptyList()))
    val searchResults: StateFlow<Result<List<ProductWithImage>>> = _searchResults

    // Stato per il Prodotto in Wishlist
    private val _isInWishlist = MutableStateFlow(false)
    val isInWishlist: StateFlow<Boolean> = _isInWishlist

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _filteredProducts = MutableStateFlow<Result<Map<Product, Image>>>(Result.success(emptyMap()))
    val filteredProducts: StateFlow<Result<Map<Product, Image>>> = _filteredProducts

    private val _canUserReview = MutableStateFlow(false)
    val canUserReview: StateFlow<Boolean> = _canUserReview.asStateFlow()

    init {
        viewModelScope.launch {
            databaseReadyManager.isDatabaseReady.collectLatest { isReady ->
                if (isReady) {
                    loadAllInitialData()
                }
            }
        }
    }

    // Metodo per aggiornare i filtri
    fun updateFilters(newFilterState: FilterState) {
        _filterState.value = newFilterState
        applyFilters()
    }

    // Metodo per resettare i filtri
    fun resetFilters() {
        _filterState.value = FilterState()
        _filteredProducts.value = _products.value
    }

    // Applica i filtri alla lista di prodotti
    private fun applyFilters() {
        viewModelScope.launch {
            val filter = _filterState.value
            _products.value.onSuccess { productMap ->
                val filteredMap = productMap.filter { (product, _) ->
                    val priceInRange = product.price >= filter.priceRange.start &&
                            product.price <= filter.priceRange.endInclusive

                    val brandMatch = filter.selectedBrands.isEmpty() ||
                            filter.selectedBrands.contains(product.brand)

                    val genderMatch = filter.selectedGender == null ||
                            product.genre == filter.selectedGender.toString()

                    val nameMatch = filter.searchQuery == "" ||
                            filter.searchQuery.split(" ").any{ w ->
                                product.brand.contains(w, ignoreCase = true) ||
                                        product.name.contains(w, ignoreCase = true)
                            }

                    priceInRange && brandMatch && genderMatch && nameMatch
                }

                // Ordinamento
                val sortedMap = when(filter.orderBy) {
                    OrderBy.PRICE_LOW_HIGH -> filteredMap.toList()
                        .sortedBy { it.first.price }
                        .toMap()
                    OrderBy.PRICE_HIGH_LOW -> filteredMap.toList()
                        .sortedByDescending { it.first.price }
                        .toMap()
                    OrderBy.ALPHABETICAL -> filteredMap.toList()
                        .sortedBy { "${it.first.brand} ${it.first.name}" }
                        .toMap()
                    else -> filteredMap
                }
                _filteredProducts.value = Result.success(sortedMap)
            }.onFailure {
                _filteredProducts.value = _products.value
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

    fun canUserReview(email: String, productId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.canUserReview(email, productId)
                result.onSuccess { res ->
                    _canUserReview.value = res
                }.onFailure {
                    _canUserReview.value = false
                }

            } catch (e: Exception) {
                _canUserReview.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addReviewOfProduct(email: String, productId: Int, rating: Double, comment: String){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.addReview(email, productId, rating, comment)
                getReviewsOfProduct(productId)
            } catch (e: Exception){
                _errorMessage.value = "Error adding review. Retry later."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteReviewOfProduct(email: String, productId: Int){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.deleteReview(email, productId)
                getReviewsOfProduct(productId)
            } catch (e: Exception){
                _errorMessage.value = "Error deleting the review. Retry later."
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

data class FilterState(
    val priceRange: ClosedFloatingPointRange<Float> = 40f..500f,
    val selectedBrands: Set<String> = emptySet(),
    val selectedColors: Set<String> = emptySet(),
    val selectedSizes: Set<Int> = emptySet(),
    val selectedGender: ShopCategory? = null,
    val orderBy: OrderBy = OrderBy.NONE,
    val searchQuery: String = ""
)

enum class OrderBy {
    NONE,
    PRICE_LOW_HIGH,
    PRICE_HIGH_LOW,
    ALPHABETICAL
}