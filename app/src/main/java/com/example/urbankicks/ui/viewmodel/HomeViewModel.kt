package com.example.urbankicks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbankicks.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val brandList: List<String> = emptyList(),
    val brandLogos: List<Int> = emptyList(),
    val errorMessage: String? = null
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            val brands = listOf("Nike", "Fila", "Puma", "Adidas", "DC")
            val logos = listOf(
                R.drawable.nike, R.drawable.fila, R.drawable.puma,
                R.drawable.adidas, R.drawable.on, R.drawable.asics,
                R.drawable.crocs, R.drawable.new_balance, R.drawable.vans,
                R.drawable.converse, R.drawable.reebok, R.drawable.under_armour,
                R.drawable.lacoste, R.drawable.diadora, R.drawable.kappa,
                R.drawable.hoka, R.drawable.havaianas, R.drawable.salomon,
                R.drawable.saucony, R.drawable.sergio_tacchini, R.drawable.ugg,
                R.drawable.vans
            )

            _uiState.value = HomeUiState(
                isLoading = false,
                brandList = brands,
                brandLogos = logos
            )
        }
    }

    // Eventuali metodi per aggiornare lo stato
    fun onError(message: String) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }
}
