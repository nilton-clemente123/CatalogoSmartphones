package com.tecsup.smartphones.ui.favoritos


import ItemRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FavoritosViewModelFactory(
    private val repository: ItemRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(FavoritosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritosViewModel(repository) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}