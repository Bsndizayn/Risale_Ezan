package com.example.risaleezan

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class BildirimAyarlariFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private val PREFS_NAME = "PrayerTimeSettings"

    // Key: Kullanıcının göreceği isim
    // Value: res/raw klasöründeki dosyanın ID'si
    // Verdiğiniz tabloya göre liste hazırlandı.
    private val soundMap = mapOf(
        "Sessiz" to -1,
        "Bip" to R.raw.bip,
        "Kuş Sesi" to R.raw.kus_sesi,
        "Cami Ezanı (YÜKSEK SES)" to R.raw.ezan,
        "İsmail Coşar (Sabah)" to R.raw.ismail_cosar_turkiye,
        "Ali Tel (Akşam)" to R.raw.ali_tel_turkiye,
        "Azzam Dweik  Mescidi Aksa (Öğle)" to R.raw.azzam_dweik_palestine,
        "Egzon İbrahimi Kosava (Akşam)" to R.raw.egzon_ibrahimi_kosova,
        "Ghofar Zaen Endenozya (İkindi)" to R.raw.ghofar_zaen_indonesia,
        "Mansor Az Zahrani Mekke (Sabah)" to R.raw.mansor_az_zahrani_mekke,
        "Mehdi Yarrahi İran (Sabah)" to R.raw.mehdi_yarrahi_iran,
        "Mishary Rashid Alafasy Kuveyt (Yatsı)" to R.raw.mishary_rashid_kuveyt,
        "Muhammad Majid Hakim Medine (Yatsı)" to R.raw.muhammad_majid_hakim_medine,
        "Ruli Maroya Endenozya (Akşam)" to R.raw.ruli_maroya_indonesia,
        "Salim Bahanan Kamerun (Öğle)" to R.raw.salim_bahanan_endonezya
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bildirim_ayarlari, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        setupPrayerTime(view, "Imsak", R.id.layoutImsak, R.id.textViewImsakSound)
        setupPrayerTime(view, "Gunes", R.id.layoutGunes, R.id.textViewGunesSound)
        setupPrayerTime(view, "Ogle", R.id.layoutOgle, R.id.textViewOgleSound)
        setupPrayerTime(view, "Ikindi", R.id.layoutIkindi, R.id.textViewIkindiSound)
        setupPrayerTime(view, "Aksam", R.id.layoutAksam, R.id.textViewAksamSound)
        setupPrayerTime(view, "Yatsi", R.id.layoutYatsi, R.id.textViewYatsiSound)
    }

    private fun setupPrayerTime(view: View, prayerName: String, layoutId: Int, textViewId: Int) {
        val layout = view.findViewById<RelativeLayout>(layoutId)
        val textView = view.findViewById<TextView>(textViewId)
        val preferenceKey = "sound_res_id_$prayerName"

        // Kayıtlı sesi yükle ve ekranda göster
        updateSoundTextView(textView, prefs.getInt(preferenceKey, R.raw.ezan)) // Varsayılan: Ezan

        layout.setOnClickListener {
            showSoundSelectionDialog(prayerName, textView, preferenceKey)
        }
    }

    private fun showSoundSelectionDialog(prayerName: String, textView: TextView, preferenceKey: String) {
        // Diyalogda gösterilecek isimleri al (Ali Tel (Turkiye), Bip vs.)
        val soundNames = soundMap.keys.toTypedArray()
        val currentSoundId = prefs.getInt(preferenceKey, R.raw.ezan)
        // Mevcut seçili olanın listedeki sırasını bul
        val checkedItem = soundMap.values.indexOf(currentSoundId).let { if (it == -1) 0 else it }

        AlertDialog.Builder(requireContext())
            .setTitle("$prayerName Sesi Seç")
            .setSingleChoiceItems(soundNames, checkedItem) { dialog, which ->
                val selectedSoundName = soundNames[which]
                val selectedSoundId = soundMap[selectedSoundName] ?: R.raw.ezan // ID'yi al

                // Seçilen sesin ID'sini kaydet
                prefs.edit().putInt(preferenceKey, selectedSoundId).apply()
                // Ekranda seçilen sesin açıklayıcı ismini göster
                updateSoundTextView(textView, selectedSoundId)

                dialog.dismiss()
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun updateSoundTextView(textView: TextView, soundId: Int) {
        // Kayıtlı olan ID'ye karşılık gelen açıklayıcı ismi bul ve ekrana yaz
        val soundName = soundMap.entries.find { it.value == soundId }?.key ?: "Bilinmeyen"
        textView.text = soundName
    }
}