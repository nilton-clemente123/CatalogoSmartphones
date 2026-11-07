package com.tecsup.smartphones

import ItemRepository
import com.tecsup.smartphones.data.remote.RetrofitClient
import com.tecsup.smartphones.data.repository.ItemRepositoryImpl

object AppContainer {

    val itemRepository: ItemRepository by lazy {
        ItemRepositoryImpl(
            apiService = RetrofitClient.apiService
        )
    }
}