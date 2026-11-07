package com.tecsup.smartphones.ui.detalle
import ItemRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.smartphones.domain.model.Item
import com.tecsup.smartphones.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleViewModel(
    private val repository: ItemRepository,
    private val smartphoneId: Int
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<Item>>(UiState.Cargando)

    val uiState = _uiState.asStateFlow()

    init {
        cargarDetalle()
    }

    fun cargarDetalle() {
        viewModelScope.launch {

            _uiState.value = UiState.Cargando

            val resultado = repository.obtenerDetalle(smartphoneId)

            resultado
                .onSuccess { smartphone ->
                    _uiState.value = UiState.Exito(smartphone)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(
                        error.message ?: "No se pudo cargar el smartphone"
                    )
                }
        }
    }
}