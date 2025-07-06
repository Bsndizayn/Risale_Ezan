package com.example.risaleezan

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log

object SoundPlayerManager {
    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context, soundResId: Int, onCompletion: () -> Unit) {
        stop() // Mevcut sesi durdur

        if (soundResId == -1) {
            Log.d("SoundPlayerManager", "Ses 'Sessiz' olarak ayarlı, çalınmayacak.")
            onCompletion()
            return
        }

        try {
            // Raw resource'dan doğrudan MediaPlayer oluştur
            mediaPlayer = MediaPlayer.create(context, soundResId)

            if (mediaPlayer == null) {
                Log.e("SoundPlayerManager", "MediaPlayer oluşturulamadı: $soundResId")
                onCompletion()
                return
            }

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            mediaPlayer?.setAudioAttributes(audioAttributes)

            mediaPlayer?.setOnCompletionListener {
                Log.d("SoundPlayerManager", "Ses çalması bitti.")
                stop()
                onCompletion()
            }

            mediaPlayer?.setOnErrorListener { _, what, extra ->
                Log.e("SoundPlayerManager", "MediaPlayer hata: what=$what, extra=$extra")
                stop()
                onCompletion()
                true
            }

            mediaPlayer?.start()
            Log.d("SoundPlayerManager", "Ses çalmaya başladı: $soundResId")

        } catch (e: Exception) {
            Log.e("SoundPlayerManager", "MediaPlayer başlatılamadı.", e)
            stop()
            onCompletion()
        }
    }

    fun stop() {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
                Log.d("SoundPlayerManager", "MediaPlayer durduruldu ve kaynaklar serbest bırakıldı.")
            } catch (e: Exception) {
                Log.e("SoundPlayerManager", "MediaPlayer durdurulurken hata.", e)
            }
        }
        mediaPlayer = null
    }
}