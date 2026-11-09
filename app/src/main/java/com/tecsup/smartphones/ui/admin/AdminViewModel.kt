package com.tecsup.smartphones.ui.admin


import com.tecsup.smartphones.data.repository.ItemRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.smartphones.notification.Notificador

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _totalGuardado = MutableStateFlow(0)
    val totalGuardado = _totalGuardado.asStateFlow()

    private val _totalFavoritos = MutableStateFlow(0)
    val totalFavoritos = _totalFavoritos.asStateFlow()

    private val _ocupado = MutableStateFlow(false)
    val ocupado = _ocupado.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    init {
        actualizarTotales()
    }

    // Consulta las cantidades almacenadas en Room.
    fun actualizarTotales() {
        viewModelScope.launch {
            cargarTotales()
        }
    }

    private suspend fun cargarTotales() {
        _totalGuardado.value = repository.cantidadGuardada()
        _totalFavoritos.value = repository.cantidadFavoritos()
    }

    // Sincroniza los smartphones desde DummyJSON hacia Room.
    fun sincronizar() {
        if (_ocupado.value) return

        viewModelScope.launch {
            _ocupado.value = true
            _mensaje.value = null

            try {
                val resultado = repository.refrescar()

                resultado
                    .onSuccess { cantidad ->
                        _mensaje.value =
                            "Sincronización completada. Se guardaron $cantidad smartphones."
                        notificador.mostrar(
                            titulo = "Datos actualizados",
                            texto = "Se guardaron $cantidad elementos"
                        )
                    }
                    .onFailure { error ->
                        _mensaje.value =
                            "No se pudo sincronizar: ${error.message ?: "Error desconocido"}"
                    }

                cargarTotales()

            } catch (e: Exception) {
                _mensaje.value =
                    "Ocurrió un error al sincronizar: ${e.message}"

            } finally {
                _ocupado.value = false
            }
        }
    }

    // Elimina los smartphones guardados localmente.
    fun limpiarCache() {
        if (_ocupado.value) return

        viewModelScope.launch {
            _ocupado.value = true
            _mensaje.value = null

            try {
                repository.limpiarCache()
                cargarTotales()

                _mensaje.value = "La caché se limpió correctamente."

            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo limpiar la caché: ${e.message}"

            } finally {
                _ocupado.value = false
            }
        }
    }
}