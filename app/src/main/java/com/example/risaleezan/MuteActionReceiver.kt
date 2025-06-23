package com.example.risaleezan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MuteActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_MUTE_SOUND") {
            Log.d("MuteActionReceiver", "Sustur butonuna basıldı.")
            SoundPlayerManager.stop()
        }
    }
}