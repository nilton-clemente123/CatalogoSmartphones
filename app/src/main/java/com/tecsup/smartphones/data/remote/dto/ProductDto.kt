package com.tecsup.smartphones.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val brand: String? = null,
    val rating: Double = 0.0,
    val stock: Int = 0,
    val thumbnail: String? = null,
    val images: List<String> = emptyList()
)
