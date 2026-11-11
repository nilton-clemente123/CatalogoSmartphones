package com.tecsup.smartphones.notification

class FakeNotificador : Notificador {

    data class Notificacion(val titulo: String, val texto: String)

    val notificaciones = mutableListOf<Notificacion>()

    override fun mostrar(titulo: String, texto: String) {
        notificaciones.add(Notificacion(titulo, texto))
    }
}