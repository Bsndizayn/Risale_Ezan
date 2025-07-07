package com.example.risaleezan

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class KibleFragment : Fragment(), SensorEventListener {

    companion object {
        private const val KABA_LATITUDE = 21.4225
        private const val KABA_LONGITUDE = 39.8262
        private const val ALPHA = 0.15f
    }

    private lateinit var qiblaInfoText: TextView
    private lateinit var qiblaArrowImage: ImageView
    private lateinit var compassImage: ImageView
    private lateinit var qiblaAccuracyText: TextView
    private lateinit var exactQiblaText: TextView

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private var isLastAccelerometerArrayCopied = false
    private var isLastMagnetometerArrayCopied = false
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    private var currentQiblaDegree = 0f
    private var qiblaDirection: Float? = null
    private var smoothedAzimuth = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_kible, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeViews(view)
        initializeSensors()
        observeLocationChanges()
    }

    private fun initializeViews(view: View) {
        qiblaInfoText = view.findViewById(R.id.textViewQiblaInfo)
        qiblaArrowImage = view.findViewById(R.id.imageViewQiblaArrow)
        compassImage = view.findViewById(R.id.imageViewCompass)
        qiblaAccuracyText = view.findViewById(R.id.textViewQiblaAccuracy)
        exactQiblaText = view.findViewById(R.id.textViewExactQibla)
    }

    private fun initializeSensors() {
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    private fun observeLocationChanges() {
        sharedViewModel.selectedLocation.observe(viewLifecycleOwner) { location ->
            if (location?.latitude != null && location.longitude != null) {
                calculateQiblaDirection(location.latitude, location.longitude)
            }
        }
    }

    private fun calculateQiblaDirection(latitude: Double, longitude: Double) {
        val kaabaLat = Math.toRadians(KABA_LATITUDE)
        val kaabaLon = Math.toRadians(KABA_LONGITUDE)
        val userLat = Math.toRadians(latitude)
        val userLon = Math.toRadians(longitude)

        val deltaLon = kaabaLon - userLon
        val y = sin(deltaLon) * cos(kaabaLat)
        val x = cos(userLat) * sin(kaabaLat) - sin(userLat) * cos(kaabaLat) * cos(deltaLon)

        var bearing = atan2(y, x)
        bearing = Math.toDegrees(bearing)
        qiblaDirection = ((bearing + 360) % 360).toFloat()

        qiblaInfoText.text = "Kıble Yönü: ${qiblaDirection?.toInt()}°"
        qiblaArrowImage.visibility = View.VISIBLE
        qiblaAccuracyText.visibility = View.VISIBLE
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (qiblaDirection == null || event == null) return

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
            processCompassData()
        }
    }

    private fun processCompassData() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)) {
            SensorManager.getOrientation(rotationMatrix, orientation)

            val azimuthInRadians = orientation[0]
            val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
            val normalizedAzimuth = (azimuthInDegrees + 360) % 360
            smoothedAzimuth = normalizedAzimuth * ALPHA + smoothedAzimuth * (1.0f - ALPHA)

            updateQiblaArrow(smoothedAzimuth)
            updateAccuracyText(smoothedAzimuth)
        }
    }

    // Arka plan pusula sabit kalmalı, bu yüzden boş
    private fun updateCompass(azimuth: Float) {}

    private fun updateQiblaArrow(azimuth: Float) {
        val qiblaAngle = (qiblaDirection!! - azimuth + 360) % 360

        val qiblaAnim = RotateAnimation(
            currentQiblaDegree, qiblaAngle,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 200
            fillAfter = true
        }
        qiblaArrowImage.startAnimation(qiblaAnim)
        currentQiblaDegree = qiblaAngle
    }

    private fun updateAccuracyText(azimuth: Float) {
        val qiblaAngle = (qiblaDirection!! - azimuth + 360) % 360

        when {
            qiblaAngle in 355.0..360.0 || qiblaAngle in 0.0..5.0 -> {
                qiblaAccuracyText.text = "TAM DOĞRU"
                qiblaArrowImage.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.holo_green_light))
                exactQiblaText.visibility = View.VISIBLE
            }
            qiblaAngle in 0.0..180.0 -> {
                qiblaAccuracyText.text = "SAĞA DÖNÜN"
                qiblaArrowImage.clearColorFilter()
                exactQiblaText.visibility = View.GONE
            }
            else -> {
                qiblaAccuracyText.text = "SOLA DÖNÜN"
                qiblaArrowImage.clearColorFilter()
                exactQiblaText.visibility = View.GONE
            }
        }

        qiblaAccuracyText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroyView() {
        sensorManager.unregisterListener(this)
        super.onDestroyView()
    }
}
