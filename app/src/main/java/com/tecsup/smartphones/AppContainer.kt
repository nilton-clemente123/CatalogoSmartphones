package com.tecsup.smartphones

import android.content.Context

/**
 * Contenedor de dependencias manual de la aplicación.
 *
 * Centraliza la construcción de los data sources, mappers, repositorios y
 * casos de uso para luego inyectarlos en las distintas pantallas (UI).
 *
 * TODO: instanciar aquí las clases de las capas de datos según se vayan
 * implementando:
 *  - data/local   -> fuentes locales (Room, DataStore, etc.)
 *  - data/remote  -> fuentes remotas (Retrofit, Ktor, etc.)
 *  - data/auth    -> autenticación (Firebase Auth, etc.)
 *  - data/mapper  -> mapeadores DTO <-> modelos de dominio
 *  - data/repository -> implementaciones de repositorios
 *  - domain/model -> modelos de dominio
 */
class AppContainer(context: Context) {

    // --- Data sources ---
    // private val localDataSource = ...
    // private val remoteDataSource = ...
    // private val authDataSource = ...

    // --- Mappers ---
    // private val smartphoneMapper = ...

    // --- Repositories ---
    // val smartphoneRepository: SmartphoneRepository = ...
    // val authRepository: AuthRepository = ...
}
