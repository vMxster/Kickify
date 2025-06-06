package it.unibo.kickify.ui.screens.productList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.database.CompleteProduct
import it.unibo.kickify.data.database.Image
import it.unibo.kickify.data.database.Product
import it.unibo.kickify.data.repositories.AppRepository
import it.unibo.kickify.utils.DatabaseReadyManager
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    // Stati di caricamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            // Attendi che il database sia pronto
            databaseReadyManager.isDatabaseReady.collect { isReady ->
                if (isReady) {
                    loadProducts()
                    loadPopularProducts()
                    loadNewProducts()
                    loadDiscountedProducts()
                    this.cancel()
                }
            }
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.getProducts()
            if (result.isSuccess) {
                val mapValues = result.getOrNull() ?: emptyMap()
                val listValues = mapValues.map { (product, image) -> Pair(product, image) }
                _products.value = Result.success(listValues)
            } else {
                _products.value = Result.failure(result.exceptionOrNull() ?: Exception("Errore caricamento prodotti"))
            }
            _isLoading.value = false
        }
    }

    fun loadPopularProducts() {
        viewModelScope.launch {
            repository.getPopularProducts().also { result ->
                _popularProducts.value = result
            }
        }
    }

    fun loadNewProducts() {
        viewModelScope.launch {
            repository.getNewProducts().also { result ->
                _newProducts.value = result
            }
        }
    }

    fun loadDiscountedProducts() {
        viewModelScope.launch {
            repository.getDiscountedProducts().also { result ->
                _discountedProducts.value = result
            }
        }
    }

    fun loadProductDetails(productId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getProductWithVariants(productId).also { result ->
                _productDetails.value = result
            }
            _isLoading.value = false
        }
    }

    fun clearProductDetails() {
        _productDetails.value = null
    }
}