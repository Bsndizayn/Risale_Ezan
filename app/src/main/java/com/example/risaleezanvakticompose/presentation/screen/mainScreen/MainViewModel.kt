package com.example.risaleezanvakticompose.presentation.screen.mainScreen

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.risaleezanvakticompose.R
import com.example.risaleezanvakticompose.data.local.dao.NotificationSettingsDao
import com.example.risaleezanvakticompose.data.local.entities.NotificationSettings
import com.example.risaleezanvakticompose.data.local.entities.PrayerTimesEntity
import com.example.risaleezanvakticompose.data.local.entities.SavedLocation
import com.example.risaleezanvakticompose.data.repository.PrayerTimesRepository
import com.example.risaleezanvakticompose.service.MidnightAlarmReceiver
import com.example.risaleezanvakticompose.util.PermissionManager
import com.example.risaleezanvakticompose.util.PermissionState
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
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: PrayerTimesRepository,
    private val notificationSettingsDao: NotificationSettingsDao,
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _currentLocation = MutableStateFlow<SavedLocation?>(null)
    val currentLocation: StateFlow<SavedLocation?> = _currentLocation.asStateFlow()

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _countdown = MutableStateFlow<CountdownTime?>(null)
    val countdown: StateFlow<CountdownTime?> = _countdown.asStateFlow()

    private val _currentQuote = MutableStateFlow("")
    val currentQuote: StateFlow<String> = _currentQuote.asStateFlow()

    private val _notificationSettings = MutableStateFlow<NotificationSettings?>(null)
    val notificationSettings: StateFlow<NotificationSettings?> = _notificationSettings.asStateFlow()

    private val _hasNotificationPermission = MutableStateFlow(false)
    val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission.asStateFlow()

    private var countdownJob: Job? = null
    private val alarmManager = PrayerAlarmManager(context)

    init {
        loadCurrentLocation()
        loadNotificationSettings()
        ensureDefaultNotificationSettings()
        MidnightAlarmReceiver.scheduleMidnightAlarm(context)
        loadDailyQuote()
        checkNotificationPermission()
    }

    private fun checkNotificationPermission() {
        _hasNotificationPermission.value = permissionManager.isNotificationPermissionGranted()
    }

    fun updateNotificationPermissionState(activity: Activity) {
        val state = permissionManager.getNotificationPermissionState(activity)
        val isGranted = state is PermissionState.GRANTED
        _hasNotificationPermission.value = isGranted

        if (isGranted) {
            Log.d("MainViewModel", "Bildirim izni verildi, alarmlar kuruluyor...")
            scheduleAlarmsForToday()
        } else {
            Log.d("MainViewModel", "Bildirim izni yok")
        }
    }

    private fun ensureDefaultNotificationSettings() {
        viewModelScope.launch {
            val settings = notificationSettingsDao.getSettingsOnce()
            if (settings == null) {
                notificationSettingsDao.insertSettings(NotificationSettings())
                Log.d("MainViewModel", "Varsayılan bildirim ayarları oluşturuldu")
            }
        }
    }

    private fun loadNotificationSettings() {
        viewModelScope.launch {
            notificationSettingsDao.getSettings().collect { settings ->
                _notificationSettings.value = settings ?: NotificationSettings()

                if (settings != null && _hasNotificationPermission.value) {
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
            val tomorrow = LocalDate.now().plusDays(1).toString()

            var todayPrayerTimes = repository.getPrayerTimesForDate(location.placeId, today)
            var tomorrowPrayerTimes = repository.getPrayerTimesForDate(location.placeId, tomorrow)

            if (todayPrayerTimes == null || tomorrowPrayerTimes == null) {
                Log.d("MainViewModel", "Namaz vakitleri yok, API'den çekiliyor...")

                val result = repository.fetchAndSavePrayerTimes(
                    placeId = location.placeId,
                    lat = location.latitude,
                    lng = location.longitude,
                    startDate = today,
                    days = 365
                )

                if (result.isSuccess) {
                    todayPrayerTimes = repository.getPrayerTimesForDate(location.placeId, today)
                    tomorrowPrayerTimes = repository.getPrayerTimesForDate(location.placeId, tomorrow)
                } else {
                    _uiState.value = MainUiState.Error(
                        result.exceptionOrNull()?.message ?: "Namaz vakitleri yüklenemedi"
                    )
                    return@launch
                }
            }

            if (todayPrayerTimes != null && tomorrowPrayerTimes != null) {
                val displayPrayerTimes = decideWhichDayToShow(todayPrayerTimes, tomorrowPrayerTimes)
                val nextPrayer = findNextPrayer(todayPrayerTimes, tomorrowPrayerTimes)

                _uiState.value = MainUiState.Success(displayPrayerTimes, nextPrayer)
                startCountdown(nextPrayer)
                scheduleAlarmsForToday()
            } else {
                _uiState.value = MainUiState.Error("Namaz vakitleri bulunamadı")
            }
        }
    }

    private fun decideWhichDayToShow(
        todayPrayerTimes: PrayerTimesEntity,
        tomorrowPrayerTimes: PrayerTimesEntity
    ): PrayerTimesEntity {
        val now = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        val yatsiTime = LocalTime.parse(todayPrayerTimes.yatsi, formatter)

        return if (now.isAfter(yatsiTime)) {
            tomorrowPrayerTimes
        } else {
            todayPrayerTimes
        }
    }

    private fun findNextPrayer(
        todayPrayerTimes: PrayerTimesEntity,
        tomorrowPrayerTimes: PrayerTimesEntity
    ): NextPrayerInfo {
        val now = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        val prayers = listOf(
            "İmsak" to todayPrayerTimes.imsak,
            "Güneş" to todayPrayerTimes.gunes,
            "Öğle" to todayPrayerTimes.ogle,
            "İkindi" to todayPrayerTimes.ikindi,
            "Akşam" to todayPrayerTimes.aksam,
            "Yatsı" to todayPrayerTimes.yatsi
        )

        for ((name, time) in prayers) {
            val prayerTime = LocalTime.parse(time, formatter)
            if (now.isBefore(prayerTime)) {
                return NextPrayerInfo(name, time, isNextDay = false)
            }
        }

        return NextPrayerInfo("İmsak", tomorrowPrayerTimes.imsak, isNextDay = true)
    }

    fun fetchCurrentLocationFromGps(lat: Double, lng: Double) {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading

            val result = repository.fetchAndSaveGpsLocation(lat, lng)

            if (result.isSuccess) {
                val savedLocation = result.getOrNull()
                Log.d(
                    "MainViewModel",
                    "GPS konumu kaydedildi ve 365 günlük veri çekildi: ${savedLocation?.placeName}"
                )

                savedLocation?.let {
                    val today = LocalDate.now().toString()
                    val fetchResult = repository.fetchAndSavePrayerTimes(
                        placeId = it.placeId,
                        lat = it.latitude,
                        lng = it.longitude,
                        startDate = today,
                        days = 365
                    )

                    if (fetchResult.isSuccess) {
                        Log.d("MainViewModel", "365 günlük namaz vakti başarıyla indirildi")
                    }
                }
            } else {
                _uiState.value = MainUiState.Error(
                    result.exceptionOrNull()?.message ?: "GPS konumu alınamadı"
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

    fun toggleNotification(prayerName: String) {
        viewModelScope.launch {
            when (prayerName.lowercase()) {
                "imsak", "İmsak" -> {
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
            scheduleAlarmsForToday()
        }
    }

    private fun startCountdown(nextPrayer: NextPrayerInfo) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                val now = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val targetTime = LocalTime.parse(nextPrayer.time, formatter)

                val targetDateTime = if (nextPrayer.isNextDay) {
                    LocalDateTime.of(LocalDate.now().plusDays(1), targetTime)
                } else {
                    LocalDateTime.of(LocalDate.now(), targetTime)
                }

                val duration = Duration.between(now, targetDateTime)

                if (duration.isNegative || duration.isZero) {
                    _currentLocation.value?.let { loadPrayerTimes(it) }
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

    private fun scheduleAlarmsForToday() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state !is MainUiState.Success) {
                Log.d("MainViewModel", "UI state Success değil, alarm kurulamıyor")
                return@launch
            }

            if (!_hasNotificationPermission.value) {
                Log.d("MainViewModel", "Bildirim izni yok, alarm kurulamadı")
                return@launch
            }

            val settings = _notificationSettings.value ?: NotificationSettings()

            if (state.prayerTimes.date == LocalDate.now().toString()) {
                Log.d("MainViewModel", "Bugünün alarmları kuruluyor...")
                alarmManager.cancelAllAlarms()
                alarmManager.scheduleAlarmsForToday(state.prayerTimes, settings)
                Log.d("MainViewModel", "Alarmlar başarıyla kuruldu")
            } else {
                Log.d("MainViewModel", "Prayer times tarihi bugün değil: ${state.prayerTimes.date}")
            }
        }
    }

    private fun loadDailyQuote() {
        viewModelScope.launch {
            _uiState.collect { state ->
                if (state is MainUiState.Success) {
                    val quote = getQuoteForPrayer(state.nextPrayer.name)
                    _currentQuote.value = quote
                }
            }
        }
    }

    private fun getQuoteForPrayer(prayerName: String): String {
        val quotes = context.resources.getStringArray(R.array.risale_quotes)
        if (quotes.isEmpty()) return ""

        val prayerIndex =
            when (prayerName.lowercase().replace("ı", "i").replace("ü", "u").replace("ö", "o")
                .replace("ş", "s").replace("ğ", "g").replace("ç", "c")) {
                "imsak" -> 0
                "gunes" -> 1
                "ogle" -> 2
                "ikindi" -> 3
                "aksam" -> 4
                "yatsi" -> 5
                else -> 0
            }

        val today = LocalDate.now().dayOfYear
        val index = (prayerIndex + today) % quotes.size

        return quotes[index]
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }

    fun getWeeklyPrayerTimes(placeId: Int): Flow<List<PrayerTimesEntity>> {
        val tomorrow = LocalDate.now().plusDays(1).toString()
        val thirtyDaysLater = LocalDate.now().plusDays(30).toString()
        return repository.getPrayerTimesRange(placeId, tomorrow, thirtyDaysLater)
    }
}

sealed class MainUiState {
    object Loading : MainUiState()
    object NoLocation : MainUiState()
    data class Success(
        val prayerTimes: PrayerTimesEntity,
        val nextPrayer: NextPrayerInfo
    ) : MainUiState()

    data class Error(val message: String) : MainUiState()
}

data class NextPrayerInfo(
    val name: String,
    val time: String,
    val isNextDay: Boolean = false
)

data class CountdownTime(
    val hours: Int,
    val minutes: Int,
    val seconds: Int
)