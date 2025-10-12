package com.example.risaleezanvakticompose.util

import kotlin.math.*

object QiblaCalculator {

    // Kabe'nin koordinatları (sabit)
    private const val KABA_LATITUDE = 21.4225
    private const val KABA_LONGITUDE = 39.8262

    /**
     * Kullanıcının konumuna göre Kıble yönünü hesaplar
     * @return Kıble açısı (0-360 derece)
     */
    fun calculateQiblaDirection(userLatitude: Double, userLongitude: Double): Float {
        val kaabaLat = Math.toRadians(KABA_LATITUDE)
        val kaabaLon = Math.toRadians(KABA_LONGITUDE)
        val userLat = Math.toRadians(userLatitude)
        val userLon = Math.toRadians(userLongitude)

        val deltaLon = kaabaLon - userLon
        val y = sin(deltaLon) * cos(kaabaLat)
        val x = cos(userLat) * sin(kaabaLat) - sin(userLat) * cos(kaabaLat) * cos(deltaLon)

        var bearing = atan2(y, x)
        bearing = Math.toDegrees(bearing)

        return ((bearing + 360) % 360).toFloat()
    }

    /**
     * İki açı arasındaki farkı hesaplar
     */
    fun getAngleDifference(targetAngle: Float, currentAngle: Float): Float {
        var diff = targetAngle - currentAngle
        if (diff > 180) diff -= 360
        if (diff < -180) diff += 360
        return diff
    }

    /**
     * Kullanıcının kıbleye ne kadar yakın olduğunu kontrol eder
     */
    fun getQiblaAccuracy(qiblaAngle: Float, currentAzimuth: Float): QiblaAccuracy {
        val diff = getAngleDifference(qiblaAngle, currentAzimuth)
        val absDiff = abs(diff)

        return when {
            absDiff <= 10 -> QiblaAccuracy.EXACT // ±10° içinde DOĞRU
            absDiff <= 15 -> QiblaAccuracy.VERY_CLOSE
            absDiff <= 30 -> QiblaAccuracy.CLOSE
            diff > 0 -> QiblaAccuracy.TURN_RIGHT
            else -> QiblaAccuracy.TURN_LEFT
        }
    }
}

enum class QiblaAccuracy {
    EXACT,
    VERY_CLOSE,
    CLOSE,
    TURN_RIGHT,
    TURN_LEFT
}