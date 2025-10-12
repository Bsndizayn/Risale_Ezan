package com.example.risaleezanvakticompose.presentation.screen.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.risaleezanvakticompose.data.local.dao.NotificationSettingsDao
import com.example.risaleezanvakticompose.data.local.entities.NotificationSettings
import com.example.risaleezanvakticompose.util.PrayerAlarmManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val notificationSettingsDao: NotificationSettingsDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _settings = MutableStateFlow<NotificationSettings?>(null)
    val settings: StateFlow<NotificationSettings?> = _settings.asStateFlow()

    private val _availableSounds = MutableStateFlow<List<String>>(emptyList())
    val availableSounds: StateFlow<List<String>> = _availableSounds.asStateFlow()


    init {
        loadSettings()
        loadAvailableSounds()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            notificationSettingsDao.getSettings().collect { settings ->
                _settings.value = settings ?: NotificationSettings()
            }
        }
    }

    private fun loadAvailableSounds() {
        viewModelScope.launch {
            // raw klasöründeki ezan seslerini listele
            val sounds = mutableListOf<String>()

            // Varsayılan sesler
            sounds.add("ezan_mekke")
            sounds.add("ezan_medine")
            sounds.add("ezan_istanbul")
            sounds.add("ezan_hafiz")

            _availableSounds.value = sounds
        }
    }

    fun togglePrayer(prayerName: String) {
        viewModelScope.launch {
            when (prayerName.lowercase()) {
                "imsak" -> {
                    val current = _settings.value?.imsakEnabled ?: true
                    notificationSettingsDao.toggleImsak(!current)
                }
                "gunes", "güneş" -> {
                    val current = _settings.value?.gunesEnabled ?: false
                    notificationSettingsDao.toggleGunes(!current)
                }
                "ogle", "öğle" -> {
                    val current = _settings.value?.ogleEnabled ?: true
                    notificationSettingsDao.toggleOgle(!current)
                }
                "ikindi" -> {
                    val current = _settings.value?.ikindiEnabled ?: true
                    notificationSettingsDao.toggleIkindi(!current)
                }
                "aksam", "akşam" -> {
                    val current = _settings.value?.aksamEnabled ?: true
                    notificationSettingsDao.toggleAksam(!current)
                }
                "yatsi", "yatsı" -> {
                    val current = _settings.value?.yatsiEnabled ?: true
                    notificationSettingsDao.toggleYatsi(!current)
                }
            }

            // Alarmları yeniden kur
            rescheduleAlarms()
        }
    }

    fun updateSound(prayerName: String, soundName: String) {
        viewModelScope.launch {
            val currentSettings = _settings.value ?: return@launch

            val updatedSettings = when (prayerName.lowercase()) {
                "imsak" -> currentSettings.copy(imsakSound = soundName)
                "gunes", "güneş" -> currentSettings.copy(gunesSound = soundName)
                "ogle", "öğle" -> currentSettings.copy(ogleSound = soundName)
                "ikindi" -> currentSettings.copy(ikindiSound = soundName)
                "aksam", "akşam" -> currentSettings.copy(aksamSound = soundName)
                "yatsi", "yatsı" -> currentSettings.copy(yatsiSound = soundName)
                else -> currentSettings
            }

            notificationSettingsDao.updateSettings(updatedSettings)

            // Alarmları yeniden kur
            rescheduleAlarms()
        }
    }

    fun updateMinutesBefore(prayerName: String, minutes: Int) {
        viewModelScope.launch {
            val currentSettings = _settings.value ?: return@launch

            val updatedSettings = when (prayerName.lowercase()) {
                "imsak" -> currentSettings.copy(imsakMinutesBefore = minutes)
                "gunes", "güneş" -> currentSettings.copy(gunesMinutesBefore = minutes)
                "ogle", "öğle" -> currentSettings.copy(ogleMinutesBefore = minutes)
                "ikindi" -> currentSettings.copy(ikindiMinutesBefore = minutes)
                "aksam", "akşam" -> currentSettings.copy(aksamMinutesBefore = minutes)
                "yatsi", "yatsı" -> currentSettings.copy(yatsiSound = minutes.toString())
                else -> currentSettings
            }

            notificationSettingsDao.updateSettings(updatedSettings)

            // Alarmları yeniden kur
            rescheduleAlarms()
        }
    }

    private fun rescheduleAlarms() {
        // Bu fonksiyon MainViewModel'deki scheduleAlarmsForToday'i tetikleyecek
        // AlarmManager'ı kullanarak mevcut alarmları iptal edip yeniden kuracak
        viewModelScope.launch {
            // Not: Burada MainViewModel'e bir event göndermek veya
            // repository üzerinden alarmları yönetmek daha iyi olabilir
            // Şimdilik basit tutuyoruz
        }
    }
}