package com.example.risaleezan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NamazAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val prayerName = intent?.getStringExtra("PRAYER_NAME") ?: "Namaz Vakti"
            NotificationHelper.showNotification(
                it,
                "Vakit Girdi",
                "$prayerName vakti geldi ⏰"
            )
        }
    }
}