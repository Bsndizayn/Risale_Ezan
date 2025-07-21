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
        val notificationQuote = intent?.getStringExtra("NOTIFICATION_QUOTE") ?: "" // Yeni: Vecizeyi al

        Log.d("EzanSistemi", "EzanPlaybackService servisi başlatıldı. Ses ID: $soundResourceId")

        createNotificationChannel()
        val notification = createNotification(prayerName, notificationQuote) // Vecizeyi bildirim oluşturucuya gönder
        startForeground(NOTIFICATION_ID, notification)

        if (soundResourceId != -1) {
            playAdhan(soundResourceId)
        } else {
            Log.e("EzanSistemi", "Ses dosyası ID'si bulunamadı (muhtemelen Sessiz seçildi), servis durduruluyor.")
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun playAdhan(soundResId: Int) {
        mediaPlayer?.release()
        mediaPlayer = null

        if (soundResId == -1) {
            Log.d("EzanSistemi", "Ses 'Sessiz' olarak ayarlı, çalınmayacak.")
            stopSelf()
            return
        }

        try {
            mediaPlayer = MediaPlayer.create(this, soundResId)
            if (mediaPlayer == null) {
                Log.e("EzanSistemi", "MediaPlayer oluşturulamadı: $soundResId")
                stopSelf()
                return
            }

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            mediaPlayer?.setAudioAttributes(audioAttributes)

            mediaPlayer?.setOnCompletionListener {
                Log.d("EzanSistemi", "Ezan sesi çalması bitti, servis durduruluyor.")
                stopSelf()
            }

            mediaPlayer?.setOnErrorListener { _, what, extra ->
                Log.e("EzanSistemi", "MediaPlayer hata: what=$what, extra=$extra")
                stopSelf()
                true
            }

            mediaPlayer?.start()
            Log.d("EzanSistemi", "Ezan sesi çalmaya başladı: $soundResId")

        } catch (e: Exception) {
            Log.e("EzanSistemi", "MediaPlayer başlatılamadı veya çalarken hata.", e)
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

    // Vecize parametresi eklendi
    private fun createNotification(prayerName: String, quote: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Sustur Butonu için Intent
        val muteIntent = Intent(this, MuteActionReceiver::class.java).apply {
            action = "ACTION_MUTE_SOUND"
        }
        val mutePendingIntent = PendingIntent.getBroadcast(
            this,
            prayerName.hashCode(), // Her bildirim için farklı bir request code
            muteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Paylaş Butonu için Intent (AlarmReceiver'daki gibi)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, quote)
        }
        val chooser = Intent.createChooser(shareIntent, "Vecizeyi Paylaş")
        val sharePendingIntent = PendingIntent.getActivity(
            this,
            quote.hashCode() + 1, // Farklı bir request code
            chooser,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("$prayerName Vakti") // Bildirim başlığı
            .setContentText(quote) // Vecizeyi buraya ekledik
            .setStyle(NotificationCompat.BigTextStyle().bigText(quote)) // Büyük metin stili
            .setSmallIcon(R.mipmap.ic_launcher) // Küçük ikon
            .setContentIntent(pendingIntent) // Bildirime tıklayınca ne olacağı
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Öncelik
            .setSilent(true) // Bu bildirimin kendi sesini veya titreşimini yapmamasını sağla (ezan çalacağı için)
            .setOngoing(true) // Kullanıcının kaydırarak kapatmasını engelle (ön plan servisi)
            .addAction(android.R.drawable.ic_menu_share, "Vecizeyi Paylaş", sharePendingIntent) // Paylaş butonu
            .addAction(android.R.drawable.ic_notification_clear_all, "Sessize Al", mutePendingIntent) // Sustur butonu
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("EzanSistemi", "EzanPlaybackService yok edildi.")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}