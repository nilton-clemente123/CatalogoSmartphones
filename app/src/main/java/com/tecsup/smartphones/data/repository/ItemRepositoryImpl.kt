package com.tecsup.smartphones.data.repository

import com.tecsup.smartphones.data.local.ItemDao
import com.tecsup.smartphones.data.mapper.toDomain
import com.tecsup.smartphones.data.mapper.toEntity
import com.tecsup.smartphones.data.remote.ApiService
import com.tecsup.smartphones.domain.model.Item
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class ItemRepositoryImpl(
    private val apiService: ApiService,
    private val itemDao: ItemDao
) : ItemRepository {

    // Los listados ahora se obtienen desde Room.
    override val items: Flow<List<Item>> =
        itemDao.todos().map { entidades ->
            entidades.map { it.toDomain() }
        }

    override val favoritos: Flow<List<Item>> =
        itemDao.favoritos().map { entidades ->
            entidades.map { it.toDomain() }
        }

    // Obtiene los smartphones de la API y los guarda en Room.
    override suspend fun refrescar(): Result<Int> {
        return try {
            // Conservamos los favoritos actuales.
            val idsFavoritos = itemDao.idsFavoritos().toSet()

            // Consultamos la API.
            val respuesta = apiService.obtenerSmartphones()

            // Convertimos los DTO en entidades.
            val entidades = respuesta.products.map { producto ->
                producto.toEntity(
                    esFavorito = producto.id in idsFavoritos
                )
            }

            // Guardamos los resultados en la base de datos.
            itemDao.guardarTodos(entidades)

            Result.success(entidades.size)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtiene el detalle desde la API y también lo almacena en Room.
    override suspend fun obtenerDetalle(id: Int): Result<Item> {
        return try {
            val producto = apiService.obtenerDetalle(id)

            val esFavorito = id in itemDao.idsFavoritos().toSet()

            val entidad = producto.toEntity(esFavorito)
            itemDao.guardarTodos(listOf(entidad))

            Result.success(entidad.toDomain())

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun alternarFavorito(id: Int) {
        itemDao.alternarFavorito(id)
    }

    override suspend fun cantidadGuardada(): Int {
        return itemDao.contar()
    }

    override suspend fun cantidadFavoritos(): Int {
        return itemDao.contarFavoritos()
    }

    override suspend fun limpiarCache() {
        itemDao.borrarTodo()
    }
}