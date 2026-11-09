package com.tecsup.smartphones.ui.detalle

import com.tecsup.smartphones.data.repository.ItemRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetalleViewModelFactory(
    private val repository: ItemRepository,
    private val smartphoneId: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(DetalleViewModel::class.java)) {
            return DetalleViewModel(
                repository = repository,
                smartphoneId = smartphoneId
            ) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}