package com.example.risaleezan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MuteActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_MUTE_SOUND") {
            Log.d("MuteActionReceiver", "Sustur butonuna basıldı.")
            // EzanPlaybackService'i durdurmak için Intent gönderin
            val stopServiceIntent = Intent(context, EzanPlaybackService::class.java)
            context.stopService(stopServiceIntent)
            Log.d("MuteActionReceiver", "EzanPlaybackService durdurma komutu gönderildi.")
        }
    }
}