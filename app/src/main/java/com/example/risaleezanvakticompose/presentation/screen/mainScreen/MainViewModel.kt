package com.example.risaleezanvakticompose.presentation.screen.mainScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.risaleezanvakticompose.data.local.entities.NotificationSettings
import com.example.risaleezanvakticompose.data.local.entities.PrayerTimesEntity
import com.example.risaleezanvakticompose.data.local.entities.SavedLocation
import com.example.risaleezanvakticompose.data.repository.PrayerTimesRepository
import com.example.risaleezanvakticompose.service.MidnightAlarmReceiver
import com.example.risaleezanvakticompose.util.PermissionManager
import com.example.risaleezanvakticompose.util.PrayerAlarmManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PrayerTimesRepository,
    private val permissionManager: PermissionManager,
    private val notificationSettingsDao: com.example.risaleezanvakticompose.data.local.dao.NotificationSettingsDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _currentLocation = MutableStateFlow<SavedLocation?>(null)
    val currentLocation: StateFlow<SavedLocation?> = _currentLocation.asStateFlow()

    private val _countdown = MutableStateFlow<CountdownTime?>(null)
    val countdown: StateFlow<CountdownTime?> = _countdown.asStateFlow()

    private val _notificationSettings = MutableStateFlow<NotificationSettings?>(null)
    val notificationSettings: StateFlow<NotificationSettings?> = _notificationSettings.asStateFlow()

    private var countdownJob: Job? = null

    private val alarmManager = PrayerAlarmManager(context)

    init {
        loadCurrentLocation()
        loadNotificationSettings()
        ensureDefaultNotificationSettings()

        MidnightAlarmReceiver.scheduleMidnightAlarm(context)
    }

    private fun ensureDefaultNotificationSettings() {
        viewModelScope.launch {
            val settings = notificationSettingsDao.getSettingsOnce()
            if (settings == null) {
                notificationSettingsDao.insertSettings(NotificationSettings())
            }
        }
    }

    private fun loadNotificationSettings() {
        viewModelScope.launch {
            notificationSettingsDao.getSettings().collect { settings ->
                _notificationSettings.value = settings

                // Alarmları güncelle
                if (settings != null) {
                    scheduleAlarmsForToday()
                }
            }
        }
    }

    private fun loadCurrentLocation() {
        viewModelScope.launch {
            repository.getCurrentLocation().collect { location ->
                _currentLocation.value = location
                location?.let {
                    loadPrayerTimes(it)
                } ?: run {
                    _uiState.value = MainUiState.NoLocation
                }
            }
        }
    }

    private fun loadPrayerTimes(location: SavedLocation) {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading

            val today = LocalDate.now().toString()
            var prayerTimes = repository.getPrayerTimesForDate(location.placeId, today)

            if (prayerTimes == null) {
                val result = repository.fetchAndSavePrayerTimes(
                    placeId = location.placeId,
                    lat = location.latitude,
                    lng = location.longitude,
                    startDate = today,
                    days = 365
                )

                if (result.isSuccess) {
                    prayerTimes = repository.getPrayerTimesForDate(location.placeId, today)
                } else {
                    _uiState.value = MainUiState.Error(
                        result.exceptionOrNull()?.message ?: "Vakitler yüklenemedi"
                    )
                    return@launch
                }
            }

            prayerTimes?.let {
                val nextPrayer = calculateNextPrayer(it)
                _uiState.value = MainUiState.Success(
                    location = location,
                    prayerTimes = it,
                    nextPrayer = nextPrayer
                )
                startCountdown(nextPrayer)
                scheduleAlarmsForToday()
            } ?: run {
                _uiState.value = MainUiState.Error("Vakitler bulunamadı")
            }
        }
    }

    private fun scheduleAlarmsForToday() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state is MainUiState.Success) {
                val settings = _notificationSettings.value ?: return@launch
                alarmManager.scheduleAlarmsForToday(state.prayerTimes, settings)
            }
        }
    }

    fun toggleNotification(prayerName: String) {
        viewModelScope.launch {
            when (prayerName.lowercase()) {
                "imsak" -> {
                    val current = _notificationSettings.value?.imsakEnabled ?: true
                    notificationSettingsDao.toggleImsak(!current)
                }
                "gunes", "güneş" -> {
                    val current = _notificationSettings.value?.gunesEnabled ?: false
                    notificationSettingsDao.toggleGunes(!current)
                }
                "ogle", "öğle" -> {
                    val current = _notificationSettings.value?.ogleEnabled ?: true
                    notificationSettingsDao.toggleOgle(!current)
                }
                "ikindi" -> {
                    val current = _notificationSettings.value?.ikindiEnabled ?: true
                    notificationSettingsDao.toggleIkindi(!current)
                }
                "aksam", "akşam" -> {
                    val current = _notificationSettings.value?.aksamEnabled ?: true
                    notificationSettingsDao.toggleAksam(!current)
                }
                "yatsi", "yatsı" -> {
                    val current = _notificationSettings.value?.yatsiEnabled ?: true
                    notificationSettingsDao.toggleYatsi(!current)
                }
            }

            // Alarmları yeniden kur
            scheduleAlarmsForToday()
        }
    }

    private fun startCountdown(nextPrayer: NextPrayerInfo) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val targetTime = LocalTime.parse(nextPrayer.time, formatter)
                val now = LocalTime.now()

                val duration = java.time.Duration.between(now, targetTime)

                if (duration.isNegative || duration.isZero) {
                    _currentLocation.value?.let { location ->
                        loadPrayerTimes(location)
                    }
                    break
                }

                val hours = duration.toHours()
                val minutes = duration.toMinutes() % 60
                val seconds = duration.seconds % 60

                _countdown.value = CountdownTime(
                    hours = hours.toInt(),
                    minutes = minutes.toInt(),
                    seconds = seconds.toInt()
                )

                delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }

    fun getWeeklyPrayerTimes(placeId: Int): Flow<List<PrayerTimesEntity>> {
        val tomorrow = LocalDate.now().plusDays(1).toString()
        val nextWeek = LocalDate.now().plusDays(7).toString()
        return repository.getPrayerTimesRange(placeId, tomorrow, nextWeek)
    }

    fun fetchCurrentLocationFromGps(lat: Double, lng: Double) {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading

            val result = repository.fetchAndSaveGpsLocation(lat, lng)

            if (result.isFailure) {
                _uiState.value = MainUiState.Error(
                    result.exceptionOrNull()?.message ?: "Konum alınamadı"
                )
            }
        }
    }

    fun refreshPrayerTimes() {
        _currentLocation.value?.let { location ->
            viewModelScope.launch {
                _uiState.value = MainUiState.Loading

                val result = repository.refreshPrayerTimes(
                    location.placeId,
                    location.latitude,
                    location.longitude
                )

                if (result.isSuccess) {
                    loadPrayerTimes(location)
                } else {
                    _uiState.value = MainUiState.Error(
                        result.exceptionOrNull()?.message ?: "Yenileme başarısız"
                    )
                }
            }
        }
    }

    private fun calculateNextPrayer(prayerTimes: PrayerTimesEntity): NextPrayerInfo {
        val now = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        val prayers = listOf(
            "İmsak" to LocalTime.parse(prayerTimes.imsak, formatter),
            "Güneş" to LocalTime.parse(prayerTimes.gunes, formatter),
            "Öğle" to LocalTime.parse(prayerTimes.ogle, formatter),
            "İkindi" to LocalTime.parse(prayerTimes.ikindi, formatter),
            "Akşam" to LocalTime.parse(prayerTimes.aksam, formatter),
            "Yatsı" to LocalTime.parse(prayerTimes.yatsi, formatter)
        )

        for ((name, time) in prayers) {
            if (now.isBefore(time)) {
                val minutesUntil = java.time.Duration.between(now, time).toMinutes()
                return NextPrayerInfo(
                    name = name,
                    time = time.format(formatter),
                    minutesRemaining = minutesUntil.toInt()
                )
            }
        }

        return NextPrayerInfo(
            name = "İmsak",
            time = prayerTimes.imsak,
            minutesRemaining = 0
        )
    }

    fun checkPermissions(): PermissionsStatus {
        return PermissionsStatus(
            locationGranted = permissionManager.isLocationPermissionGranted(),
            notificationGranted = permissionManager.isNotificationPermissionGranted(),
            batteryOptimizationDisabled = permissionManager.isBatteryOptimizationDisabled()
        )
    }
}

sealed class MainUiState {
    data object Loading : MainUiState()
    data object NoLocation : MainUiState()
    data class Success(
        val location: SavedLocation,
        val prayerTimes: PrayerTimesEntity,
        val nextPrayer: NextPrayerInfo
    ) : MainUiState()
    data class Error(val message: String) : MainUiState()
}

data class NextPrayerInfo(
    val name: String,
    val time: String,
    val minutesRemaining: Int
)

data class CountdownTime(
    val hours: Int,
    val minutes: Int,
    val seconds: Int
)

data class PermissionsStatus(
    val locationGranted: Boolean,
    val notificationGranted: Boolean,
    val batteryOptimizationDisabled: Boolean
)