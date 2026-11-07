package com.tecsup.smartphones.data.repository

import ItemRepository
import com.tecsup.smartphones.data.remote.ApiService
import com.tecsup.smartphones.data.remote.dto.ProductDto
import com.tecsup.smartphones.domain.model.Item
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ItemRepositoryImpl(
    private val apiService: ApiService
) : ItemRepository {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    override val items: Flow<List<Item>> = _items.asStateFlow()

    private val _favoritos = MutableStateFlow<List<Item>>(emptyList())
    override val favoritos: Flow<List<Item>> = _favoritos.asStateFlow()

    override suspend fun refrescar(): Result<Int> {
        return try {
            // Solicitar los smartphones a DummyJSON
            val respuesta = apiService.obtenerSmartphones()

            // Convertir los DTO recibidos en modelos de dominio
            val smartphones = respuesta.products.map { producto ->
                producto.toItem()
            }

            // Guardar temporalmente los resultados en memoria
            _items.value = smartphones

            Result.success(smartphones.size)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> {
        return try {
            val producto = apiService.obtenerDetalle(id)
            Result.success(producto.toItem())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun alternarFavorito(id: Int) {
        val actualizados = _items.value.map { item ->
            if (item.id == id) {
                item.copy(esFavorito = !item.esFavorito)
            } else {
                item
            }
        }

        _items.value = actualizados
        _favoritos.value = actualizados.filter { it.esFavorito }
    }

    override suspend fun cantidadGuardada(): Int {
        return _items.value.size
    }

    override suspend fun cantidadFavoritos(): Int {
        return _favoritos.value.size
    }

    override suspend fun limpiarCache() {
        _items.value = emptyList()
        _favoritos.value = emptyList()
    }

    private fun ProductDto.toItem(): Item {
        return Item(
            id = id,
            titulo = title,
            subtitulo = description,
            imagenUrl = thumbnail,
            esFavorito = false,
            precio = price,
            marca = brand,
            calificacion = rating,
            stock = stock
        )
    }
}