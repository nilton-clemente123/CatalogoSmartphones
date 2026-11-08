
package com.tecsup.smartphones

import ItemRepository
import android.content.Context
import androidx.room.Room

import com.tecsup.smartphones.data.auth.AuthRepository
import com.tecsup.smartphones.data.auth.FirebaseAuthRepository
import com.tecsup.smartphones.data.local.AppDatabase
import com.tecsup.smartphones.data.remote.RetrofitClient
import com.tecsup.smartphones.data.repository.ItemRepositoryImpl
import com.tecsup.smartphones.notification.Notificador
import com.tecsup.smartphones.notification.NotificadorAndroid

object AppContainer {

    private var notificadorInstancia: Notificador? = null

    private var database: AppDatabase? = null
    private var repository: ItemRepository? = null

    val notificador: Notificador
        get() = checkNotNull(notificadorInstancia) {
            "El notificador todavía no ha sido inicializado."
        }

    // Inicialización de Room.
    fun inicializar(context: Context) {
        if (database == null) {
            database = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "catalogo_smartphones.db"
            ).build()
        }

        if (notificadorInstancia == null) {
            notificadorInstancia = NotificadorAndroid(
                context.applicationContext
            )
        }
    }

    // Repositorio de smartphones.
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

    // Repositorio de autenticación Firebase.
    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository()
    }
}
