package com.example.risaleezan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_STOP_SOUND") {
            Log.d("EzanService", "Bildirimden susturuldu.")

            val stopIntent = Intent(context, EzanPlaybackService::class.java)

            // Android 8 ve üzeri için servisi güvenli şekilde durdur
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.stopService(stopIntent)
            } else {
                context.stopService(stopIntent)
            }
        }
    }
}
