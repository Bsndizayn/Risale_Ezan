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
            val prefs = context.getSharedPreferences("PrayerTimeSettings", Context.MODE_PRIVATE)

            // TÜM NAMAZ VAKİTLERİ İÇİN TERCIH ANAHTARINI DUZELTEN KISIM BURADA
            val finalPrayerNameKey = when (prayerName) {
                "Sabah" -> "Imsak" // 'Sabah'ı 'Imsak' anahtarına eşle
                "Güneş" -> "Gunes" // 'Güneş'i 'Gunes' anahtarına eşle
                "Öğle" -> "Ogle"   // 'Öğle'yi 'Ogle' anahtarına eşle
                "İkindi" -> "Ikindi" // 'İkindi'yi 'Ikindi' anahtarına eşle
                "Akşam" -> "Aksam" // 'Akşam'ı 'Aksam' anahtarına eşle
                "Yatsı" -> "Yatsi" // 'Yatsı'yı 'Yatsi' anahtarına eşle
                else -> prayerName.capitalizeAsCustom() // Her ihtimale karşı fallback
            }

            val soundPreferenceKey = "sound_res_id_${finalPrayerNameKey}"
            val soundResId = prefs.getInt(soundPreferenceKey, R.raw.ezan) // Varsayılan: Ezan

            val serviceIntent = Intent(context, EzanPlaybackService::class.java).apply {
                putExtra("PRAYER_NAME", prayerName)
                putExtra("SOUND_RESOURCE_ID", soundResId)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }

            showNotification(context, prayerName)

        } catch (e: Exception) {
            Log.e("AlarmReceiver", "EzanPlaybackService başlatılırken hata.", e)
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
                Log.d("AlarmReceiver", "WakeLock serbest bırakıldı.")
            }
        }
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
            randomQuote.hashCode() + 1, // Farklı bir request code, çakışmayı önlemek için +1
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