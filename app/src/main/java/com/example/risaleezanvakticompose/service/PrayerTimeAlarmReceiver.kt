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
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.example.risaleezanvakticompose.MainActivity
import com.example.risaleezanvakticompose.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

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

        fun stopSound() {
            try {
                android.util.Log.d("PrayerAlarm", "Ses durdurma çağrıldı")

                mediaPlayer?.apply {
                    if (isPlaying) {
                        stop()
                        android.util.Log.d("PrayerAlarm", "MediaPlayer durduruldu")
                    }
                    release()
                }
                mediaPlayer = null

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

                mediaSession?.apply {
                    isActive = false
                    release()
                }
                mediaSession = null

                context?.let { ctx ->
                    val notificationManager =
                        ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(currentNotificationId)
                }

            } catch (e: Exception) {
                android.util.Log.e("PrayerAlarm", "Ses durdurma hatası", e)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: return
        val prayerTime = intent.getStringExtra(EXTRA_PRAYER_TIME) ?: return
        val soundName = intent.getStringExtra(EXTRA_SOUND_NAME) ?: "default_ezan"

        android.util.Log.d("PrayerAlarm", "Alarm çaldı: $prayerName, Ses: $soundName")

        Companion.context = context.applicationContext

        val pendingResult = goAsync()

        scope.launch {
            try {
                createNotificationChannel(context)
                currentNotificationId = prayerName.hashCode()
                setupMediaSession(context)
                playPrayerSound(context, soundName)
                showNotification(context, prayerName, prayerTime)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun setupMediaSession(context: Context) {
        try {
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
                        android.util.Log.d("PrayerAlarm", "MediaSession: Stop")
                        stopSound()
                    }

                    override fun onPause() {
                        android.util.Log.d("PrayerAlarm", "MediaSession: Pause")
                        stopSound()
                    }

                    override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                        android.util.Log.d("PrayerAlarm", "MediaSession: Button event")
                        stopSound()
                        return true
                    }
                })

                isActive = true
            }
            android.util.Log.d("PrayerAlarm", "MediaSession kuruldu")
        } catch (e: Exception) {
            android.util.Log.e("PrayerAlarm", "MediaSession hatası", e)
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH // MAX yerine HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Namaz vakitleri için bildirimler"
                enableLights(true)
                lightColor = android.graphics.Color.GREEN
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500) // Titreşim deseni
                setSound(null, null) // Ezan sesi zaten MediaPlayer'dan çalıyor
                setShowBadge(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            android.util.Log.d("PrayerAlarm", "Notification channel oluşturuldu")
        }
    }

    private fun playPrayerSound(context: Context, soundName: String) {
        try {
            android.util.Log.d("PrayerAlarm", "Ses çalmaya başlanıyor: $soundName")

            stopSound()

            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val soundUri = getSoundUri(context, soundName)

            val focusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
                android.util.Log.d("PrayerAlarm", "Audio focus değişti: $focusChange")
                when (focusChange) {
                    AudioManager.AUDIOFOCUS_LOSS,
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                        android.util.Log.d("PrayerAlarm", "Focus kaybedildi, ses durduruluyor")
                        stopSound()
                    }

                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                        android.util.Log.d("PrayerAlarm", "Duck durumu, ses durduruluyor")
                        stopSound()
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
                        .setAcceptsDelayedFocusGain(false)
                        .setWillPauseWhenDucked(false)
                        .build()

                audioManager?.requestAudioFocus(audioFocusRequest!!)
            } else {
                @Suppress("DEPRECATION")
                audioManager?.requestAudioFocus(
                    focusChangeListener,
                    AudioManager.STREAM_ALARM,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                )
            }

            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                android.util.Log.w("PrayerAlarm", "Audio focus alınamadı!")
            } else {
                android.util.Log.d("PrayerAlarm", "Audio focus alındı")
            }

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )

                setDataSource(context, soundUri)
                setVolume(1.0f, 1.0f)
                prepare()
                start()

                android.util.Log.d("PrayerAlarm", "Ses çalmaya başladı, süre: ${duration}ms")

                setOnCompletionListener {
                    android.util.Log.d("PrayerAlarm", "Ses bitti")
                    stopSound()
                }

                setOnErrorListener { mp, what, extra ->
                    android.util.Log.e(
                        "PrayerAlarm",
                        "MediaPlayer hatası: what=$what, extra=$extra"
                    )
                    stopSound()
                    true
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("PrayerAlarm", "Ses çalma hatası", e)
        }
    }

    private fun getSoundUri(context: Context, soundName: String): Uri {
        var resourceId = context.resources.getIdentifier(soundName, "raw", context.packageName)

        if (resourceId == 0) {
            val alternatives =
                listOf("ezan_mekke", "ezan_medine", "ezan_istanbul", "ezan_hafiz", "default_ezan")
            for (alt in alternatives) {
                resourceId = context.resources.getIdentifier(alt, "raw", context.packageName)
                if (resourceId != 0) {
                    android.util.Log.d("PrayerAlarm", "Alternatif ses bulundu: $alt")
                    break
                }
            }
        }

        return if (resourceId != 0) {
            Uri.parse("android.resource://${context.packageName}/$resourceId")
        } else {
            android.util.Log.w("PrayerAlarm", "Hiçbir ezan sesi bulunamadı")
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }
    }

    private fun showNotification(context: Context, prayerName: String, prayerTime: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopSoundIntent = Intent(context, StopSoundReceiver::class.java)
        val stopSoundPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopSoundIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val dismissIntent = Intent(context, DismissNotificationReceiver::class.java)
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            dismissIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ok)
            .setContentTitle("$prayerName Vakti")
            .setContentText("Vakit: $prayerTime")
            .setPriority(NotificationCompat.PRIORITY_MAX) // En yüksek öncelik
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setOngoing(true) // false yerine true - kullanıcı kapatana kadar kalır
            .setContentIntent(pendingIntent)
            .setDeleteIntent(dismissPendingIntent)
            .setSound(null)
            .setVibrate(longArrayOf(0, 500, 200, 500)) // Titreşim ekle
            .addAction(
                R.drawable.ok,
                "Sesi Durdur",
                stopSoundPendingIntent
            )
            .setFullScreenIntent(pendingIntent, true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Kilit ekranında göster
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(prayerName.hashCode(), notification)

        android.util.Log.d("PrayerAlarm", "Bildirim gösterildi: $prayerName")
    }

}

class StopSoundReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        android.util.Log.d("StopSoundReceiver", "Buton basıldı")
        PrayerTimeAlarmReceiver.stopSound()
    }
}

class DismissNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        android.util.Log.d("DismissReceiver", "Bildirim kaydırıldı")
        PrayerTimeAlarmReceiver.stopSound()
    }
}