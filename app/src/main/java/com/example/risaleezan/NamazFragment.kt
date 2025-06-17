package com.example.risaleezan // Paket adını kontrol et

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class NamazFragment : Fragment() {

    private lateinit var textViewCityName: TextView
    private lateinit var textViewDate: TextView
    private lateinit var textViewNextPrayerName: TextView
    private lateinit var textViewCountdown: TextView
    private lateinit var recyclerViewPrayerTimes: RecyclerView
    private lateinit var prayerTimesAdapter: PrayerTimesAdapter
    private lateinit var homeProgressBar: ProgressBar
    private lateinit var prayerInfoLayout: View
    private var countDownTimer: CountDownTimer? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_namaz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        displayCurrentDate()

        sharedViewModel.selectedLocation.observe(viewLifecycleOwner) { location ->
            fetchPrayerTimes(location)
        }

        if (sharedViewModel.selectedLocation.value == null) {
            sharedViewModel.selectLocation(SelectedLocation("İzmir", "Turkey"))
        }
    }

    private fun initializeViews(view: View) {
        textViewCityName = view.findViewById(R.id.textViewCityName)
        textViewDate = view.findViewById(R.id.textViewDate)
        textViewNextPrayerName = view.findViewById(R.id.textViewNextPrayerName)
        textViewCountdown = view.findViewById(R.id.textViewCountdown)
        recyclerViewPrayerTimes = view.findViewById(R.id.recyclerViewPrayerTimes)
        homeProgressBar = view.findViewById(R.id.homeProgressBar)
        prayerInfoLayout = view.findViewById(R.id.prayer_info_layout)

        prayerTimesAdapter = PrayerTimesAdapter(mutableListOf())
        recyclerViewPrayerTimes.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewPrayerTimes.adapter = prayerTimesAdapter
    }

    private fun displayCurrentDate() {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy, EEEE", Locale("tr"))
        textViewDate.text = dateFormat.format(Date())
    }

    private fun fetchPrayerTimes(location: SelectedLocation) {
        homeProgressBar.visibility = View.VISIBLE
        prayerInfoLayout.visibility = View.INVISIBLE
        val city = location.city
        val country = location.country
        textViewCityName.text = "$city, $country"

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        val url = "https://api.aladhan.com/v1/timingsByCity/$currentDate?city=$city&country=$country&method=2"

        val queue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val data = response.getJSONObject("data")
                    val timings = data.getJSONObject("timings")
                    startCountdown(timings)
                    updatePrayerTimesList(timings)
                    homeProgressBar.visibility = View.GONE
                    prayerInfoLayout.visibility = View.VISIBLE
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Veri işlenemedi.", Toast.LENGTH_LONG).show()
                    textViewNextPrayerName.text = "Hata"
                    homeProgressBar.visibility = View.GONE
                }
            },
            { error ->
                Toast.makeText(requireContext(), "İnternet bağlantısı kurulamadı.", Toast.LENGTH_LONG).show()
                textViewNextPrayerName.text = "Bağlantı Yok"
                homeProgressBar.visibility = View.GONE
            }
        )
        jsonObjectRequest.setShouldCache(false)
        queue.add(jsonObjectRequest)
    }

    private fun updatePrayerTimesList(timings: JSONObject) {
        val prayerList = mutableListOf<PrayerTime>()
        prayerList.add(PrayerTime("İMSAK", timings.getString("Imsak")))
        prayerList.add(PrayerTime("GÜNEŞ", timings.getString("Sunrise")))
        prayerList.add(PrayerTime("ÖĞLE", timings.getString("Dhuhr")))
        prayerList.add(PrayerTime("İKİNDİ", timings.getString("Asr")))
        prayerList.add(PrayerTime("AKŞAM", timings.getString("Maghrib")))
        prayerList.add(PrayerTime("YATSI", timings.getString("Isha")))
        prayerTimesAdapter.updateData(prayerList)
    }

    private fun startCountdown(timings: JSONObject) {
        countDownTimer?.cancel()
        val prayerTimes = mapOf(
            "İMSAK" to timings.getString("Imsak"), "GÜNEŞ" to timings.getString("Sunrise"),
            "ÖĞLE" to timings.getString("Dhuhr"), "İKİNDİ" to timings.getString("Asr"),
            "AKŞAM" to timings.getString("Maghrib"), "YATSI" to timings.getString("Isha")
        )
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Calendar.getInstance()
        var nextPrayerName = ""
        var nextPrayerTime: Calendar? = null

        for ((name, timeStr) in prayerTimes.entries.sortedBy { it.value }) {
            val prayerCal = Calendar.getInstance()
            val prayerTime = timeFormat.parse(timeStr) ?: continue
            prayerCal.time = prayerTime
            prayerCal.set(Calendar.YEAR, now.get(Calendar.YEAR))
            prayerCal.set(Calendar.MONTH, now.get(Calendar.MONTH))
            prayerCal.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
            if (prayerCal.after(now)) {
                nextPrayerName = name
                nextPrayerTime = prayerCal
                break
            }
        }

        if (nextPrayerTime == null) {
            nextPrayerName = "İMSAK"
            val imsakTimeStr = prayerTimes["İMSAK"]!!
            val prayerCal = Calendar.getInstance()
            val prayerTime = timeFormat.parse(imsakTimeStr) ?: return
            prayerCal.time = prayerTime
            prayerCal.add(Calendar.DAY_OF_MONTH, 1)
            nextPrayerTime = prayerCal
        }

        scheduleAlarm(nextPrayerTime.timeInMillis, nextPrayerName)
        val millisUntilFinished = nextPrayerTime.timeInMillis - now.timeInMillis
        textViewNextPrayerName.text = nextPrayerName

        countDownTimer = object : CountDownTimer(millisUntilFinished, 1000) {
            override fun onTick(millisLeft: Long) {
                val hours = TimeUnit.MILLISECONDS.toHours(millisLeft)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisLeft) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisLeft) % 60
                textViewCountdown.text = String.format("-%02d:%02d:%02d", hours, minutes, seconds)
            }
            override fun onFinish() {
                textViewCountdown.text = "Vakit Girdi!"
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    sharedViewModel.selectedLocation.value?.let { currentLocation ->
                        fetchPrayerTimes(currentLocation)
                    }
                }, 5000)
            }
        }.start()
    }

    private fun scheduleAlarm(timeInMillis: Long, prayerName: String) {
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("PRAYER_NAME", prayerName)
            val selectedSoundResourceId = R.raw.ezan
            putExtra("SOUND_RESOURCE_ID", selectedSoundResourceId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            prayerName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }
}