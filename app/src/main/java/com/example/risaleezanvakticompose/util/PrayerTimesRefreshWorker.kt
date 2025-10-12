package com.example.risaleezanvakticompose.util

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.risaleezanvakticompose.data.repository.PrayerTimesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PrayerTimesRefreshWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: PrayerTimesRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val locations = mutableListOf<com.example.risaleezanvakticompose.data.local.entities.SavedLocation>()
            repository.getAllLocations().collect { locationList ->
                locations.addAll(locationList)
            }

            locations.forEach { location ->
                repository.refreshPrayerTimes(
                    location.placeId,
                    location.latitude,
                    location.longitude
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}