package com.tecsup.smartphones.ui.lista

import com.tecsup.smartphones.data.repository.ItemRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.smartphones.notification.Notificador

class ListaViewModelFactory(
    private val repository: ItemRepository,
    private val notificador: Notificador
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(ListaViewModel::class.java)) {
            return ListaViewModel(
                repository = repository,
                notificador = notificador
            ) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}
