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
        private const val LOCK_THRESHOLD = 10f // ±10 derece içinde kilitlenme
    }

    private val compassSensorManager = CompassSensorManager(application)

    private val _qiblaDirection = MutableStateFlow<Float?>(null)
    val qiblaDirection: StateFlow<Float?> = _qiblaDirection.asStateFlow()

    private val _currentAzimuth = MutableStateFlow(0f)
    val currentAzimuth: StateFlow<Float> = _currentAzimuth.asStateFlow()

    private var smoothedAzimuth = 0f
    private var lastRotation = 0f // Önceki rotation değeri (360° geçiş için)

    private val _qiblaArrowRotation = MutableStateFlow(0f)
    val qiblaArrowRotation: StateFlow<Float> = _qiblaArrowRotation.asStateFlow()

    private val _qiblaAccuracy = MutableStateFlow<QiblaAccuracy?>(null)
    val qiblaAccuracy: StateFlow<QiblaAccuracy?> = _qiblaAccuracy.asStateFlow()

    private val _isPhoneFlat = MutableStateFlow(true)
    val isPhoneFlat: StateFlow<Boolean> = _isPhoneFlat.asStateFlow()

    private val _isSensorAvailable = MutableStateFlow(false)
    val isSensorAvailable: StateFlow<Boolean> = _isSensorAvailable.asStateFlow()

    init {
        _isSensorAvailable.value = compassSensorManager.isSensorAvailable()
        loadCurrentLocationAndCalculateQibla()
        startCompassSensor()
    }

    private fun loadCurrentLocationAndCalculateQibla() {
        viewModelScope.launch {
            repository.getCurrentLocation().collect { location ->
                if (location != null) {
                    val qibla = QiblaCalculator.calculateQiblaDirection(
                        location.latitude,
                        location.longitude
                    )
                    _qiblaDirection.value = qibla
                }
            }
        }
    }

    private fun startCompassSensor() {
        viewModelScope.launch {
            compassSensorManager.getCompassFlow()
                .collect { compassData ->
                    val normalizedAzimuth = compassData.azimuth
                    _currentAzimuth.value = normalizedAzimuth

                    // Low-pass filter
                    smoothedAzimuth = normalizedAzimuth * ALPHA + smoothedAzimuth * (1.0f - ALPHA)

                    // Telefonun yatay olup olmadığını kontrol et
                    val isFlat = abs(compassData.pitch) < 30 && abs(compassData.roll) < 30
                    _isPhoneFlat.value = isFlat

                    // Kıble yönünü hesapla
                    val qibla = _qiblaDirection.value
                    if (qibla != null) {
                        updateQiblaRotation(qibla, smoothedAzimuth)

                        // Doğruluk hesapla
                        val accuracy = QiblaCalculator.getQiblaAccuracy(qibla, smoothedAzimuth)
                        _qiblaAccuracy.value = accuracy
                    }
                }
        }
    }

    /**
     * Kıble rotation'ını hesaplar - ±5° içinde kilitler
     */
    private fun updateQiblaRotation(qibla: Float, azimuth: Float) {
        // Kıble ile azimuth arasındaki en kısa farkı hesapla
        val diff = getShortestAngleDifference(qibla, azimuth)
        val absDiff = abs(diff)

        // ±5° İÇİNDE Mİ? → KİLİTLE
        if (absDiff <= LOCK_THRESHOLD) {
            _qiblaArrowRotation.value = 0f // Tam kıble yönü, sıfırda kilitle
        } else {
            // ±5° DIŞINDA → Normal takip et
            var newRotation = (qibla - azimuth + 360) % 360

            // 360° DÖNME FİX'İ - En kısa yolu seç
            val rotationDiff = getShortestAngleDifference(newRotation, lastRotation)
            newRotation = lastRotation + rotationDiff

            lastRotation = newRotation
            _qiblaArrowRotation.value = newRotation
        }
    }

    /**
     * İki açı arasındaki en kısa farkı hesaplar (-180 ile +180 arası)
     */
    private fun getShortestAngleDifference(target: Float, current: Float): Float {
        var diff = target - current
        while (diff > 180) diff -= 360
        while (diff < -180) diff += 360
        return diff
    }
}