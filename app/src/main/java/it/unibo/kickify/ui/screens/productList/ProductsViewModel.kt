package it.unibo.kickify.ui.screens.productList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.database.Image
import it.unibo.kickify.data.database.Product
import it.unibo.kickify.data.repositories.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductsViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _products = MutableStateFlow<Result<List<Pair<Product, Image>>>>(Result.success(emptyList()))
    val products: StateFlow<Result<List<Pair<Product, Image>>>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadProducts()
        println("products viewmodel:" + products.value)
    }

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true

            /* TODO get last access from repository */
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val lastAccess = dateFormat.format(Date())

            val result = repository.getProducts(lastAccess)
            if(result.isSuccess){
                val mapValues = result.getOrNull() ?: emptyMap()
                println("map values:$mapValues")
                val listValues =
                    mapValues.map { (product, image) -> Pair(product, image) }
                println("list values. $listValues")
                _products.value = Result.success(listValues)
            }
            _isLoading.value = false
        }
    }
}