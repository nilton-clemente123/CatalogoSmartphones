package com.tecsup.smartphones.ui.lista
import ItemRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.smartphones.domain.model.Item
import com.tecsup.smartphones.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ListaViewModel(
    private val repository: ItemRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)

    val uiState = _uiState.asStateFlow()

    init {
        cargarSmartphones()
    }

    fun cargarSmartphones() {
        viewModelScope.launch {

            _uiState.value = UiState.Cargando

            val resultado = repository.refrescar()

            resultado
                .onSuccess {
                    val smartphones = repository.items.first()

                    _uiState.value = UiState.Exito(smartphones)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(
                        error.message ?: "No se pudieron cargar los smartphones"
                    )
                }
        }
    }
}