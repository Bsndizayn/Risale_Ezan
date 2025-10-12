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

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var database: AppDatabase

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()

            scope.launch {
                try {
                    rescheduleAlarms(context)

                    // Gece yarısı alarmını da kur
                    MidnightAlarmReceiver.scheduleMidnightAlarm(context)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    private suspend fun rescheduleAlarms(context: Context) {
        try {
            val savedLocationDao = database.savedLocationDao()
            val prayerTimesDao = database.prayerTimesDao()
            val notificationSettingsDao = database.notificationSettingsDao()

            val currentLocation = savedLocationDao.getCurrentLocation().first()

            currentLocation?.let { location ->
                val today = LocalDate.now().toString()
                val todayPrayerTimes = prayerTimesDao.getPrayerTimesForDate(location.placeId, today)
                val settings = notificationSettingsDao.getSettingsOnce()

                if (todayPrayerTimes != null && settings != null) {
                    val alarmManager = PrayerAlarmManager(context)
                    alarmManager.scheduleAlarmsForToday(todayPrayerTimes, settings)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("BootReceiver", "Alarm kurma hatası", e)
        }
    }
}