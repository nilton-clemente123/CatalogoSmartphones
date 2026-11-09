package com.tecsup.smartphones.ui.admin

import com.tecsup.smartphones.data.repository.ItemRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.smartphones.notification.Notificador


class AdminViewModelFactory(
    private val repository: ItemRepository,
    private val notificador: Notificador
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(
                repository,
                notificador
            ) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}