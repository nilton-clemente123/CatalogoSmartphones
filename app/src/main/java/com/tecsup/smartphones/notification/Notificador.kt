package com.tecsup.smartphones.notification

interface Notificador {

    fun mostrar(
        titulo: String,
        texto: String
    )
}