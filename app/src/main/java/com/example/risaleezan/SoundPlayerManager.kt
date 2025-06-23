package com.example.risaleezan

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log

object SoundPlayerManager {

    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context, soundResId: Int, onCompletion: () -> Unit) {
        // Eğer başka bir ses çalıyorsa önce onu durdur
        stop()

        if (soundResId == -1) {
            Log.d("SoundPlayerManager", "Ses 'Sessiz' olarak ayarlı, çalınmayacak.")
            onCompletion()
            return
        }

        try {
            val soundUri = Uri.parse("android.resource://${context.packageName}/$soundResId")
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, soundUri)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setAudioAttributes(audioAttributes)
                prepare()
                start()
                setOnCompletionListener {
                    Log.d("SoundPlayerManager", "Ses çalması bitti.")
                    stop() // İş bitince kaynakları temizle
                    onCompletion()
                }
                setOnErrorListener { _, _, _ ->
                    Log.e("SoundPlayerManager", "MediaPlayer hata verdi.")
                    stop() // Hata durumunda da kaynakları temizle
                    onCompletion()
                    true
                }
            }
        } catch (e: Exception) {
            Log.e("SoundPlayerManager", "MediaPlayer başlatılamadı.", e)
            onCompletion()
        }
    }

    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            Log.d("SoundPlayerManager", "MediaPlayer durduruldu ve kaynaklar serbest bırakıldı.")
        }
        mediaPlayer = null
    }
}