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

            val finalPrayerNameKey = when (prayerName) {
                "Sabah" -> "Imsak"
                "Güneş" -> "Gunes"
                "Öğle" -> "Ogle"
                "İkindi" -> "Ikindi"
                "Akşam" -> "Aksam"
                "Yatsı" -> "Yatsi"
                else -> prayerName.capitalizeAsCustom()
            }

            val soundPreferenceKey = "sound_res_id_${finalPrayerNameKey}"
            val soundResId = prefs.getInt(soundPreferenceKey, R.raw.ezan)

            // Rastgele Vecize Seçimi (Bildirimde kullanılacak)
            val quotes = context.resources.getStringArray(R.array.notification_quotes)
            val randomQuote = quotes.random()

            val serviceIntent = Intent(context, EzanPlaybackService::class.java).apply {
                putExtra("PRAYER_NAME", prayerName)
                putExtra("SOUND_RESOURCE_ID", soundResId)
                putExtra("NOTIFICATION_QUOTE", randomQuote) // Vecizeyi ekledik
                // Diğer bildirim aksiyonları için gerekli bilgileri de ekleyebiliriz,
                // veya EzanPlaybackService bunları kendi içinde oluşturabilir.
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }

            // showNotification(context, prayerName) // BU SATIR ARTIK YOK, EzanPlaybackService halledecek

        } catch (e: Exception) {
            Log.e("AlarmReceiver", "EzanPlaybackService başlatılırken hata.", e)
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
                Log.d("AlarmReceiver", "WakeLock serbest bırakıldı.")
            }
        }
    }

    // showNotification metodu artık bu sınıfta gerekli değil, kaldırıldı
    /*
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

        val muteIntent = Intent(context, MuteActionReceiver::class.java).apply {
            action = "ACTION_MUTE_SOUND"
        }
        val mutePendingIntent = PendingIntent.getBroadcast(
            context,
            prayerName.hashCode(),
            muteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val quotes = context.resources.getStringArray(R.array.notification_quotes)
        val randomQuote = quotes.random()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, randomQuote)
        }
        val chooser = Intent.createChooser(shareIntent, "Vecizeyi Paylaş")
        val sharePendingIntent = PendingIntent.getActivity(
            context,
            randomQuote.hashCode() + 1,
            chooser,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$prayerName Vakti")
            .setContentText(randomQuote)
            .setStyle(NotificationCompat.BigTextStyle().bigText(randomQuote))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_share, "Vecizeyi Paylaş", sharePendingIntent)
            .addAction(
                android.R.drawable.ic_notification_clear_all,
                "Sessize Al",
                mutePendingIntent
            )

        notificationManager.notify(prayerName.hashCode(), builder.build())
    }
    */

    private fun String.capitalizeAsCustom(): String {
        return this.replace("İ", "I")
            .replace("Ğ", "G")
            .replace("Ş", "S")
            .lowercase(Locale.ROOT)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }
}