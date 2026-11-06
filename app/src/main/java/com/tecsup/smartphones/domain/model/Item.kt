package com.tecsup.smartphones.domain.model


data class Item(
    val id: Int,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String?,
    val esFavorito: Boolean = false
)
