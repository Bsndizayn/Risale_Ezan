// EzanPlaybackService için tam ve güncel kod...
// (Bu kod, bildirimi oluşturan ve sesi çalan en doğru hali içerir)
package com.example.risaleezan

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.risaleezan.MainActivity

class EzanPlaybackService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "ezan_playback_channel"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prayerName = intent?.getStringExtra("PRAYER_NAME") ?: "Namaz Vakti"
        val soundResourceId = intent?.getIntExtra("SOUND_RESOURCE_ID", -1) ?: -1

        Log.d("EzanSistemi", "EzanPlaybackService servisi başlatıldı.")

        createNotificationChannel()
        val notification = createNotification(prayerName)
        startForeground(NOTIFICATION_ID, notification)

        if (soundResourceId != -1) {
            playAdhan(soundResourceId)
        } else {
            Log.e("EzanSistemi", "Ses dosyası ID'si bulunamadı, servis durduruluyor.")
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun playAdhan(soundResId: Int) {
        mediaPlayer?.release()
        try {
            mediaPlayer = MediaPlayer.create(this, soundResId)
            mediaPlayer?.setOnCompletionListener {
                Log.d("EzanSistemi", "Ezan sesi çalması bitti, servis durduruluyor.")
                stopSelf()
            }
            mediaPlayer?.start()
            Log.d("EzanSistemi", "Ezan sesi çalmaya başladı.")
        } catch (e: Exception) {
            Log.e("EzanSistemi", "MediaPlayer başlatılamadı.", e)
            stopSelf()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Ezan Çalma Bildirimi",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(prayerName: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Vakit Girdi")
            .setContentText("$prayerName vakti")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("EzanSistemi", "Servis yok edildi.")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}