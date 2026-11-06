
package com.tecsup.smartphones.data.remote

import com.tecsup.smartphones.data.remote.dto.ProductDto
import com.tecsup.smartphones.data.remote.dto.ProductListResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("products/category/smartphones")
    suspend fun obtenerSmartphones(): ProductListResponse

    @GET("products/{id}")
    suspend fun obtenerDetalle(
        @Path("id") id: Int
    ): ProductDto
}
