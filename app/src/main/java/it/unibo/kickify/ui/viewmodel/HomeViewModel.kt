package it.unibo.kickify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            //val logos = listOf()

            _uiState.value = HomeUiState(
                isLoading = false,
                brandList = brands,
                //brandLogos = logos
            )
        }
    }

    // Eventuali metodi per aggiornare lo stato
    fun onError(message: String) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }
}
