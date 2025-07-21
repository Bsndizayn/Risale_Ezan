package com.example.risaleezan

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

// ✅ GÜNCELLEME: OnNotificationToggleListener arayüzü, sınıf dışına taşındı
interface OnNotificationToggleListener {
    fun onToggleSound(prayerNameEnglish: String) // İngilizce isimle geri bildirim yapacak
}

data class PrayerTime(val name: String, val time: String)

class PrayerTimesAdapter(private var prayerTimes: List<PrayerTime>) :
    RecyclerView.Adapter<PrayerTimesAdapter.ViewHolder>() {

    private lateinit var prefs: SharedPreferences
    private val PREFS_NAME = "PrayerTimeSettings"

    // ✅ GÜNCELLEME: Geri çağırım dinleyicisi
    var onNotificationToggleListener: OnNotificationToggleListener? = null

    // Bildirim Ayarları ekranında kullanılan ses map'i (NamazFragment ile uyumlu olması için)
    private val soundMap = mapOf(
        "Sessiz" to -1,
        "Bip" to R.raw.bip,
        "Kuş Sesi" to R.raw.kus_sesi,
        "Cami Ezanı (YÜKSEK SES)" to R.raw.ezan,
        "İsmail Coşar (Sabah)" to R.raw.ismail_cosar_turkiye,
        "Ali Tel (Akşam)" to R.raw.ali_tel_turkiye,
        "Azzam Dweik  Mescidi Aksa (Öğle)" to R.raw.azzam_dweik_palestine,
        "Egzon İbrahimi Kosova (Akşam)" to R.raw.egzon_ibrahimi_kosova,
        "Ghofar Zaen Endenozya (İkindi)" to R.raw.ghofar_zaen_indonesia,
        "Mansor Az Zahrani Mekke (Sabah)" to R.raw.mansor_az_zahrani_mekke,
        "Mehdi Yarrahi İran (Sabah)" to R.raw.mehdi_yarrahi_iran,
        "Mishary Rashid Alafasy Kuveyt (Yatsı)" to R.raw.mishary_rashid_kuveyt,
        "Muhammad Majid Hakim Medine (Yatsı)" to R.raw.muhammad_majid_hakim_medine,
        "Ruli Maroya Endenozya (Akşam)" to R.raw.ruli_maroya_indonesia,
        "Salim Bahanan Kamerun (Öğle)" to R.raw.salim_bahanan_endonezya
    )

    // Namaz vakti İngilizce isimlerini SharedPreferences'ta kullanılan Türkçe karşılıklarına çevir
    private val englishToTurkishPrayerKeyMap = mapOf(
        "SABAH" to "Imsak",
        "GÜNEŞ" to "Gunes",
        "ÖĞLE" to "Ogle",
        "İKİNDİ" to "Ikindi",
        "AKŞAM" to "Aksam",
        "YATSI" to "Yatsi"
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val prayerName: TextView = view.findViewById(R.id.textViewPrayerNameItem)
        val prayerTime: TextView = view.findViewById(R.id.textViewPrayerTimeItem)
        val notificationIcon: ImageView = view.findViewById(R.id.imageViewNotificationToggle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        prefs = parent.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.prayer_time_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val prayer = prayerTimes[position]
        holder.prayerName.text = prayer.name
        holder.prayerTime.text = prayer.time

        val sharedPrefsKey = englishToTurkishPrayerKeyMap[prayer.name] ?: prayer.name
        val currentSoundId = prefs.getInt("sound_res_id_$sharedPrefsKey", R.raw.ezan)

        updateIcon(holder.notificationIcon, currentSoundId)

        holder.notificationIcon.setOnClickListener {
            val editor = prefs.edit()

            val newSoundId: Int
            if (currentSoundId == -1) { // Eğer şu an sessizse, sesi aç
                val lastNonSilentSound = prefs.getInt("last_non_silent_sound_res_id_$sharedPrefsKey", R.raw.ezan)
                newSoundId = lastNonSilentSound
            } else { // Eğer şu an sesliyse, sessize al
                editor.putInt("last_non_silent_sound_res_id_$sharedPrefsKey", currentSoundId)
                newSoundId = -1
            }
            editor.putInt("sound_res_id_$sharedPrefsKey", newSoundId)
            editor.apply()

            updateIcon(holder.notificationIcon, newSoundId)

            // ✅ HATA DÜZELTME: 'when' ifadesindeki söz dizimi hatası giderildi (to yerine ->)
            val apiPrayerKey = when (prayer.name) {
                "SABAH" -> "Fajr"
                "GÜNEŞ" -> "Sunrise"
                "ÖĞLE" -> "Dhuhr"
                "İKİNDİ" -> "Asr"
                "AKŞAM" -> "Maghrib"
                "YATSI" -> "Isha"
                else -> prayer.name
            }
            onNotificationToggleListener?.onToggleSound(apiPrayerKey)
        }
    }

    // İkonu ses durumuna göre güncelleyen fonksiyon
    private fun updateIcon(imageView: ImageView, soundId: Int) {
        if (soundId == -1) { // Sessiz
            // ✅ HATA DÜZELTME: Unresolved reference 'ic_notifications_off'
            imageView.setImageResource(R.drawable.ic_notifications_off)
            imageView.setColorFilter(ContextCompat.getColor(imageView.context, R.color.off_white))
        } else { // Sesli
            // ✅ HATA DÜZELTME: Unresolved reference 'ic_notifications_on'
            imageView.setImageResource(R.drawable.ic_notifications_on)
            imageView.setColorFilter(ContextCompat.getColor(imageView.context, R.color.light_gold))
        }
    }

    override fun getItemCount() = prayerTimes.size

    fun updateData(newPrayerTimes: List<PrayerTime>) {
        (this.prayerTimes as MutableList<PrayerTime>).clear()
        (this.prayerTimes as MutableList<PrayerTime>).addAll(newPrayerTimes)
        notifyDataSetChanged()
    }

    private fun String.capitalizeAsCustom(): String {
        return this.replace("İ", "I")
            .replace("Ğ", "G")
            .replace("Ş", "S")
            .lowercase(Locale.getDefault())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}