package com.example.risaleezan

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.util.Locale

class BildirimAyarlariFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private val prayerNames = listOf("Imsak", "Gunes", "Ogle", "Ikindi", "Aksam", "Yatsi")
    private val prayerNameMap = mapOf(
        "Imsak" to "İmsak",
        "Gunes" to "Güneş",
        "Ogle" to "Öğle",
        "Ikindi" to "İkindi",
        "Aksam" to "Akşam",
        "Yatsi" to "Yatsı"
    )

    private lateinit var adjustmentValues: IntArray

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bildirim_ayarlari, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("PrayerTimeSettings", Context.MODE_PRIVATE)

        adjustmentValues = resources.getStringArray(R.array.time_adjustment_values).map { it.toInt() }.toIntArray()

        prayerNames.forEach { prayerName ->
            val soundIconId = resources.getIdentifier("imageView${prayerName}Sound", "id", requireContext().packageName)
            val spinnerId = resources.getIdentifier("spinner${prayerName}Adjustment", "id", requireContext().packageName)
            val textViewId = resources.getIdentifier("textView${prayerName}Sound", "id", requireContext().packageName)

            if (soundIconId != 0 && spinnerId != 0 && textViewId != 0) {
                setupPrayerTime(view, prayerName, soundIconId, spinnerId, textViewId)
            }
        }

        return view
    }

    private fun setupPrayerTime(view: View, prayerName: String, soundIconId: Int, spinnerId: Int, textViewId: Int) {
        val soundIcon = view.findViewById<ImageView>(soundIconId)
        val spinnerAdjustment = view.findViewById<Spinner>(spinnerId)
        val textViewSound = view.findViewById<TextView>(textViewId)

        updateSoundTextView(textViewSound, sharedPreferences.getInt("sound_res_id_$prayerName", R.raw.ezan))

        soundIcon.setOnClickListener {
            val bundle = Bundle().apply { putString("prayer_name", prayerName) }
            findNavController().navigate(R.id.soundSelectionFragment, bundle)
        }

        textViewSound.setOnClickListener {
            val bundle = Bundle().apply { putString("prayer_name", prayerName) }
            findNavController().navigate(R.id.soundSelectionFragment, bundle)
        }

        // --- DEĞİŞİKLİK BURADA: Renk mantığı kaldırıldı, standart adaptöre dönüldü ---
        val options = resources.getStringArray(R.array.time_adjustment_options)
        val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, options)
        adapter.setDropDownViewResource(R.layout.custom_spinner_item)
        spinnerAdjustment.adapter = adapter
        // --- DEĞİŞİKLİK SONU ---

        val savedAdjustment = sharedPreferences.getInt("adjustment_minutes_$prayerName", 0)
        val defaultSelectionIndex = adjustmentValues.indexOf(savedAdjustment)
        spinnerAdjustment.setSelection(if (defaultSelectionIndex != -1) defaultSelectionIndex else adjustmentValues.indexOf(0))

        spinnerAdjustment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedValue = adjustmentValues[position]
                sharedPreferences.edit().putInt("adjustment_minutes_$prayerName", selectedValue).apply()
                Log.d("BildirimAyarlari", "$prayerName zaman ayarı: $selectedValue dakika")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateSoundTextView(textView: TextView, soundId: Int) {
        val soundName = soundMap.entries.find { it.value == soundId }?.key ?: "Bilinmeyen"
        textView.text = soundName
    }
}