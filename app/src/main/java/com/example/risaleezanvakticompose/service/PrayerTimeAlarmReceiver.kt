package com.example.risaleezanvakticompose.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.risaleezanvakticompose.MainActivity
import com.example.risaleezanvakticompose.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PrayerTimeAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationSettingsDao: com.example.risaleezanvakticompose.data.local.dao.NotificationSettingsDao

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        const val EXTRA_PRAYER_NAME = "prayer_name"
        const val EXTRA_PRAYER_TIME = "prayer_time"
        const val EXTRA_SOUND_NAME = "sound_name"

        private const val CHANNEL_ID = "prayer_times_channel"
        private const val CHANNEL_NAME = "Namaz Vakitleri"

        private var mediaPlayer: MediaPlayer? = null

        // stopSound fonksiyonunu companion object içinde tanımlıyoruz
        fun stopSound() {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: return
        val prayerTime = intent.getStringExtra(EXTRA_PRAYER_TIME) ?: return
        val soundName = intent.getStringExtra(EXTRA_SOUND_NAME) ?: "default_ezan"

        // PendingIntent için goAsync kullan - BroadcastReceiver'ın 10 saniye limiti var
        val pendingResult = goAsync()

        scope.launch {
            try {
                createNotificationChannel(context)
                playPrayerSound(context, soundName)
                showNotification(context, prayerName, prayerTime)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Namaz vakitleri için bildirimler"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun playPrayerSound(context: Context, soundName: String) {
        try {
            // Önceki sesi durdur
            stopSound()

            // Ses dosyasını bul
            val soundUri = getSoundUri(context, soundName)

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )

                setDataSource(context, soundUri)
                prepare()
                start()

                // Ses bitince MediaPlayer'ı temizle
                setOnCompletionListener {
                    stopSound()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getSoundUri(context: Context, soundName: String): Uri {
        val resourceId = context.resources.getIdentifier(
            soundName,
            "raw",
            context.packageName
        )

        return if (resourceId != 0) {
            // Ses dosyası bulundu
            Uri.parse("android.resource://${context.packageName}/$resourceId")
        } else {
            // Varsayılan alarm sesi
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }
    }

    private fun showNotification(context: Context, prayerName: String, prayerTime: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Sesi durdurma action'ı
        val stopSoundIntent = Intent(context, StopSoundReceiver::class.java)
        val stopSoundPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopSoundIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("$prayerName Vakti")
            .setContentText("Vakit: $prayerTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Sesi Durdur",
                stopSoundPendingIntent
            )
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(prayerName.hashCode(), notification)
    }
}

// Sesi durdurmak için ayrı bir receiver
class StopSoundReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        PrayerTimeAlarmReceiver.stopSound()
    }
}