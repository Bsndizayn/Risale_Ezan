package com.example.risaleezan

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
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
        val notificationQuote = intent?.getStringExtra("NOTIFICATION_QUOTE") ?: ""

        Log.d("EzanSistemi", "EzanPlaybackService servisi başlatıldı. Ses ID: $soundResourceId")

        createNotificationChannel() // Kanal oluşturuluyor (artık kanalı sessiz yapacağız)
        val notification = createNotification(prayerName, notificationQuote, soundResourceId)
        startForeground(NOTIFICATION_ID, notification)

        if (soundResourceId != -1) { // Ses ID -1 değilse (yani ses çalması gerekiyorsa)
            if (mediaPlayer == null) {
                try {
                    mediaPlayer = MediaPlayer.create(this, soundResourceId).apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .build()
                        )
                        setOnCompletionListener { mp ->
                            mp.release()
                            mediaPlayer = null
                            stopSelf() // Ses bittikten sonra servisi durdur
                        }
                    }
                    mediaPlayer?.start()
                    Log.d("EzanSistemi", "Ezan çalıyor. Ses ID: $soundResourceId")
                } catch (e: Exception) {
                    Log.e("EzanSistemi", "Ezan çalarken hata: ${e.message}", e)
                    stopSelf() // Hata durumunda servisi durdur
                }
            }
        } else { // Ses ID -1 ise (yani sessiz olması gerekiyorsa)
            Log.d("EzanSistemi", "Ses ID -1, ezan çalmayacak. Bildirim görünecek.")
            // Sessiz bildirim durumunda servisi hemen durdurmuyoruz.
            // Bildirimin ekranda kalmasını sağlamak için servis çalışmaya devam etmeli.
        }

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Ezan Çalma Servisi",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Ezan sesini arka planda çalmak için servis kanalı"
                // ✅ GÜNCELLEME: Bildirim kanalının sesini ve titreşimini tamamen kapat
                setSound(null, null)
                enableVibration(false)
                vibrationPattern = null
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(prayerName: String, quote: String, soundResId: Int): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val muteIntent = Intent(this, MuteActionReceiver::class.java).apply {
            action = "ACTION_MUTE_SOUND"
            putExtra("PRAYER_NAME", prayerName)
        }
        val mutePendingIntent = PendingIntent.getBroadcast(
            this,
            prayerName.hashCode(),
            muteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, quote)
        }
        val chooser = Intent.createChooser(shareIntent, "Vecizeyi Paylaş")
        val sharePendingIntent = PendingIntent.getActivity(
            this,
            quote.hashCode() + 1,
            chooser,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("$prayerName Vakti")
            .setContentText(quote)
            .setStyle(NotificationCompat.BigTextStyle().bigText(quote))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_share, "Vecizeyi Paylaş", sharePendingIntent)
            .addAction(android.R.drawable.ic_notification_clear_all, "Sessize Al", mutePendingIntent)

        // ✅ GÜNCELLEME: Bildirimin kendi sesini ve titreşimini her zaman kapat
        // Çünkü kanal zaten sessiz ayarlandı. Bu satırlar ek bir güvenlik önlemi olarak kalabilir.
        builder.setSound(null)
        builder.setVibrate(null)
        builder.setDefaults(0)
        Log.d("EzanSistemi", "Bildirim her zaman sessiz ayarlandı (ses MediaPlayer'dan gelecek).")

        return builder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("EzanSistemi", "EzanPlaybackService servisi durduruldu ve kaynaklar serbest bırakıldı.")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}