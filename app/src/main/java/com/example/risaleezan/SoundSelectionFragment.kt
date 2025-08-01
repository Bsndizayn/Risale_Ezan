package com.example.risaleezan

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SoundSelectionFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private var prayerName: String? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var adapter: SoundAdapter

    private val prayerNameMap = mapOf(
        "Imsak" to "İmsak", "Gunes" to "Güneş", "Ogle" to "Öğle",
        "Ikindi" to "İkindi", "Aksam" to "Akşam", "Yatsi" to "Yatsı"
    )

    private val soundMap = mapOf(
        "Sessiz" to -1,
        "Bip" to R.raw.bip,
        "Kuş Sesi" to R.raw.kus_sesi,
        "Cami Ezanı (YÜKSEK SES)" to R.raw.ezan,
        "İsmail Coşar (Sabah)" to R.raw.ismail_cosar_turkiye,
        "Ali Tel (Akşam)" to R.raw.ali_tel_turkiye,
        "Azzam Dweik Mescidi Aksa (Öğle)" to R.raw.azzam_dweik_palestine,
        "Egzon İbrahimi Kosova (Akşam)" to R.raw.egzon_ibrahimi_kosova,
        "Ghofar Zaen Endenozya (İkindi)" to R.raw.ghofar_zaen_indonesia,
        "Mansor Az Zahrani Mekke (Sabah)" to R.raw.mansor_az_zahrani_mekke,
        "Mehdi Yarrahi İran (Sabah)" to R.raw.mehdi_yarrahi_iran,
        "Mishary Rashid Alafasy Kuveyt (Yatsı)" to R.raw.mishary_rashid_kuveyt,
        "Muhammad Majid Hakim Medine (Yatsı)" to R.raw.muhammad_majid_hakim_medine,
        "Ruli Maroya Endenozya (Akşam)" to R.raw.ruli_maroya_indonesia,
        "Salim Bahanan Kamerun (Öğle)" to R.raw.salim_bahanan_endonezya
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prayerName = arguments?.getString("prayer_name")
        prefs = requireActivity().getSharedPreferences("PrayerTimeSettings", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sound_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prayerNameKey = prayerName ?: return
        val currentSoundId = prefs.getInt("sound_res_id_$prayerNameKey", R.raw.ezan)

        val titleTextView = view.findViewById<TextView>(R.id.textViewSoundSelectionTitle)
        titleTextView.text = "${prayerNameMap[prayerNameKey]} Sesi Seç"

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewSounds)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val soundList = soundMap.entries.map { it.key to it.value }

        adapter = SoundAdapter(soundList, currentSoundId) { selectedSound ->
            playSelectedSound(selectedSound.second)
        }
        recyclerView.adapter = adapter

        val buttonComplete = view.findViewById<Button>(R.id.buttonComplete)
        buttonComplete.setOnClickListener {
            stopPlayback()
            val selectedSound = adapter.getSelectedSound()
            if (selectedSound != null) {
                prefs.edit().putInt("sound_res_id_$prayerNameKey", selectedSound.second).apply()
                Toast.makeText(context, "${selectedSound.first} seçildi", Toast.LENGTH_SHORT).show()
            }
            findNavController().popBackStack()
        }
    }

    private fun playSelectedSound(soundResId: Int) {
        stopPlayback()
        if (soundResId != -1) {
            mediaPlayer = MediaPlayer.create(context, soundResId)
            mediaPlayer?.setOnCompletionListener { mp ->
                mp.release()
                mediaPlayer = null
            }
            mediaPlayer?.start()
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onStop() {
        super.onStop()
        stopPlayback()
    }
}