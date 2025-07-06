package com.example.risaleezan

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
        val wakeLock =
            powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "RisaleEzan::AlarmWakeLock")
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
        val soundPreferenceKey = "sound_res_id_${prayerName.capitalizeAsCustom()}"
        val soundResId = prefs.getInt(soundPreferenceKey, R.raw.ezan)

        SoundPlayerManager.play(context, soundResId, onCompletion)
    }

    private fun showNotification(context: Context, prayerName: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "prayer_time_channel"
        val channelName = "Namaz Vakti Bildirimleri"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Namaz vakitleri girdiğinde bildirim gönderir."
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // --- Sustur Butonu için ---
        val muteIntent = Intent(context, MuteActionReceiver::class.java).apply {
            action = "ACTION_MUTE_SOUND"
        }
        val mutePendingIntent = PendingIntent.getBroadcast(
            context,
            prayerName.hashCode(), // Her bildirim için farklı bir request code
            muteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // --- Rastgele Vecize Seçimi ---
        val quotes = context.resources.getStringArray(R.array.notification_quotes)
        val randomQuote = quotes.random()

        // --- YENİ: Paylaş Butonu için ---
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, randomQuote)
        }
        val chooser = Intent.createChooser(shareIntent, "Vecizeyi Paylaş")
        val sharePendingIntent = PendingIntent.getActivity(
            context,
            randomQuote.hashCode(), // Her vecize için farklı bir request code
            chooser,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // --- Bildirimi Oluşturma ---
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$prayerName Vakti")
            .setContentText(randomQuote)
            .setStyle(NotificationCompat.BigTextStyle().bigText(randomQuote))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            // YENİ: Paylaş butonunu ekle
            .addAction(android.R.drawable.ic_menu_share, "Vecizeyi Paylaş", sharePendingIntent)
            // Mevcut Sustur butonunu ekle
            .addAction(
                android.R.drawable.ic_notification_clear_all,
                "Sessize Al",
                mutePendingIntent
            )


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