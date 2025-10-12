package com.example.risaleezanvakticompose.util

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkManagerHelper {

    private const val PRAYER_TIMES_REFRESH_WORK = "prayer_times_refresh_work"

    fun schedulePrayerTimesRefresh(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshRequest = PeriodicWorkRequestBuilder<PrayerTimesRefreshWorker>(
            30, TimeUnit.DAYS  // 30 günde bir
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PRAYER_TIMES_REFRESH_WORK,
            ExistingPeriodicWorkPolicy.KEEP,  // Zaten varsa yeniden başlatma
            refreshRequest
        )
    }

    fun cancelPrayerTimesRefresh(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(PRAYER_TIMES_REFRESH_WORK)
    }
}