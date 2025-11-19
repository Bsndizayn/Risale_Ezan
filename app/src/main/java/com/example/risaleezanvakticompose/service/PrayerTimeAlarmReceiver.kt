package com.example.risaleezanvakticompose.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.risaleezanvakticompose.MainActivity
import com.example.risaleezanvakticompose.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

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
        private var currentNotificationId: Int = 0
        private var audioManager: AudioManager? = null
        private var audioFocusRequest: AudioFocusRequest? = null
        private var mediaSession: MediaSessionCompat? = null
        private var context: Context? = null

        private var autoStopHandler: Handler? = null
        private const val AUTO_STOP_DURATION = 5 * 60 * 1000L // 5 dakika

        /**
         * Sadece sesi durdurur, bildirimi kapatmaz
         */
        fun stopSoundOnly() {
            try {
                Log.d("PrayerAlarm", "Sadece ses durduruluyor")

                // Auto-stop handler'ı iptal et
                autoStopHandler?.removeCallbacksAndMessages(null)
                autoStopHandler = null

                mediaPlayer?.apply {
                    if (isPlaying) {
                        stop()
                        Log.d("PrayerAlarm", "MediaPlayer durduruldu")
                    }
                    release()
                }
                mediaPlayer = null

                // Audio focus'u bırak
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    audioFocusRequest?.let {
                        audioManager?.abandonAudioFocusRequest(it)
                    }
                } else {
                    @Suppress("DEPRECATION")
                    audioManager?.abandonAudioFocus(null)
                }
                audioFocusRequest = null
                audioManager = null

                // MediaSession'ı kapat
                mediaSession?.apply {
                    isActive = false
                    release()
                }
                mediaSession = null

            } catch (e: Exception) {
                Log.e("PrayerAlarm", "Ses durdurma hatası", e)
            }
        }

        /**
         * Hem sesi durdurur hem de bildirimi kapatır
         */
        fun stopSound() {
            try {
                Log.d("PrayerAlarm", "Ses ve bildirim durduruluyor")

                // Önce sesi durdur
                stopSoundOnly()

                // Sonra bildirimi kapat
                context?.let { ctx ->
                    val notificationManager =
                        ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(currentNotificationId)
                    Log.d("PrayerAlarm", "Bildirim kapatıldı: $currentNotificationId")
                }

            } catch (e: Exception) {
                Log.e("PrayerAlarm", "Ses ve bildirim durdurma hatası", e)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: return
        val prayerTime = intent.getStringExtra(EXTRA_PRAYER_TIME) ?: return
        val soundName = intent.getStringExtra(EXTRA_SOUND_NAME) ?: "system_ringtone"

        Log.d("PrayerAlarm", "Alarm çaldı: $prayerName, Ses: $soundName, Zaman: $prayerTime")

        Companion.context = context.applicationContext

        val pendingResult = goAsync()

        scope.launch {
            try {
                val vecize = getRandomVecize(context)
                createNotificationChannel(context)
                currentNotificationId = prayerName.hashCode()

                withContext(Dispatchers.Main) {
                    setupMediaSession(context)
                }

                playPrayerSound(context, soundName)
                showNotification(context, prayerName, prayerTime, vecize)

                Log.d("PrayerAlarm", "Bildirim ve ses başarıyla başlatıldı")
            } catch (e: Exception) {
                Log.e("PrayerAlarm", "onReceive hatası", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun getRandomVecize(context: Context): String {
        return try {
            val quotesArray = context.resources.getStringArray(R.array.risale_quotes)
            if (quotesArray.isNotEmpty()) {
                quotesArray[Random.nextInt(quotesArray.size)]
            } else {
                "Allah'ı çokça zikredin ki kurtuluşa eresiniz."
            }
        } catch (e: Exception) {
            Log.e("PrayerAlarm", "Vecize çekme hatası", e)
            "Allah'ı çokça zikredin ki kurtuluşa eresiniz."
        }
    }

    private fun setupMediaSession(context: Context) {
        try {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                Log.e("PrayerAlarm", "MediaSession Main thread'de değil!")
                return
            }

            mediaSession = MediaSessionCompat(context, "PrayerAlarm").apply {
                setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                )

                setPlaybackState(
                    PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                        .setActions(PlaybackStateCompat.ACTION_STOP or PlaybackStateCompat.ACTION_PAUSE)
                        .build()
                )

                setCallback(object : MediaSessionCompat.Callback() {
                    override fun onStop() {
                        Log.d("PrayerAlarm", "MediaSession: Stop")
                        stopSoundOnly()
                    }

                    override fun onPause() {
                        Log.d("PrayerAlarm", "MediaSession: Pause")
                        stopSoundOnly()
                    }

                    override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                        Log.d("PrayerAlarm", "MediaSession: Button event")
                        stopSoundOnly()
                        return true
                    }
                })

                isActive = true
            }
            Log.d("PrayerAlarm", "MediaSession kuruldu")
        } catch (e: Exception) {
            Log.e("PrayerAlarm", "MediaSession hatası", e)
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Namaz vakitleri için bildirimler"
                enableLights(true)
                lightColor = android.graphics.Color.GREEN
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                setSound(null, null) // Ses MediaPlayer ile çalacak
                setShowBadge(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            Log.d("PrayerAlarm", "Notification channel oluşturuldu")
        }
    }

    private fun playPrayerSound(context: Context, soundName: String) {
        try {
            Log.d("PrayerAlarm", "Ses çalmaya başlanıyor: $soundName")

            // Önce mevcut sesi durdur
            stopSoundOnly()

            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val soundUri = getSoundUri(context, soundName)

            Log.d("PrayerAlarm", "Ses URI: $soundUri")

            // Audio focus al
            val focusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
                when (focusChange) {
                    AudioManager.AUDIOFOCUS_LOSS -> {
                        Log.d("PrayerAlarm", "Audio focus kaybedildi")
                        stopSoundOnly()
                    }
                }
            }

            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest =
                    AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                        )
                        .setOnAudioFocusChangeListener(focusChangeListener)
                        .build()

                audioManager?.requestAudioFocus(audioFocusRequest!!)
                    ?: AudioManager.AUDIOFOCUS_REQUEST_FAILED
            } else {
                @Suppress("DEPRECATION")
                audioManager?.requestAudioFocus(
                    focusChangeListener,
                    AudioManager.STREAM_ALARM,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                ) ?: AudioManager.AUDIOFOCUS_REQUEST_FAILED
            }

            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Log.w("PrayerAlarm", "Audio focus alınamadı!")
            } else {
                Log.d("PrayerAlarm", "Audio focus alındı")
            }

            // MediaPlayer'ı kur ve başlat
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )

                setDataSource(context, soundUri)
                setVolume(1.0f, 1.0f)
                isLooping = true // Loop modu aktif

                setOnPreparedListener {
                    val durationMs = duration
                    Log.d("PrayerAlarm", "MediaPlayer hazır, süre: ${durationMs}ms, Loop: AÇIK")
                    start()
                    Log.d("PrayerAlarm", "Ses çalıyor ve 5 dakika boyunca loop modunda!")
                }

                setOnErrorListener { mp, what, extra ->
                    Log.e("PrayerAlarm", "MediaPlayer HATASI: what=$what, extra=$extra")
                    stopSoundOnly()
                    true
                }

                prepareAsync() // Asenkron hazırlık
            }

            // Otomatik durdurma - 5 dakika sonra
            autoStopHandler = Handler(Looper.getMainLooper())
            autoStopHandler?.postDelayed({
                Log.d("PrayerAlarm", "5 dakika doldu, ses otomatik durduruluyor")
                stopSound() // Hem ses hem bildirim kapat
            }, AUTO_STOP_DURATION)

            Log.d("PrayerAlarm", "Otomatik durdurma 5 dakika için ayarlandı")

        } catch (e: Exception) {
            Log.e("PrayerAlarm", "Ses çalma EXCEPTION", e)
        }
    }

    private fun getSoundUri(context: Context, soundName: String): Uri {
        // Sistem alarm sesi (daha uzun çalar)
        if (soundName == "system_ringtone") {
            return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }

        // Özel ses dosyası
        var resourceId = context.resources.getIdentifier(soundName, "raw", context.packageName)

        if (resourceId == 0) {
            Log.w("PrayerAlarm", "Ses bulunamadı: $soundName, alternatiflere bakılıyor")

            val alternatives = listOf(
                "kus_sesi",
                "ezan_mekke",
                "ezan_medine",
                "ezan_istanbul",
                "ezan_hafiz"
            )

            for (alt in alternatives) {
                resourceId = context.resources.getIdentifier(alt, "raw", context.packageName)
                if (resourceId != 0) {
                    Log.d("PrayerAlarm", "Alternatif ses bulundu: $alt")
                    break
                }
            }
        }

        return if (resourceId != 0) {
            Uri.parse("android.resource://${context.packageName}/$resourceId")
        } else {
            Log.w("PrayerAlarm", "Hiçbir ses bulunamadı, sistem alarm sesi kullanılıyor")
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }
    }

    private fun showNotification(
        context: Context,
        prayerName: String,
        prayerTime: String,
        vecize: String
    ) {
        // Uygulamayı aç intent'i
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("STOP_PRAYER_SOUND", true)
        }

        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Sesi durdur butonu
        val stopSoundIntent = Intent(context, StopSoundReceiver::class.java)
        val stopSoundPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopSoundIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Bildirim kaydırıldığında
        val dismissIntent = Intent(context, DismissNotificationReceiver::class.java)
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            dismissIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Vecizeyi paylaş butonu
        val shareIntent = Intent(context, ShareVecizeReceiver::class.java).apply {
            putExtra("vecize", vecize)
            putExtra("prayer_name", prayerName)
            putExtra("prayer_time", prayerTime)
        }
        val sharePendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            shareIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ok)
            .setContentTitle("$prayerName Vakti")
            .setContentText(vecize)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$vecize\n\nVakit: $prayerTime")
                    .setBigContentTitle("$prayerName Vakti")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentIntent(openAppPendingIntent)
            .setDeleteIntent(dismissPendingIntent)
            .setSound(null)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .addAction(
                R.drawable.ok,
                "Sesi Durdur",
                stopSoundPendingIntent
            )
            .addAction(
                android.R.drawable.ic_menu_share,
                "Paylaş",
                sharePendingIntent
            )
            .setFullScreenIntent(openAppPendingIntent, true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(currentNotificationId, notification)

        Log.d(
            "PrayerAlarm",
            "Bildirim gösterildi (expand): $prayerName (ID: $currentNotificationId)"
        )
    }
}

class OpenAppAndStopSoundReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("OpenAppAndStopSound", "Bildirime basıldı")

        // Önce sesi durdur
        PrayerTimeAlarmReceiver.stopSound()

        // Sonra uygulamayı aç
        val appIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(appIntent)
    }
}

class StopSoundReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("StopSoundReceiver", "Sesi Durdur butonu basıldı")

        // Sadece sesi durdur, bildirimi kapat
        PrayerTimeAlarmReceiver.stopSound()
    }
}

class DismissNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("DismissReceiver", "Bildirim kaydırıldı")
        PrayerTimeAlarmReceiver.stopSound()
    }
}

class ShareVecizeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ShareReceiver", "Paylaş butonu basıldı")

        val vecize = intent.getStringExtra("vecize") ?: ""
        val prayerName = intent.getStringExtra("prayer_name") ?: ""
        val prayerTime = intent.getStringExtra("prayer_time") ?: ""

        val shareText = "$prayerName Vakti - $prayerTime\n\n$vecize\n\n#NamazVakti #RisaleiNur"

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(shareIntent)
            Log.d("ShareReceiver", "Paylaşım başlatıldı")
        } catch (e: Exception) {
            Log.e("ShareReceiver", "Paylaşım hatası", e)
        }
    }
}