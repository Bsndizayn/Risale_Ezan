package com.example.risaleezanvakticompose.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.risaleezanvakticompose.data.local.AppDatabase
import com.example.risaleezanvakticompose.data.repository.PrayerTimesRepository
import com.example.risaleezanvakticompose.util.PrayerAlarmManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class MidnightAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var repository: PrayerTimesRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val MIDNIGHT_ALARM_REQUEST_CODE = 9999

        fun scheduleMidnightAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, MidnightAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                MIDNIGHT_ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Yarın sabah 00:01'i hesapla
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 1)
                set(Calendar.SECOND, 0)

                // Eğer şu an 00:01'i geçmişsek, yarına ayarla
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            // Her gün tekrarlansın
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )

            android.util.Log.d("MidnightAlarm", "Gece yarısı alarmı kuruldu: ${calendar.time}")
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        scope.launch {
            try {
                setupTodaysAlarms(context)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun setupTodaysAlarms(context: Context) {
        try {
            val savedLocationDao = database.savedLocationDao()
            val prayerTimesDao = database.prayerTimesDao()
            val notificationSettingsDao = database.notificationSettingsDao()

            val currentLocation = savedLocationDao.getCurrentLocation().first()

            currentLocation?.let { location ->
                val today = LocalDate.now().toString()
                var todayPrayerTimes = prayerTimesDao.getPrayerTimesForDate(location.placeId, today)

                if (todayPrayerTimes == null) {
                    android.util.Log.d("MidnightAlarm", "Bugünün vakitleri yok, API'den çekiliyor...")

                    val result = repository.fetchAndSavePrayerTimes(
                        placeId = location.placeId,
                        lat = location.latitude,
                        lng = location.longitude,
                        startDate = today,
                        days = 365
                    )

                    if (result.isSuccess) {
                        todayPrayerTimes = prayerTimesDao.getPrayerTimesForDate(location.placeId, today)
                    } else {
                        android.util.Log.e("MidnightAlarm", "API'den veri çekilemedi: ${result.exceptionOrNull()?.message}")
                        return
                    }
                }

                val oneMonthLater = LocalDate.now().plusMonths(1).toString()
                val hasDataAfterOneMonth = prayerTimesDao.hasPrayerTimesAfterDate(location.placeId, oneMonthLater)

                if (!hasDataAfterOneMonth) {
                    android.util.Log.d("MidnightAlarm", "1 ay sonrası için veri yok, güncelleniyor...")

                    repository.fetchAndSavePrayerTimes(
                        placeId = location.placeId,
                        lat = location.latitude,
                        lng = location.longitude,
                        startDate = today,
                        days = 365
                    )
                }

                val settings = notificationSettingsDao.getSettingsOnce()

                if (todayPrayerTimes != null && settings != null) {
                    val alarmManager = PrayerAlarmManager(context)

                    alarmManager.cancelAllAlarms()

                    alarmManager.scheduleAlarmsForToday(todayPrayerTimes, settings)

                    android.util.Log.d("MidnightAlarm", "Bugünün alarmları kuruldu: $today")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MidnightAlarm", "Alarm kurma hatası", e)
        }
    }
}