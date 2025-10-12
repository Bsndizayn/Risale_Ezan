package com.example.risaleezanvakticompose.presentation.screen.qibla

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.risaleezanvakticompose.data.repository.PrayerTimesRepository
import com.example.risaleezanvakticompose.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class QiblaViewModel @Inject constructor(
    application: Application,
    private val repository: PrayerTimesRepository
) : AndroidViewModel(application) {

    companion object {
        private const val ALPHA = 0.15f
        private const val LOCK_THRESHOLD = 10f
    }

    private val compassSensorManager = CompassSensorManager(application)

    private val _qiblaDirection = MutableStateFlow<Float?>(null)
    val qiblaDirection: StateFlow<Float?> = _qiblaDirection.asStateFlow()

    private val _currentAzimuth = MutableStateFlow(0f)
    val currentAzimuth: StateFlow<Float> = _currentAzimuth.asStateFlow()

    private var smoothedAzimuth = 0f
    private var lastRotation = 0f

    private val _qiblaArrowRotation = MutableStateFlow(0f)
    val qiblaArrowRotation: StateFlow<Float> = _qiblaArrowRotation.asStateFlow()

    private val _qiblaAccuracy = MutableStateFlow<QiblaAccuracy?>(null)
    val qiblaAccuracy: StateFlow<QiblaAccuracy?> = _qiblaAccuracy.asStateFlow()

    private val _isPhoneFlat = MutableStateFlow(true)
    val isPhoneFlat: StateFlow<Boolean> = _isPhoneFlat.asStateFlow()

    private val _isSensorAvailable = MutableStateFlow(false)
    val isSensorAvailable: StateFlow<Boolean> = _isSensorAvailable.asStateFlow()

    private val _hasLocation = MutableStateFlow(false)
    val hasLocation: StateFlow<Boolean> = _hasLocation.asStateFlow()

    init {
        _isSensorAvailable.value = compassSensorManager.isSensorAvailable()
        loadCurrentLocationAndCalculateQibla()
        startCompassSensor()
    }

    private fun loadCurrentLocationAndCalculateQibla() {
        viewModelScope.launch {
            repository.getCurrentLocation().collect { location ->
                if (location != null) {
                    _hasLocation.value = true
                    val qibla = QiblaCalculator.calculateQiblaDirection(
                        location.latitude,
                        location.longitude
                    )
                    _qiblaDirection.value = qibla
                } else {
                    _hasLocation.value = false
                    _qiblaDirection.value = null
                }
            }
        }
    }

    fun checkLocationAndRefresh() {
        loadCurrentLocationAndCalculateQibla()
    }

    private fun startCompassSensor() {
        viewModelScope.launch {
            compassSensorManager.getCompassFlow()
                .collect { compassData ->
                    val normalizedAzimuth = compassData.azimuth
                    _currentAzimuth.value = normalizedAzimuth

                    smoothedAzimuth = smoothAzimuth(smoothedAzimuth, normalizedAzimuth)

                    _isPhoneFlat.value = compassData.isFlat

                    _qiblaDirection.value?.let { qibla ->
                        val targetRotation = qibla - smoothedAzimuth
                        val smoothedRotation = smoothRotation(lastRotation, targetRotation)
                        lastRotation = smoothedRotation
                        _qiblaArrowRotation.value = smoothedRotation

                        val accuracy = QiblaCalculator.getQiblaAccuracy(qibla, smoothedAzimuth)
                        _qiblaAccuracy.value = accuracy
                    }
                }
        }
    }

    private fun smoothAzimuth(currentSmoothed: Float, newValue: Float): Float {
        val diff = ((newValue - currentSmoothed + 540) % 360) - 180
        return (currentSmoothed + ALPHA * diff + 360) % 360
    }

    private fun smoothRotation(current: Float, target: Float): Float {
        val diff = ((target - current + 540) % 360) - 180
        return if (abs(diff) <= LOCK_THRESHOLD) {
            target
        } else {
            (current + ALPHA * diff + 360) % 360
        }
    }

    override fun onCleared() {
        super.onCleared()
        compassSensorManager.stopListening()
    }
}