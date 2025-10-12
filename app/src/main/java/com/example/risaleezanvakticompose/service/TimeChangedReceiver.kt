package com.example.risaleezanvakticompose.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.risaleezanvakticompose.data.local.AppDatabase
import com.example.risaleezanvakticompose.util.PrayerAlarmManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * Sistem saati değiştiğinde veya timezone değiştiğinde alarmları yeniden kuran receiver.
 *
 * Dinlediği broadcast'ler:
 * - ACTION_TIME_CHANGED: Manuel saat değişikliği
 * - ACTION_TIMEZONE_CHANGED: Timezone değişikliği
 * - ACTION_DATE_CHANGED: Gece yarısı tarih değişikliği
 */
@AndroidEntryPoint
class TimeChangedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var database: AppDatabase

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_DATE_CHANGED -> {
                val pendingResult = goAsync()

                scope.launch {
                    try {
                        rescheduleAlarms(context)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }

    private suspend fun rescheduleAlarms(context: Context) {
        try {
            val savedLocationDao = database.savedLocationDao()
            val prayerTimesDao = database.prayerTimesDao()
            val notificationSettingsDao = database.notificationSettingsDao()

            // Mevcut konumu al
            val currentLocation = savedLocationDao.getCurrentLocation().first()

            currentLocation?.let { location ->
                val today = LocalDate.now().toString()
                val todayPrayerTimes = prayerTimesDao.getPrayerTimesForDate(location.placeId, today)
                val settings = notificationSettingsDao.getSettingsOnce()

                if (todayPrayerTimes != null && settings != null) {
                    val alarmManager = PrayerAlarmManager(context)

                    // Önce mevcut alarmları iptal et
                    alarmManager.cancelAllAlarms()

                    // Yeni alarmları kur
                    alarmManager.scheduleAlarmsForToday(todayPrayerTimes, settings)

                    android.util.Log.d("TimeChangedReceiver", "Alarmlar yeniden kuruldu")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("TimeChangedReceiver", "Alarm yenileme hatası", e)
        }
    }
}