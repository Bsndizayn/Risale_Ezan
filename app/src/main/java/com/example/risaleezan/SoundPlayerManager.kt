package com.example.risaleezan

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log

object SoundPlayerManager {
    // Ezan çalma sorumluluğu EzanPlaybackService'e taşındığı için,
    // bu obje artık alarm sesleri için doğrudan kullanılmayacak.
    // Ancak başka yerlerde kullanılabileceği varsayılarak boş bir fonksiyon bırakılabilir.
    // Eğer bu obje sadece alarm sesleri için kullanılıyorsa, bu dosya kaldırılabilir.

    // Şu anki durumda, AlarmReceiver tarafından doğrudan çağrılmayacaktır.
    // Diğer yerlerde kullanılıyorsa mevcut haliyle kalabilir.
    // Bu versiyon, AlarmReceiver'dan çağrılmayacağını varsayarak basitleştirilmiştir.
    private var mediaPlayer: MediaPlayer? = null // Eğer başka bir yerde kullanılmayacaksa kaldırılabilir

    fun play(context: Context, soundResId: Int, onCompletion: () -> Unit) {
        // Bu fonksiyon artık alarm çalma döngüsünde kullanılmıyor,
        // EzanPlaybackService sorumluluğu üstlendi.
        // Eğer bu obje başka yerlerde ses çalmak için hala kullanılıyorsa,
        // eski içeriğini korumalısınız.
        Log.d("SoundPlayerManager", "play() çağrıldı, ancak EzanPlaybackService sorumluluğu üstlendi.")
        onCompletion() // Hemen tamamlandığını bildir
    }

    fun stop() {
        // Bu fonksiyon da EzanPlaybackService tarafından yönetildiği için
        // artık AlarmReceiver veya MuteActionReceiver tarafından çağrılmayacak.
        // Eğer başka bir yerde kullanılıyorsa mevcut haliyle kalabilir.
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