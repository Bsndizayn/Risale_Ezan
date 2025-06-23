package com.example.risaleezan

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Locale

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("PRAYER_NAME") ?: "Vakit Girdi"
        Log.d("AlarmReceiver", "$prayerName için alarm alındı.")

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "RisaleEzan::AlarmWakeLock")
        wakeLock.acquire(10 * 60 * 1000L /* 10 dakika zaman aşımı */)

        try {
            playSound(context, prayerName) {
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
        // İsimdeki Türkçe karakterleri API'den gelen isme benzetiyoruz (Imsak, Ogle vb.)
        val soundPreferenceKey = "sound_res_id_${prayerName.capitalizeAsCustom()}"
        val soundResId = prefs.getInt(soundPreferenceKey, R.raw.ezan)

        // Yeni merkezi ses yöneticimizi kullanıyoruz
        SoundPlayerManager.play(context, soundResId, onCompletion)
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

        // 1. Sustur butonu için Intent ve PendingIntent oluştur
        val muteIntent = Intent(context, MuteActionReceiver::class.java).apply {
            action = "ACTION_MUTE_SOUND"
        }
        val mutePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            muteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. Bildirimi yeni özelliklerle oluştur
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            // YENİ: Büyük ikonu (resmi) ekle
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_risale))
            .setContentTitle("$prayerName Vakti")
            // YENİ: Bildirim metnini değiştir (bu metni istediğiniz gibi güncelleyebilirsiniz)
            .setContentText("Vakit girdi. Allah kabul etsin.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            // YENİ: "Sustur" butonunu (Action) ekle
            .addAction(R.drawable.ic_risale, "Sustur", mutePendingIntent)

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