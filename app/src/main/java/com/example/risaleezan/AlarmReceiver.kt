package com.example.risaleezan // Paket adını kontrol et

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("PRAYER_NAME") ?: "Vakit Girdi"
        val soundResourceId = intent.getIntExtra("SOUND_RESOURCE_ID", -1)

        Log.d("AlarmReceiver", "$prayerName için alarm alındı.")

        if (soundResourceId != -1) {
            try {
                val soundUri = Uri.parse("android.resource://${context.packageName}/$soundResourceId")
                val mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, soundUri)
                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                    setAudioAttributes(audioAttributes)
                    prepare()
                    start()
                    setOnCompletionListener { mp -> mp.release() }
                }
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "MediaPlayer hatası", e)
                e.printStackTrace()
            }
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "prayer_time_channel"
        val channelName = "Namaz Vakti Bildirimleri"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Namaz vakitleri girdiğinde bildirim gönderir."
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            // --- DÜZELTİLEN SATIR ---
            // Projemizde olmayan ikon yerine, sistemde her zaman var olan bir ikonu kullandık.
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Vakit Girdi")
            .setContentText("$prayerName vakti")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)

        notificationManager.notify(prayerName.hashCode(), builder.build())
    }
}