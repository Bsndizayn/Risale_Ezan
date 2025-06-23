package com.example.risaleezan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.provider.Settings
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

    private val TAG = "NamazDebug"

    // Diğer değişkenler...
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

    // lastFetchedCity değişkeni kaldırıldı.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_namaz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)

        // ViewModel'i dinleme mantığı sadeleştirildi.
        sharedViewModel.selectedLocation.observe(viewLifecycleOwner) { location ->
            location?.let {
                // Konum her değiştiğinde veriyi çek.
                fetchPrayerTimes(it)
            }
        }

        // Eğer başlangıçta bir şehir seçilmemişse, varsayılanı ayarla.
        if (sharedViewModel.selectedLocation.value == null) {
            sharedViewModel.selectLocation(SelectedLocation("İzmir", "Turkey"))
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


    private fun fetchPrayerTimes(location: SelectedLocation) {
        homeProgressBar.visibility = View.VISIBLE
        prayerInfoLayout.visibility = View.INVISIBLE

        val city = location.city
        textViewCityName.text = city

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        val url = "https://api.aladhan.com/v1/timingsByCity/$currentDate?city=$city&country=${location.country}&method=2"

        val queue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val data = response.getJSONObject("data")
                    val timings = data.getJSONObject("timings")
                    val meta = data.getJSONObject("meta")
                    val latitude = meta.getDouble("latitude")
                    val longitude = meta.getDouble("longitude")

                    // ViewModel'deki konumu koordinatlarla güncelle (Kıble için)
                    val updatedLocation = location.copy(latitude = latitude, longitude = longitude)
                    sharedViewModel.selectLocation(updatedLocation)

                    val dateData = data.getJSONObject("date")
                    val gregorianData = dateData.getJSONObject("gregorian")
                    val hijriData = dateData.getJSONObject("hijri")
                    val gregorianDay = gregorianData.getString("day")
                    val gregorianMonth = gregorianData.getJSONObject("month").getString("en")
                    val gregorianYear = gregorianData.getString("year")
                    val formattedGregorianDate = formatToTurkishDate(gregorianDay, gregorianMonth, gregorianYear)
                    val hijriDay = hijriData.getString("day")
                    val hijriMonth = hijriData.getJSONObject("month").getString("en")
                    val hijriYear = hijriData.getString("year")
                    val formattedHijriDate = "$hijriDay $hijriMonth $hijriYear"
                    textViewDate.text = "$formattedGregorianDate\n$formattedHijriDate"

                    scheduleAlarmsForToday(timings)
                    startCountdown(timings)
                    updatePrayerTimesList(timings)

                    homeProgressBar.visibility = View.GONE
                    prayerInfoLayout.visibility = View.VISIBLE
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Veri işlenemedi.", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(requireContext(), "İnternet bağlantısı kurulamadı.", Toast.LENGTH_LONG).show()
            }
        )
        jsonObjectRequest.setShouldCache(false)
        queue.add(jsonObjectRequest)
    }

    private fun scheduleAlarmsForToday(timings: JSONObject) {
        val context = requireContext()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                AlertDialog.Builder(context)
                    .setTitle("Alarm İzni Gerekli")
                    .setMessage("Bildirimlerin zamanında gelmesi için lütfen 'Alarmlar ve hatırlatıcılar' iznini verin.")
                    .setPositiveButton("Ayarlara Git") { _, _ ->
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        startActivity(intent)
                    }
                    .setNegativeButton("İptal", null)
                    .show()
                return
            }
        }

        val prayerTimesToSchedule = listOf("Imsak", "Dhuhr", "Asr", "Maghrib", "Isha")
        val prayerNamesTurkish = mapOf(
            "Imsak" to "İmsak", "Dhuhr" to "Öğle", "Asr" to "İkindi",
            "Maghrib" to "Akşam", "Isha" to "Yatsı"
        )
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        for ((index, prayer) in prayerTimesToSchedule.withIndex()) {
            val timeStr = timings.optString(prayer)
            if(timeStr.isNullOrEmpty()) continue
            val prayerTime = timeFormat.parse(timeStr) ?: continue

            val timeCalendar = Calendar.getInstance().apply { time = prayerTime }

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1)
            }

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("PRAYER_NAME", prayerNamesTurkish[prayer])
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                index,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            Log.d(TAG, "--> ${prayerNamesTurkish[prayer]} için alarm kuruldu: ${calendar.time}")
        }
    }

    // Diğer fonksiyonlarınız aynı kalacak...
    private fun formatToTurkishDate(day: String, month: String, year: String): String {
        val monthMap = mapOf("January" to "Ocak", "February" to "Şubat", "March" to "Mart", "April" to "Nisan", "May" to "Mayıs", "June" to "Haziran", "July" to "Temmuz", "August" to "Ağustos", "September" to "Eylül", "October" to "Ekim", "November" to "Kasım", "December" to "Aralık")
        val turkishMonth = monthMap[month] ?: month
        return "$day $turkishMonth $year"
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
        val prayerTimes = mapOf("İMSAK" to timings.getString("Imsak"), "ÖĞLE" to timings.getString("Dhuhr"), "İKİNDİ" to timings.getString("Asr"), "AKŞAM" to timings.getString("Maghrib"), "YATSI" to timings.getString("Isha"))
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Calendar.getInstance()
        var nextPrayerName = ""
        var nextPrayerTime: Calendar? = null
        val sortedPrayerTimes = prayerTimes.entries.sortedBy { it.value }
        for ((name, timeStr) in sortedPrayerTimes) {
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
            val firstPrayerEntry = sortedPrayerTimes.first()
            nextPrayerName = firstPrayerEntry.key
            val prayerCal = Calendar.getInstance()
            val prayerTime = timeFormat.parse(firstPrayerEntry.value) ?: return
            prayerCal.time = prayerTime
            prayerCal.add(Calendar.DAY_OF_MONTH, 1)
            nextPrayerTime = prayerCal
        }
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
    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }
}