package com.example.risaleezan

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {

    fun showNotification(context: Context, title: String, message: String) {
        val channelId = "namaz_channel_id" // Bildirim Kanalı ID'si
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8+ için kanal gerekli
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Namaz Vakitleri",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                // ✅ GÜNCELLEME: Bildirim kanalının sesini ve titreşimini kapat
                setSound(null, null)
                enableVibration(false)
                vibrationPattern = null
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Bu ikonun projenizde olduğundan emin olun
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // ✅ GÜNCELLEME: Bildirimin kendi sesini ve titreşimini her zaman kapat
        // Kanalda zaten kapatıldı ama builder seviyesinde de ek bir güvenlik önlemi
        builder.setSound(null)
        builder.setVibrate(null)
        builder.setDefaults(0)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}