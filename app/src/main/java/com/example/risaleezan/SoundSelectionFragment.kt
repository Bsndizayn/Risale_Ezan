package com.example.risaleezan

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class SoundSelectionFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private var prayerName: String? = null

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

    companion object {
        private const val ARG_PRAYER_NAME = "prayer_name"
        fun newInstance(prayerName: String): SoundSelectionFragment {
            val fragment = SoundSelectionFragment()
            val args = Bundle()
            args.putString(ARG_PRAYER_NAME, prayerName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prayerName = arguments?.getString(ARG_PRAYER_NAME)
        prefs = requireActivity().getSharedPreferences("PrayerTimeSettings", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sound_selection, container, false)
        val listView = view.findViewById<ListView>(R.id.listViewSounds)

        val soundNames = soundMap.keys.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_single_choice, soundNames)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        val currentSoundId = prefs.getInt("sound_res_id_$prayerName", R.raw.ezan)
        val checkedItem = soundMap.values.indexOf(currentSoundId).let { if (it == -1) 0 else it }
        listView.setItemChecked(checkedItem, true)

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedSoundName = soundNames[position]
            val selectedSoundId = soundMap[selectedSoundName] ?: R.raw.ezan
            prefs.edit().putInt("sound_res_id_$prayerName", selectedSoundId).apply()
            Log.d("SoundSelection", "$prayerName için '$selectedSoundName' seçildi.")
            parentFragmentManager.popBackStack()
        }

        val titleTextView = view.findViewById<TextView>(R.id.textViewSoundSelectionTitle)
        titleTextView.text = "$prayerName Sesi Seç"

        return view
    }
}
