package com.tecsup.smartphones.data.remote.dto



import kotlinx.serialization.Serializable

@Serializable
data class ProductListResponse(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
