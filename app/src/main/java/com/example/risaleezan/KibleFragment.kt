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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class KibleFragment : Fragment(), SensorEventListener {

    private lateinit var qiblaInfoText: TextView
    private lateinit var qiblaArrowImage: ImageView
    private lateinit var compassImage: ImageView

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
    private var currentCompassDegree = 0f
    private var currentQiblaDegree = 0f

    private var qiblaDirection: Float? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_kible, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        initializeSensors()

        sharedViewModel.selectedLocation.observe(viewLifecycleOwner) { locationData ->
            if (locationData?.latitude != null && locationData.longitude != null) {
                fetchQiblaDirection(locationData.latitude, locationData.longitude)
            } else {
                qiblaInfoText.text = "Lütfen önce Namaz ekranından bir şehir seçin."
                qiblaArrowImage.visibility = View.INVISIBLE
            }
        }
    }

    private fun fetchQiblaDirection(latitude: Double, longitude: Double) {
        val url = "https://api.aladhan.com/v1/qibla/$latitude/$longitude"
        val queue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val data = response.getJSONObject("data")
                    qiblaDirection = data.getDouble("direction").toFloat()
                    qiblaInfoText.text = "Kıble Açısı: ${qiblaDirection?.toInt()}°"
                    qiblaArrowImage.visibility = View.VISIBLE
                } catch (e: Exception) {
                    qiblaInfoText.text = "Kıble yönü alınamadı."
                    qiblaArrowImage.visibility = View.INVISIBLE
                }
            },
            {
                qiblaInfoText.text = "İnternet bağlantısı kurulamadı."
                qiblaArrowImage.visibility = View.INVISIBLE
            }
        )
        queue.add(jsonObjectRequest)
    }

    private fun initializeViews(view: View) {
        qiblaInfoText = view.findViewById(R.id.textViewQiblaInfo)
        qiblaArrowImage = view.findViewById(R.id.imageViewQiblaArrow)
        compassImage = view.findViewById(R.id.imageViewCompass)
    }

    private fun initializeSensors() {
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (qiblaDirection == null || event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
                isLastAccelerometerArrayCopied = true
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
                isLastMagnetometerArrayCopied = true
            }
        }

        if (isLastAccelerometerArrayCopied && isLastMagnetometerArrayCopied) {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)
            SensorManager.getOrientation(rotationMatrix, orientation)

            val azimuthInRadians = orientation[0]
            val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
            val azimuth = (azimuthInDegrees + 360) % 360

            // 🧭 Pusula Görseli Döndür
            val compassAnim = RotateAnimation(
                currentCompassDegree,
                -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 250
                fillAfter = true
            }
            compassImage.startAnimation(compassAnim)
            currentCompassDegree = -azimuth

            // 🕌 Kıble Oku Döndür
            val bearingToQibla = (qiblaDirection!! - azimuth + 360) % 360
            val qiblaAnim = RotateAnimation(
                currentQiblaDegree,
                -bearingToQibla,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 250
                fillAfter = true
            }
            qiblaArrowImage.startAnimation(qiblaAnim)
            currentQiblaDegree = -bearingToQibla
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Sensör doğruluğu değiştiğinde burası çalışır, şimdilik boş bırakabiliriz.
    }
}
