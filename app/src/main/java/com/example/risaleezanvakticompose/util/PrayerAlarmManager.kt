package com.example.risaleezanvakticompose.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.risaleezanvakticompose.data.local.entities.NotificationSettings
import com.example.risaleezanvakticompose.data.local.entities.PrayerTimesEntity
import com.example.risaleezanvakticompose.service.PrayerTimeAlarmReceiver
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PrayerAlarmManager(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Bugünün namaz vakitleri için alarmları kur
     */
    fun scheduleAlarmsForToday(
        prayerTimes: PrayerTimesEntity,
        settings: NotificationSettings
    ) {
        val today = LocalDate.now()

        // Her namaz vakti için alarm kur
        if (settings.imsakEnabled) {
            scheduleAlarm(
                prayerName = "İmsak",
                prayerTime = prayerTimes.imsak,
                date = today,
                soundName = settings.imsakSound,
                minutesBefore = settings.imsakMinutesBefore,
                requestCode = 1
            )
        }

        if (settings.gunesEnabled) {
            scheduleAlarm(
                prayerName = "Güneş",
                prayerTime = prayerTimes.gunes,
                date = today,
                soundName = settings.gunesSound,
                minutesBefore = settings.gunesMinutesBefore,
                requestCode = 2
            )
        }

        if (settings.ogleEnabled) {
            scheduleAlarm(
                prayerName = "Öğle",
                prayerTime = prayerTimes.ogle,
                date = today,
                soundName = settings.ogleSound,
                minutesBefore = settings.ogleMinutesBefore,
                requestCode = 3
            )
        }

        if (settings.ikindiEnabled) {
            scheduleAlarm(
                prayerName = "İkindi",
                prayerTime = prayerTimes.ikindi,
                date = today,
                soundName = settings.ikindiSound,
                minutesBefore = settings.ikindiMinutesBefore,
                requestCode = 4
            )
        }

        if (settings.aksamEnabled) {
            scheduleAlarm(
                prayerName = "Akşam",
                prayerTime = prayerTimes.aksam,
                date = today,
                soundName = settings.aksamSound,
                minutesBefore = settings.aksamMinutesBefore,
                requestCode = 5
            )
        }

        if (settings.yatsiEnabled) {
            scheduleAlarm(
                prayerName = "Yatsı",
                prayerTime = prayerTimes.yatsi,
                date = today,
                soundName = settings.yatsiSound,
                minutesBefore = settings.yatsiMinutesBefore,
                requestCode = 6
            )
        }
    }

    private fun scheduleAlarm(
        prayerName: String,
        prayerTime: String,
        date: LocalDate,
        soundName: String,
        minutesBefore: Int,
        requestCode: Int
    ) {
        try {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val time = LocalTime.parse(prayerTime, formatter)
            val adjustedTime = time.minusMinutes(minutesBefore.toLong())

            val alarmTime = date.atTime(adjustedTime)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            // Geçmişte bir zaman ise alarm kurma
            if (alarmTime < System.currentTimeMillis()) {
                return
            }

            val intent = Intent(context, PrayerTimeAlarmReceiver::class.java).apply {
                putExtra(PrayerTimeAlarmReceiver.EXTRA_PRAYER_NAME, prayerName)
                putExtra(PrayerTimeAlarmReceiver.EXTRA_PRAYER_TIME, prayerTime)
                putExtra(PrayerTimeAlarmReceiver.EXTRA_SOUND_NAME, soundName)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Exact alarm kullan (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Tüm alarmları iptal et
     */
    fun cancelAllAlarms() {
        for (requestCode in 1..6) {
            val intent = Intent(context, PrayerTimeAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            )

            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        }
    }
}