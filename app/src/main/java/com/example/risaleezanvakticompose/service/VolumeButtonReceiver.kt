package com.example.risaleezanvakticompose.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class VolumeButtonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "android.media.VOLUME_CHANGED_ACTION" -> {
                android.util.Log.d("VolumeButton", "Ses seviyesi değişti")
                // Ezan çalıyorsa durdur
                PrayerTimeAlarmReceiver.stopSound()

                // Notification'ı kapat
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                notificationManager.cancelAll()
            }
        }
    }

    companion object {
        fun register(context: Context): VolumeButtonReceiver {
            val receiver = VolumeButtonReceiver()
            val filter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")
            context.registerReceiver(receiver, filter)
            return receiver
        }
    }
}