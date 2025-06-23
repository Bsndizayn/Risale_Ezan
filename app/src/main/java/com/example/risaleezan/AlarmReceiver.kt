package com.example.risaleezan

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Locale

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("PRAYER_NAME") ?: "Vakit Girdi"
        Log.d("AlarmReceiver", "$prayerName için alarm alındı.")

        // Telefonu uyandırmak için WakeLock al
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "RisaleEzan::AlarmWakeLock")
        wakeLock.acquire(10*60*1000L /* 10 dakika zaman aşımı */)

        try {
            // Sesi Çal ve Bildirimi Göster
            playSound(context, prayerName) {
                // İşlem bittiğinde WakeLock'u serbest bırak
                if (wakeLock.isHeld) {
                    wakeLock.release()
                    Log.d("AlarmReceiver", "WakeLock serbest bırakıldı.")
                }
            }
            showNotification(context, prayerName)
        } catch (e: Exception) {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }
    }

    private fun playSound(context: Context, prayerName: String, onCompletion: () -> Unit) {
        val prefs = context.getSharedPreferences("PrayerTimeSettings", Context.MODE_PRIVATE)
        val soundPreferenceKey = "sound_res_id_${prayerName.capitalizeAsCustom()}"
        val soundResId = prefs.getInt(soundPreferenceKey, R.raw.ezan)

        if (soundResId == -1) {
            Log.d("AlarmReceiver", "$prayerName için ses 'Sessiz' olarak ayarlı.")
            onCompletion() // Sessizse bile tamamlandığını bildir
            return
        }

        try {
            val soundUri = Uri.parse("android.resource://${context.packageName}/$soundResId")
            val mediaPlayer = MediaPlayer().apply {
                setDataSource(context, soundUri)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setAudioAttributes(audioAttributes)
                prepare()
                start()
                setOnCompletionListener { mp ->
                    mp.release()
                    onCompletion() // Ses bittiğinde tamamlandığını bildir
                }
                setOnErrorListener { _, _, _ ->
                    onCompletion() // Hata olursa da tamamlandığını bildir
                    true
                }
            }
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "MediaPlayer hatası", e)
            onCompletion() // Hata olursa da tamamlandığını bildir
        }
    }

    private fun showNotification(context: Context, prayerName: String) {
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
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$prayerName Vakti")
            .setContentText("Hayırlı namazlar dileriz. ⏰")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(prayerName.hashCode(), builder.build())
    }

    private fun String.capitalizeAsCustom(): String {
        return this.replace("İ", "I")
            .replace("Ğ", "G")
            .replace("Ş", "S")
            .lowercase(Locale.ROOT)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }
}