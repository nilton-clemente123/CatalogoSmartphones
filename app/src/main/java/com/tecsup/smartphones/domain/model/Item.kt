package com.tecsup.smartphones.domain.model

data class Item(
    val id: Int,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String?,
    val esFavorito: Boolean = false,
    val precio: Double = 0.0,
    val marca: String? = null,
    val calificacion: Double = 0.0,
    val stock: Int = 0
)