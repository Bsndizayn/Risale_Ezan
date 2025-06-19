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
        Log.d("AlarmReceiver", "$prayerName için alarm alındı.")

        val prefs = context.getSharedPreferences("PrayerTimeSettings", Context.MODE_PRIVATE)

        // 1. ADIM: Bu vaktin bildirimi ana ekrandan açılmış mı? KONTROL ET.
        val enabledPreferenceKey = "alarm_${prayerName.capitalizeAsCustom()}_enabled"
        val isEnabled = prefs.getBoolean(enabledPreferenceKey, true) // Varsayılan: açık

        if (!isEnabled) {
            // Bildirim kapalıysa hiçbir şey yapma.
            Log.d("AlarmReceiver", "$prayerName için bildirim kapalı, işlem yapılmadı.")
            return
        }

        // 2. ADIM: Bildirim açıksa, hangi sesin çalacağını KONTROL ET.
        playSound(context, prayerName)

        // 3. ADIM: Bildirimi göster.
        showNotification(context, prayerName)
    }

    private fun playSound(context: Context, prayerName: String) {
        val prefs = context.getSharedPreferences("PrayerTimeSettings", Context.MODE_PRIVATE)

        // Vakit ismine göre ses ayarı anahtarını oluştur
        val soundPreferenceKey = "sound_res_id_${prayerName.capitalizeAsCustom()}"

        // O vakit için kaydedilmiş sesin ID'sini al. Varsayılan olarak Ezan.
        val soundResId = prefs.getInt(soundPreferenceKey, R.raw.ezan)

        // Eğer ses "Sessiz" olarak ayarlandıysa (-1), hiçbir şey çalma.
        if (soundResId == -1) {
            Log.d("AlarmReceiver", "$prayerName için ses 'Sessiz' olarak ayarlı.")
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
                setOnCompletionListener { mp -> mp.release() }
            }
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "MediaPlayer hatası", e)
            e.printStackTrace()
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
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Vakit Girdi")
            .setContentText("$prayerName vakti")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)

        notificationManager.notify(prayerName.hashCode(), builder.build())
    }

    // Türkçe karakterleri (İ,Ğ,Ş) doğru şekilde işlemek için yardımcı fonksiyon
    private fun String.capitalizeAsCustom(): String {
        return this.replace("İ", "I")
            .replace("Ğ", "G")
            .replace("Ş", "S")
            .toLowerCase(java.util.Locale.ROOT)
            .capitalize(java.util.Locale.ROOT)
    }
}