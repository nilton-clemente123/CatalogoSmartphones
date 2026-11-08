package com.tecsup.smartphones.ui.lista

import ItemRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.smartphones.domain.model.Item
import com.tecsup.smartphones.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException

class ListaViewModel(
    private val repository: ItemRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)

    val uiState = _uiState.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)

    val aviso = _aviso.asStateFlow()

    private var refrescoTerminado = false

    init {
        // Observamos continuamente los datos de Room.
        viewModelScope.launch {
            repository.items.collect { smartphones ->

                if (smartphones.isNotEmpty() || refrescoTerminado) {
                    _uiState.value = UiState.Exito(smartphones)
                }
            }
        }

        refrescar()
    }

    fun refrescar() {
        viewModelScope.launch {

            refrescoTerminado = false
            _aviso.value = null

            // Si no hay datos locales, mostramos Cargando.
            if (repository.cantidadGuardada() == 0) {
                _uiState.value = UiState.Cargando
            }

            // Intentamos actualizar los datos desde DummyJSON.
            val resultado = repository.refrescar()

            refrescoTerminado = true

            // Comprobamos si existen datos en Room.
            val cantidadGuardada = repository.cantidadGuardada()

            resultado
                .onSuccess {

                    _aviso.value = null

                    // Si la API devolvió una lista vacía,
                    // mostramos el estado vacío.
                    if (cantidadGuardada == 0) {
                        _uiState.value = UiState.Exito(emptyList())
                    }
                }
                .onFailure { error ->

                    if (cantidadGuardada == 0) {

                        // No tenemos datos locales.
                        _uiState.value = UiState.Error(
                            mensajeDe(error)
                        )

                    } else {

                        // Tenemos datos locales, pero falló la conexión.
                        // Conservamos la lista de Room.
                        _aviso.value = avisoDe(error)
                    }
                }
        }
    }

    fun alternarFavorito(id: Int) {
        viewModelScope.launch {
            repository.alternarFavorito(id)
        }
    }

    private fun mensajeDe(error: Throwable): String {
        return when (error) {
            is IOException -> "No hay conexión a internet."
            is HttpException -> "Error del servidor: ${error.code()}"
            else -> "No fue posible cargar los smartphones."
        }
    }

    private fun avisoDe(error: Throwable): String {
        return when (error) {
            is IOException ->
                "Sin conexión: mostrando smartphones guardados."

            is HttpException ->
                "No se pudo actualizar el catálogo. Mostrando datos guardados."

            else ->
                "No fue posible actualizar el catálogo. Mostrando datos guardados."
        }
    }
}