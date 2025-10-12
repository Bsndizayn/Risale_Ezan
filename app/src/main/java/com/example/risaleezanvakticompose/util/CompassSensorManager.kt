package com.example.risaleezanvakticompose.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CompassSensorManager(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private var isLastAccelerometerArrayCopied = false
    private var isLastMagnetometerArrayCopied = false
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    fun getCompassFlow(): Flow<CompassData> = callbackFlow {
        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        System.arraycopy(event.values, 0, lastAccelerometer, 0, 3)
                        isLastAccelerometerArrayCopied = true
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        System.arraycopy(event.values, 0, lastMagnetometer, 0, 3)
                        isLastMagnetometerArrayCopied = true
                    }
                }

                if (isLastAccelerometerArrayCopied && isLastMagnetometerArrayCopied) {
                    if (SensorManager.getRotationMatrix(
                            rotationMatrix,
                            null,
                            lastAccelerometer,
                            lastMagnetometer
                        )
                    ) {
                        SensorManager.getOrientation(rotationMatrix, orientation)

                        val azimuthInRadians = orientation[0]
                        val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
                        val normalizedAzimuth = (azimuthInDegrees + 360) % 360

                        trySend(
                            CompassData(
                                azimuth = normalizedAzimuth,
                                pitch = Math.toDegrees(orientation[1].toDouble()).toFloat(),
                                roll = Math.toDegrees(orientation[2].toDouble()).toFloat()
                            )
                        )
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelerometer?.also {
            sensorManager.registerListener(
                sensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_GAME 
            )
        }

        magnetometer?.also {
            sensorManager.registerListener(
                sensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_GAME 
            )
        }

        awaitClose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    fun isSensorAvailable(): Boolean {
        return accelerometer != null && magnetometer != null
    }
}

data class CompassData(
    val azimuth: Float,
    val pitch: Float,
    val roll: Float
)