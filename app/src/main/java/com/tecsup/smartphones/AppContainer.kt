package com.tecsup.smartphones

import ItemRepository
import android.content.Context
import androidx.room.Room
import com.tecsup.smartphones.data.local.AppDatabase
import com.tecsup.smartphones.data.remote.RetrofitClient
import com.tecsup.smartphones.data.repository.ItemRepositoryImpl

object AppContainer {

    private var database: AppDatabase? = null
    private var repository: ItemRepository? = null

    fun inicializar(context: Context) {
        if (database == null) {
            database = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "catalogo_smartphones.db"
            ).build()
        }
    }

    val itemRepository: ItemRepository
        get() {
            val dao = checkNotNull(database) {
                "La base de datos todavía no ha sido inicializada."
            }.itemDao()

            return repository ?: ItemRepositoryImpl(
                apiService = RetrofitClient.apiService,
                itemDao = dao
            ).also {
                repository = it
            }
        }
}