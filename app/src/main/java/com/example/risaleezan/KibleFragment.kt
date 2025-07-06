package com.example.risaleezan

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class KibleFragment : Fragment(), SensorEventListener {

    companion object {
        private const val TAG = "KibleFragment"
        private const val KABA_LATITUDE = 21.4225
        private const val KABA_LONGITUDE = 39.8262
        private const val ALPHA = 0.15f
        private const val API_TIMEOUT = 10000
    }

    private lateinit var qiblaInfoText: TextView
    private lateinit var qiblaArrowImage: ImageView
    private lateinit var compassImage: ImageView
    private lateinit var qiblaAccuracyText: TextView

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
    private var smoothedAzimuth = 0f

    private var isCalculationInProgress = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_kible, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            initializeViews(view)
            initializeSensors()
            observeLocationChanges()
        } catch (e: Exception) {
            Log.e(TAG, "Fragment başlatma hatası", e)
            showError("Kıble pusulası başlatılamadı: ${e.message}")
        }
    }

    private fun observeLocationChanges() {
        sharedViewModel.selectedLocation.observe(viewLifecycleOwner) { locationData ->
            try {
                if (locationData?.latitude != null && locationData.longitude != null) {
                    if (!isCalculationInProgress) {
                        calculateQiblaDirection(locationData.latitude, locationData.longitude)
                    }
                } else {
                    resetToInitialState()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Konum değişikliği işlenirken hata", e)
                showError("Konum işlenirken hata oluştu")
            }
        }
    }

    private fun resetToInitialState() {
        qiblaInfoText.text = "Lütfen önce Namaz ekranından bir şehir seçin."
        qiblaArrowImage.visibility = View.INVISIBLE
        qiblaAccuracyText.visibility = View.INVISIBLE
        qiblaDirection = null
    }

    private fun calculateQiblaDirection(latitude: Double, longitude: Double) {
        try {
            isCalculationInProgress = true

            // Girdi validasyonu
            if (!isValidCoordinate(latitude, longitude)) {
                showError("Geçersiz koordinatlar")
                isCalculationInProgress = false
                return
            }

            // Kabe'nin koordinatları
            val kaabaLat = Math.toRadians(KABA_LATITUDE)
            val kaabaLon = Math.toRadians(KABA_LONGITUDE)
            val userLat = Math.toRadians(latitude)
            val userLon = Math.toRadians(longitude)

            // Kıble yönünü hesapla
            val deltaLon = kaabaLon - userLon
            val y = sin(deltaLon) * cos(kaabaLat)
            val x = cos(userLat) * sin(kaabaLat) - sin(userLat) * cos(kaabaLat) * cos(deltaLon)

            var bearing = atan2(y, x)
            bearing = Math.toDegrees(bearing)
            qiblaDirection = ((bearing + 360) % 360).toFloat()

            // NaN kontrolü
            if (qiblaDirection!!.isNaN() || qiblaDirection!!.isInfinite()) {
                throw IllegalStateException("Kıble hesaplama sonucu geçersiz")
            }

            updateUI(true)

            // API'den doğrulama yap (eğer internet varsa)
            if (isNetworkAvailable()) {
                fetchQiblaDirectionFromAPI(latitude, longitude)
            } else {
                Log.w(TAG, "İnternet bağlantısı yok, sadece hesaplanmış değer kullanılıyor")
                qiblaInfoText.text = "Kıble Yönü: ${qiblaDirection?.toInt()}° (Çevrimdışı)"
                isCalculationInProgress = false
            }

        } catch (e: Exception) {
            Log.e(TAG, "Kıble hesaplama hatası", e)
            showError("Kıble yönü hesaplanamadı: ${e.message}")
            isCalculationInProgress = false
        }
    }

    private fun updateUI(isSuccessful: Boolean) {
        if (isSuccessful && qiblaDirection != null) {
            qiblaInfoText.text = "Kıble Yönü: ${qiblaDirection?.toInt()}°"
            qiblaArrowImage.visibility = View.VISIBLE
            qiblaAccuracyText.visibility = View.VISIBLE
        } else {
            qiblaArrowImage.visibility = View.INVISIBLE
            qiblaAccuracyText.visibility = View.INVISIBLE
        }
    }

    private fun isValidCoordinate(latitude: Double, longitude: Double): Boolean {
        return latitude in -90.0..90.0 &&
                longitude in -180.0..180.0 &&
                !latitude.isNaN() &&
                !longitude.isNaN() &&
                !latitude.isInfinite() &&
                !longitude.isInfinite()
    }

    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } else {
                @Suppress("DEPRECATION")
                val networkInfo = connectivityManager.activeNetworkInfo
                networkInfo?.isConnected == true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ağ durumu kontrol edilemedi", e)
            false
        }
    }

    private fun fetchQiblaDirectionFromAPI(latitude: Double, longitude: Double) {
        try {
            val url = "https://api.aladhan.com/v1/qibla/$latitude/$longitude"
            val queue = Volley.newRequestQueue(requireContext())

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null,
                { response ->
                    handleAPIResponse(response.toString())
                },
                { error ->
                    handleAPIError(error)
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    return hashMapOf(
                        "User-Agent" to "RisaleEzan/1.0",
                        "Accept" to "application/json"
                    )
                }
            }

            // Timeout ayarları
            jsonObjectRequest.setShouldCache(false)
            queue.add(jsonObjectRequest)

        } catch (e: Exception) {
            Log.e(TAG, "API isteği oluşturulurken hata", e)
            handleAPIError(null)
        }
    }

    private fun handleAPIResponse(responseString: String) {
        try {
            val response = org.json.JSONObject(responseString)

            if (!response.has("data")) {
                throw JSONException("API yanıtında 'data' alanı bulunamadı")
            }

            val data = response.getJSONObject("data")

            if (!data.has("direction")) {
                throw JSONException("API yanıtında 'direction' alanı bulunamadı")
            }

            val apiDirection = data.getDouble("direction").toFloat()

            // NaN ve sonsuzluk kontrolü
            if (apiDirection.isNaN() || apiDirection.isInfinite()) {
                throw IllegalStateException("API'den geçersiz yön değeri alındı")
            }

            // Geçerli aralık kontrolü
            if (apiDirection < 0 || apiDirection >= 360) {
                throw IllegalStateException("API'den aralık dışı yön değeri alındı: $apiDirection")
            }

            // Hesaplanan değer ile API değerini karşılaştır
            val difference = abs((qiblaDirection ?: 0f) - apiDirection)

            if (difference > 10) { // Tolerans artırıldı
                Log.w(TAG, "API ve hesaplanan değer arasında büyük fark: $difference°")
                qiblaDirection = apiDirection
                qiblaInfoText.text = "Kıble Yönü: ${qiblaDirection?.toInt()}°"
            } else {
                qiblaInfoText.text = "Kıble Yönü: ${qiblaDirection?.toInt()}°"
            }

            Log.d(TAG, "API'den kıble yönü başarıyla alındı: $apiDirection°")

        } catch (e: JSONException) {
            Log.e(TAG, "API yanıtı JSON parse hatası", e)
            handleAPIError(null)
        } catch (e: Exception) {
            Log.e(TAG, "API yanıtı işlenirken beklenmeyen hata", e)
            handleAPIError(null)
        } finally {
            isCalculationInProgress = false
        }
    }

    private fun handleAPIError(error: VolleyError?) {
        try {
            val errorMessage = when (error) {
                is NoConnectionError -> "İnternet bağlantısı yok"
                is TimeoutError -> "API zaman aşımı"
                else -> "API hatası: ${error?.message ?: "Bilinmeyen hata"}"
            }

            Log.w(TAG, "API hatası: $errorMessage", error)

            // Hesaplanan değeri kullan
            if (qiblaDirection != null) {
                qiblaInfoText.text = "Kıble Yönü: ${qiblaDirection?.toInt()}°"
            } else {
                showError("Kıble yönü hesaplanamadı")
            }

        } catch (e: Exception) {
            Log.e(TAG, "API hata işleme sırasında hata", e)
        } finally {
            isCalculationInProgress = false
        }
    }

    private fun initializeViews(view: View) {
        try {
            qiblaInfoText = view.findViewById(R.id.textViewQiblaInfo)
            qiblaArrowImage = view.findViewById(R.id.imageViewQiblaArrow)
            compassImage = view.findViewById(R.id.imageViewCompass)
            qiblaAccuracyText = view.findViewById(R.id.textViewQiblaAccuracy)

            // View'ların null olmadığını kontrol et
            if (::qiblaInfoText.isInitialized && ::qiblaArrowImage.isInitialized &&
                ::compassImage.isInitialized && ::qiblaAccuracyText.isInitialized) {
                Log.d(TAG, "View'lar başarıyla başlatıldı")
            } else {
                throw IllegalStateException("Bir veya daha fazla view bulunamadı")
            }

        } catch (e: Exception) {
            Log.e(TAG, "View başlatma hatası", e)
            throw e
        }
    }

    private fun initializeSensors() {
        try {
            sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

            when {
                accelerometer == null && magnetometer == null -> {
                    throw IllegalStateException("Cihazda pusula sensörleri bulunamadı")
                }
                accelerometer == null -> {
                    throw IllegalStateException("Cihazda ivmeölçer sensörü bulunamadı")
                }
                magnetometer == null -> {
                    throw IllegalStateException("Cihazda manyetometre sensörü bulunamadı")
                }
                else -> {
                    Log.d(TAG, "Sensörler başarıyla başlatıldı")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Sensör başlatma hatası", e)
            showError("Pusula sensörleri başlatılamadı: ${e.message}")
            throw e
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            accelerometer?.let {
                val success = sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
                if (!success) {
                    Log.w(TAG, "İvmeölçer sensörü kayıt edilemedi")
                }
            }
            magnetometer?.let {
                val success = sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
                if (!success) {
                    Log.w(TAG, "Manyetometre sensörü kayıt edilemedi")
                }
            }
            Log.d(TAG, "Sensör dinleyicileri kayıt edildi")
        } catch (e: Exception) {
            Log.e(TAG, "Sensör kayıt hatası", e)
            showError("Sensörler başlatılamadı")
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            sensorManager.unregisterListener(this)
            Log.d(TAG, "Sensör dinleyicileri kapatıldı")
        } catch (e: Exception) {
            Log.e(TAG, "Sensör kapatma hatası", e)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (qiblaDirection == null || event == null) return

        try {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    if (event.values.size >= 3) {
                        System.arraycopy(event.values, 0, lastAccelerometer, 0, 3)
                        isLastAccelerometerArrayCopied = true
                    }
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    if (event.values.size >= 3) {
                        System.arraycopy(event.values, 0, lastMagnetometer, 0, 3)
                        isLastMagnetometerArrayCopied = true
                    }
                }
            }

            if (isLastAccelerometerArrayCopied && isLastMagnetometerArrayCopied) {
                processCompassData()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Sensör verisi işleme hatası", e)
        }
    }

    private fun processCompassData() {
        try {
            if (SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)) {
                SensorManager.getOrientation(rotationMatrix, orientation)

                val azimuthInRadians = orientation[0]
                if (azimuthInRadians.isNaN() || azimuthInRadians.isInfinite()) {
                    Log.w(TAG, "Geçersiz azimuth değeri")
                    return
                }

                val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
                val normalizedAzimuth = (azimuthInDegrees + 360) % 360

                smoothedAzimuth = normalizedAzimuth * ALPHA + smoothedAzimuth * (1.0f - ALPHA)

                updateCompass(smoothedAzimuth)
                updateQiblaArrow(smoothedAzimuth)
                updateAccuracyText(smoothedAzimuth)
            } else {
                Log.w(TAG, "Rotasyon matrisi hesaplanamadı")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Pusula verisi işleme hatası", e)
        }
    }

    private fun updateCompass(azimuth: Float) {
        try {
            if (!azimuth.isNaN() && !azimuth.isInfinite()) {
                val compassRotation = -azimuth
                val compassAnim = RotateAnimation(
                    currentCompassDegree,
                    compassRotation,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                ).apply {
                    duration = 200
                    fillAfter = true
                }
                compassImage.startAnimation(compassAnim)
                currentCompassDegree = compassRotation
            }
        } catch (e: Exception) {
            Log.e(TAG, "Pusula güncelleme hatası", e)
        }
    }

    private fun updateQiblaArrow(azimuth: Float) {
        try {
            if (qiblaDirection != null && !azimuth.isNaN() && !azimuth.isInfinite()) {
                val qiblaAngle = (qiblaDirection!! - azimuth + 360) % 360

                if (!qiblaAngle.isNaN() && !qiblaAngle.isInfinite()) {
                    val qiblaAnim = RotateAnimation(
                        currentQiblaDegree,
                        qiblaAngle,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                    ).apply {
                        duration = 200
                        fillAfter = true
                    }
                    qiblaArrowImage.startAnimation(qiblaAnim)
                    currentQiblaDegree = qiblaAngle
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Kıble oku güncelleme hatası", e)
        }
    }

    private fun updateAccuracyText(azimuth: Float) {
        try {
            if (qiblaDirection == null || azimuth.isNaN() || azimuth.isInfinite()) return

            val qiblaAngle = (qiblaDirection!! - azimuth + 360) % 360

            if (qiblaAngle.isNaN() || qiblaAngle.isInfinite()) return

            val accuracy = when {
                qiblaAngle < 5 || qiblaAngle > 355 -> "Mükemmel"
                qiblaAngle < 15 || qiblaAngle > 345 -> "İyi"
                qiblaAngle < 30 || qiblaAngle > 330 -> "Orta"
                else -> "Kıbleyi bulmaya çalışın"
            }

            qiblaAccuracyText.text = "Doğruluk: $accuracy"

            val colorRes = when (accuracy) {
                "Mükemmel" -> android.R.color.holo_green_light
                "İyi" -> R.color.light_gold
                "Orta" -> android.R.color.holo_orange_light
                else -> R.color.off_white
            }

            qiblaAccuracyText.setTextColor(ContextCompat.getColor(requireContext(), colorRes))

        } catch (e: Exception) {
            Log.e(TAG, "Doğruluk metni güncelleme hatası", e)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        try {
            when (accuracy) {
                SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                    Log.w(TAG, "Sensör doğruluğu düşük")
                    showToast("Sensör doğruluğu düşük, telefonu 8 şeklinde hareket ettirin")
                }
                SensorManager.SENSOR_STATUS_ACCURACY_LOW -> {
                    Log.w(TAG, "Sensör doğruluğu düşük seviyede")
                }
                SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> {
                    Log.d(TAG, "Sensör doğruluğu orta seviyede")
                }
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> {
                    Log.d(TAG, "Sensör doğruluğu yüksek seviyede")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sensör doğruluk değişikliği işleme hatası", e)
        }
    }

    private fun showError(message: String) {
        try {
            if (isAdded && context != null) {
                qiblaInfoText.text = "Hata: $message"
                showToast(message)
                Log.e(TAG, message)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Hata mesajı gösterilirken hata", e)
        }
    }

    private fun showToast(message: String) {
        try {
            if (isAdded && context != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Toast gösterilirken hata", e)
        }
    }

    override fun onDestroyView() {
        try {
            if (::sensorManager.isInitialized) {
                sensorManager.unregisterListener(this)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fragment kapatılırken hata", e)
        }
        super.onDestroyView()
    }
}