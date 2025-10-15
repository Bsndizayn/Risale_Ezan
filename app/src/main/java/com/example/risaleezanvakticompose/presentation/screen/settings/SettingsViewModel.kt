package com.example.risaleezanvakticompose.presentation.screen.settings

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.risaleezanvakticompose.data.local.dao.NotificationSettingsDao
import com.example.risaleezanvakticompose.data.local.entities.NotificationSettings
import com.example.risaleezanvakticompose.util.PermissionManager
import com.example.risaleezanvakticompose.util.PermissionState
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
    private val permissionManager: PermissionManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _settings = MutableStateFlow<NotificationSettings?>(null)
    val settings: StateFlow<NotificationSettings?> = _settings.asStateFlow()

    private val _availableSounds = MutableStateFlow<List<String>>(emptyList())
    val availableSounds: StateFlow<List<String>> = _availableSounds.asStateFlow()

    private val _hasNotificationPermission = MutableStateFlow(false)
    val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission.asStateFlow()

    private val _showPermissionEducationalDialog = MutableStateFlow(false)
    val showPermissionEducationalDialog: StateFlow<Boolean> = _showPermissionEducationalDialog.asStateFlow()

    private val _showPermissionSettingsDialog = MutableStateFlow(false)
    val showPermissionSettingsDialog: StateFlow<Boolean> = _showPermissionSettingsDialog.asStateFlow()

    private val _selectedPrayerForPermission = MutableStateFlow<String?>(null)

    init {
        loadSettings()
        loadAvailableSounds()
        checkNotificationPermission()
    }

    private fun checkNotificationPermission() {
        _hasNotificationPermission.value = permissionManager.isNotificationPermissionGranted()
    }

    fun updateNotificationPermissionState(activity: Activity) {
        val isGranted = permissionManager.isNotificationPermissionGranted()
        _hasNotificationPermission.value = isGranted

        if (isGranted && _selectedPrayerForPermission.value != null) {
            togglePrayer(_selectedPrayerForPermission.value!!)
            _selectedPrayerForPermission.value = null
        }
    }

    fun onNotificationSwitchToggled(prayerName: String, activity: Activity) {
        when (permissionManager.getNotificationPermissionState(activity)) {
            is PermissionState.GRANTED -> {
                togglePrayer(prayerName)
            }
            is PermissionState.DENIED -> {
                _selectedPrayerForPermission.value = prayerName
                _showPermissionEducationalDialog.value = true
            }
            is PermissionState.PERMANENTLY_DENIED -> {
                _showPermissionSettingsDialog.value = true
            }
        }
    }

    fun onPermissionEducationalDialogDismissed() {
        _showPermissionEducationalDialog.value = false
    }

    fun onPermissionSettingsDialogDismissed() {
        _showPermissionSettingsDialog.value = false
    }

    fun openAppSettings() {
        permissionManager.openAppSettings()
        _showPermissionSettingsDialog.value = false
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
            val sounds = mutableListOf<String>()
            sounds.add("system_ringtone")
            sounds.add("kus_sesi")
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
                "yatsi", "yatsı" -> currentSettings.copy(yatsiMinutesBefore = minutes)
                else -> currentSettings
            }

            notificationSettingsDao.updateSettings(updatedSettings)
            rescheduleAlarms()
        }
    }

    private fun rescheduleAlarms() {
        viewModelScope.launch {
        }
    }
}