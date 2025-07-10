package com.example.risaleezan

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NamazFragment : Fragment() {

    private val TAG = "NamazFragment"

    private lateinit var textViewCityName: TextView
    private lateinit var textViewDate: TextView
    private lateinit var textViewNextPrayerName: TextView
    private lateinit var textViewCountdown: TextView
    private lateinit var recyclerViewPrayerTimes: RecyclerView
    private lateinit var prayerTimesAdapter: PrayerTimesAdapter
    private lateinit var homeProgressBar: ProgressBar
    private lateinit var prayerInfoLayout: View
    private lateinit var textViewVecize: TextView
    private lateinit var imageViewShare: ImageView
    private var countDownTimer: CountDownTimer? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_namaz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)

        lifecycleScope.launch {
            CityDataProvider.loadCities(requireContext().applicationContext)
        }

        sharedViewModel.selectedLocation.observe(viewLifecycleOwner) { location ->
            location?.let {
                if (it.latitude != null && it.longitude != null) {
                    loadPrayerTimes(it)
                }
            }
        }

        if (sharedViewModel.selectedLocation.value == null) {
            sharedViewModel.selectLocation(SelectedLocation("İzmir", "Turkey", 38.4192, 27.1287))
        }

        setupVecize()
    }

    private fun initializeViews(view: View) {
        textViewCityName = view.findViewById(R.id.textViewCityName)
        textViewDate = view.findViewById(R.id.textViewDate)
        textViewNextPrayerName = view.findViewById(R.id.textViewNextPrayerName)
        textViewCountdown = view.findViewById(R.id.textViewCountdown)
        recyclerViewPrayerTimes = view.findViewById(R.id.recyclerViewPrayerTimes)
        homeProgressBar = view.findViewById(R.id.homeProgressBar)
        prayerInfoLayout = view.findViewById(R.id.prayer_info_layout)
        textViewVecize = view.findViewById(R.id.textViewVecize)
        imageViewShare = view.findViewById(R.id.imageViewShare)
        textViewVecize.movementMethod = ScrollingMovementMethod()
        prayerTimesAdapter = PrayerTimesAdapter(mutableListOf())
        recyclerViewPrayerTimes.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewPrayerTimes.adapter = prayerTimesAdapter
    }

    private fun setupVecize() {
        val quotes = resources.getStringArray(R.array.risale_quotes)
        val randomQuote = quotes.random()
        textViewVecize.text = randomQuote
        imageViewShare.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, randomQuote)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Vecizeyi Paylaş"))
        }
    }

    private fun loadPrayerTimes(location: SelectedLocation) {
        homeProgressBar.visibility = View.VISIBLE
        prayerInfoLayout.visibility = View.INVISIBLE

        val cachedData = loadCache(location)
        if (cachedData != null) {
            processMonthlyData(cachedData, location)
        } else {
            fetchMonthlyPrayerTimesFromInternet(location)
        }
    }

    private fun fetchMonthlyPrayerTimesFromInternet(location: SelectedLocation) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val method = if (location.country.equals("Turkey", ignoreCase = true)) 13 else 3
        val url = "https://api.aladhan.com/v1/calendar/$year/$month?latitude=${location.latitude}&longitude=${location.longitude}&method=$method"

        val queue = Volley.newRequestQueue(requireContext(), OkHttpStack())
        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                saveCache(location, response.toString())
                processMonthlyData(response, location)
            },
            { error ->
                showErrorDialog("Veri Alınamadı", error.toString())
                homeProgressBar.visibility = View.GONE
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("User-Agent" to "Mozilla/5.0")
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun saveCache(location: SelectedLocation, jsonData: String) {
        try {
            val file = File(requireContext().filesDir, getCacheFileName(location))
            file.writeText(jsonData)
        } catch (e: Exception) {
            Log.e(TAG, "Cache kaydedilirken hata", e)
        }
    }

    private fun loadCache(location: SelectedLocation): JSONObject? {
        return try {
            val file = File(requireContext().filesDir, getCacheFileName(location))
            if (file.exists()) JSONObject(file.readText()) else null
        } catch (e: Exception) {
            null
        }
    }

    private fun getCacheFileName(location: SelectedLocation): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val city = location.city.lowercase(Locale.ROOT).replace(" ", "_")
        val country = location.country.lowercase(Locale.ROOT).replace(" ", "_")
        val method = if (location.country.equals("Turkey", ignoreCase = true)) 13 else 3
        return "vakitler_${city}_${country}_${year}_${month}_method$method.json"
    }

    private fun processMonthlyData(monthlyData: JSONObject, location: SelectedLocation) {
        try {
            val dataArray = monthlyData.getJSONArray("data")
            val todayDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val todayObject = dataArray.optJSONObject(todayDayOfMonth - 1) ?: return

            val timings = todayObject.getJSONObject("timings")
            val dateData = todayObject.getJSONObject("date")

            textViewCityName.text = location.city
            updateDateTexts(dateData.getJSONObject("gregorian"), dateData.getJSONObject("hijri"))
            updatePrayerTimesList(timings)
            startCountdown(timings)
            scheduleAlarmsForToday(timings)

            prayerInfoLayout.visibility = View.VISIBLE
            homeProgressBar.visibility = View.GONE
        } catch (e: Exception) {
            showErrorDialog("Veri İşleme Hatası", e.message)
            homeProgressBar.visibility = View.GONE
        }
    }

    private fun showErrorDialog(title: String, message: String?) {
        if (isAdded) {
            AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage("Lütfen internet bağlantınızı kontrol edin.\n\nHata:\n$message")
                .setPositiveButton("Tamam", null)
                .show()
        }
    }

    private fun updateDateTexts(gregorianData: JSONObject, hijriData: JSONObject) {
        val gregorianDay = gregorianData.getString("day")
        val gregorianMonth = gregorianData.getJSONObject("month").getString("en")
        val gregorianYear = gregorianData.getString("year")
        val hijriDay = hijriData.getString("day")
        val hijriMonth = hijriData.getJSONObject("month").getString("en")
        val hijriYear = hijriData.getString("year")

        val monthMap = mapOf(
            "January" to "Ocak", "February" to "Şubat", "March" to "Mart", "April" to "Nisan",
            "May" to "Mayıs", "June" to "Haziran", "July" to "Temmuz", "August" to "Ağustos",
            "September" to "Eylül", "October" to "Ekim", "November" to "Kasım", "December" to "Aralık"
        )
        val formattedGregorianDate = "$gregorianDay ${monthMap[gregorianMonth] ?: gregorianMonth} $gregorianYear"
        val formattedHijriDate = "$hijriDay $hijriMonth $hijriYear"

        textViewDate.text = "$formattedGregorianDate\n$formattedHijriDate"
    }

    private fun updatePrayerTimesList(timings: JSONObject) {
        val prayerList = mutableListOf<PrayerTime>()
        prayerList.add(PrayerTime("SABAH", timings.getString("Fajr").substringBefore(" ")))
        prayerList.add(PrayerTime("GÜNEŞ", timings.getString("Sunrise").substringBefore(" ")))
        prayerList.add(PrayerTime("ÖĞLE", timings.getString("Dhuhr").substringBefore(" ")))
        prayerList.add(PrayerTime("İKİNDİ", timings.getString("Asr").substringBefore(" ")))
        prayerList.add(PrayerTime("AKŞAM", timings.getString("Maghrib").substringBefore(" ")))
        prayerList.add(PrayerTime("YATSI", timings.getString("Isha").substringBefore(" ")))
        prayerTimesAdapter.updateData(prayerList)
    }

    private fun startCountdown(timings: JSONObject) {
        countDownTimer?.cancel()
        val prayerTimes = mapOf(
            "SABAH" to timings.getString("Fajr").substringBefore(" "),
            "GÜNEŞ" to timings.getString("Sunrise").substringBefore(" "),
            "ÖĞLE" to timings.getString("Dhuhr").substringBefore(" "),
            "İKİNDİ" to timings.getString("Asr").substringBefore(" "),
            "AKŞAM" to timings.getString("Maghrib").substringBefore(" "),
            "YATSI" to timings.getString("Isha").substringBefore(" ")
        )

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Calendar.getInstance()

        var nextPrayerName: String? = null
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

        if (nextPrayerName == null) {
            val firstPrayerEntry = prayerTimes.entries.sortedBy { it.value }.first()
            nextPrayerName = firstPrayerEntry.key
            val prayerCal = Calendar.getInstance()
            val prayerTime = timeFormat.parse(firstPrayerEntry.value) ?: return
            prayerCal.time = prayerTime
            prayerCal.add(Calendar.DAY_OF_MONTH, 1)
            nextPrayerTime = prayerCal
        }

        val millisUntilFinished = nextPrayerTime!!.timeInMillis - now.timeInMillis
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
                        loadPrayerTimes(currentLocation)
                    }
                }, 5000)
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }

    private fun scheduleAlarmsForToday(timings: JSONObject) {
        val context = requireContext()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.w(TAG, "SCHEDULE_EXACT_ALARM izni verilmemiş.")
            return
        }

        val prayerTimesToSchedule = mapOf(
            "Fajr" to timings.getString("Fajr"),
            "Sunrise" to timings.getString("Sunrise"),
            "Dhuhr" to timings.getString("Dhuhr"),
            "Asr" to timings.getString("Asr"),
            "Maghrib" to timings.getString("Maghrib"),
            "Isha" to timings.getString("Isha")
        )

        val prayerNamesTurkish = mapOf(
            "Fajr" to "Sabah",
            "Sunrise" to "Güneş",
            "Dhuhr" to "Öğle",
            "Asr" to "İkindi",
            "Maghrib" to "Akşam",
            "Isha" to "Yatsı"
        )

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val today = Calendar.getInstance()

        for ((prayerKey, timeStr) in prayerTimesToSchedule) {
            val turkishName = prayerNamesTurkish[prayerKey] ?: continue

            val prayerTime = timeFormat.parse(timeStr.substringBefore(" ")) ?: continue
            val calendar = Calendar.getInstance().apply {
                time = prayerTime
                set(Calendar.YEAR, today.get(Calendar.YEAR))
                set(Calendar.MONTH, today.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (calendar.before(Calendar.getInstance())) continue

            val requestCode = turkishName.hashCode()
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("PRAYER_NAME", turkishName)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                Log.d(TAG, "$turkishName için alarm kuruldu: ${calendar.time}")
            } catch (e: SecurityException) {
                Log.e(TAG, "Alarm kurma izni reddedildi.", e)
            }
        }
    }
}
