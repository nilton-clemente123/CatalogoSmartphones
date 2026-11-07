package com.tecsup.smartphones.data.mapper

import com.tecsup.smartphones.data.local.ItemEntity
import com.tecsup.smartphones.data.remote.dto.ProductDto
import com.tecsup.smartphones.domain.model.Item


fun ProductDto.toEntity(esFavorito: Boolean = false): ItemEntity {
    return ItemEntity(
        id = id,
        titulo = title,
        subtitulo = description,
        imagenUrl = thumbnail,
        esFavorito = esFavorito,
        precio = price,
        marca = brand,
        calificacion = rating,
        stock = stock
    )
}

fun ItemEntity.toDomain(): Item {
    return Item(
        id = id,
        titulo = titulo,
        subtitulo = subtitulo,
        imagenUrl = imagenUrl,
        esFavorito = esFavorito,
        precio = precio,
        marca = marca,
        calificacion = calificacion,
        stock = stock
    )
}