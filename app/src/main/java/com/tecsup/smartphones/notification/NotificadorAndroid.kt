package com.tecsup.smartphones.notification



import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

import com.tecsup.smartphones.MainActivity
import com.tecsup.smartphones.R

class NotificadorAndroid(
    private val context: Context
) : Notificador {

    companion object {
        private const val CHANNEL_ID = "canal_sincronizacion"
        private const val NOTIFICATION_ID = 100
    }

    init {
        crearCanal()
    }

    override fun mostrar(
        titulo: String,
        texto: String
    ) {

        // Comprobar el permiso en Android 13 o superior.
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Comprobar si las notificaciones están habilitadas.
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return
        }

        // Intent para abrir MainActivity al tocar la notificación.
        val intent = Intent(
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        // Construir notificación.
        val notificacion = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(texto)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Mostrar notificación.
        NotificationManagerCompat
            .from(context)
            .notify(NOTIFICATION_ID, notificacion)
    }

    private fun crearCanal() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val canal = NotificationChannel(
                CHANNEL_ID,
                "Sincronización del catálogo",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description =
                    "Notificaciones cuando se actualiza el catálogo de smartphones."
            }

            val manager = context.getSystemService(
                NotificationManager::class.java
            )

            manager.createNotificationChannel(canal)
        }
    }
}
