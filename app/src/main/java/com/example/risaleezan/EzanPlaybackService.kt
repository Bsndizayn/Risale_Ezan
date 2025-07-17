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
import com.example.risaleezan.MainActivity // MainActivity'e doğrudan referans vermek için eklendi

class EzanPlaybackService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val NOTIFICATION_ID = 1 // Foreground Service için benzersiz bildirim ID'si
    private val CHANNEL_ID = "ezan_playback_channel" // Foreground Service bildirim kanalı

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prayerName = intent?.getStringExtra("PRAYER_NAME") ?: "Namaz Vakti"
        // SOUND_RESOURCE_ID'yi AlarmReceiver'dan al
        val soundResourceId = intent?.getIntExtra("SOUND_RESOURCE_ID", -1) ?: -1

        Log.d("EzanSistemi", "EzanPlaybackService servisi başlatıldı. Ses ID: $soundResourceId")

        // Bildirim kanalını oluştur ve servisi ön plana çıkar
        createNotificationChannel()
        val notification = createNotification(prayerName)
        startForeground(NOTIFICATION_ID, notification)

        if (soundResourceId != -1) {
            playAdhan(soundResourceId)
        } else {
            Log.e("EzanSistemi", "Ses dosyası ID'si bulunamadı (muhtemelen Sessiz seçildi), servis durduruluyor.")
            stopSelf() // Geçersiz ID veya Sessiz seçiliyse servisi durdur
        }

        // Servisin sistem tarafından öldürülmesi durumunda yeniden başlamamasını sağla
        return START_NOT_STICKY
    }

    private fun playAdhan(soundResId: Int) {
        mediaPlayer?.release() // Önceki MediaPlayer kaynaklarını serbest bırak
        mediaPlayer = null // Null olarak ayarla

        if (soundResId == -1) {
            Log.d("EzanSistemi", "Ses 'Sessiz' olarak ayarlı, çalınmayacak.")
            stopSelf() // Sessizse servisi durdur
            return
        }

        try {
            mediaPlayer = MediaPlayer.create(this, soundResId)
            if (mediaPlayer == null) {
                Log.e("EzanSistemi", "MediaPlayer oluşturulamadı: $soundResId")
                stopSelf() // Oluşturulamadıysa servisi durdur
                return
            }

            // Ses özelliklerini ayarla (alarm sesi gibi davranması için)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            mediaPlayer?.setAudioAttributes(audioAttributes)

            mediaPlayer?.setOnCompletionListener {
                Log.d("EzanSistemi", "Ezan sesi çalması bitti, servis durduruluyor.")
                stopSelf() // Ses çalması bittiğinde servisi durdur
            }

            mediaPlayer?.setOnErrorListener { _, what, extra ->
                Log.e("EzanSistemi", "MediaPlayer hata: what=$what, extra=$extra")
                stopSelf() // Hata oluştuğunda servisi durdur
                true // Hatayı işlediğimizi belirt
            }

            mediaPlayer?.start()
            Log.d("EzanSistemi", "Ezan sesi çalmaya başladı: $soundResId")

        } catch (e: Exception) {
            Log.e("EzanSistemi", "MediaPlayer başlatılamadı veya çalarken hata.", e)
            stopSelf() // Hata durumunda servisi durdur
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
            .setContentTitle("Ezan Vakti") // Bildirim başlığı
            .setContentText("$prayerName vakti şu an çalıyor.") // Bildirim içeriği
            .setSmallIcon(R.mipmap.ic_launcher) // Küçük ikon
            .setContentIntent(pendingIntent) // Bildirime tıklayınca ne olacağı
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Öncelik
            .setSilent(true) // Bu bildirimin kendi sesini veya titreşimini yapmamasını sağla (ezan çalacağı için)
            .setOngoing(true) // Kullanıcının kaydırarak kapatmasını engelle (ön plan servisi)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // MediaPlayer kaynaklarını serbest bırak
        mediaPlayer = null
        Log.d("EzanSistemi", "EzanPlaybackService yok edildi.")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}