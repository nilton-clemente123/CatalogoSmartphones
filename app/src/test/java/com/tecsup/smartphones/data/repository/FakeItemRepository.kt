package com.tecsup.smartphones.data.repository

import com.tecsup.smartphones.domain.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeItemRepository(
    itemsIniciales: List<Item> = emptyList()
) : ItemRepository {

    private val itemsGuardados = MutableStateFlow(itemsIniciales)

    override val items = itemsGuardados.asStateFlow()

    override val favoritos = items.map { lista ->
        lista.filter { it.esFavorito }
    }

    var alRefrescar: suspend () -> Result<List<Item>> = {
        Result.success(itemsIniciales)
    }

    var llamadasARefrescar = 0
        private set

    val idsAlternados = mutableListOf<Int>()

    override suspend fun refrescar(): Result<Int> {
        llamadasARefrescar++
        return alRefrescar().map { nuevosItems ->
            itemsGuardados.value = nuevosItems
            nuevosItems.size
        }
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> {
        val item = itemsGuardados.value.find { it.id == id }
        return if (item != null) {
            Result.success(item)
        } else {
            Result.failure(NoSuchElementException("Smartphone no encontrado: $id"))
        }
    }

    override suspend fun alternarFavorito(id: Int) {
        idsAlternados.add(id)
        itemsGuardados.value = itemsGuardados.value.map { item ->
            if (item.id == id) item.copy(esFavorito = !item.esFavorito) else item
        }
    }

    override suspend fun cantidadGuardada(): Int = itemsGuardados.value.size

    override suspend fun cantidadFavoritos(): Int =
        itemsGuardados.value.count { it.esFavorito }

    override suspend fun limpiarCache() {
        itemsGuardados.value = emptyList()
    }
}