package com.tecsup.smartphones.data.local


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    // Obtener todos los smartphones
    @Query("SELECT * FROM items ORDER BY titulo")
    fun todos(): Flow<List<ItemEntity>>

    // Obtener los smartphones favoritos
    @Query("SELECT * FROM items WHERE esFavorito = 1 ORDER BY titulo")
    fun favoritos(): Flow<List<ItemEntity>>

    // Obtener los IDs de los favoritos
    @Query("SELECT id FROM items WHERE esFavorito = 1")
    suspend fun idsFavoritos(): List<Int>

    // Contar los smartphones almacenados
    @Query("SELECT COUNT(*) FROM items")
    suspend fun contar(): Int

    // Contar los favoritos
    @Query("SELECT COUNT(*) FROM items WHERE esFavorito = 1")
    suspend fun contarFavoritos(): Int

    // Insertar o actualizar smartphones
    @Upsert
    suspend fun guardarTodos(items: List<ItemEntity>)

    // Cambiar el estado de favorito
    @Query("""
        UPDATE items
        SET esFavorito = CASE
            WHEN esFavorito = 1 THEN 0
            ELSE 1
        END
        WHERE id = :id
    """)
    suspend fun alternarFavorito(id: Int)

    // Eliminar todos los registros
    @Query("DELETE FROM items")
    suspend fun borrarTodo()
}
