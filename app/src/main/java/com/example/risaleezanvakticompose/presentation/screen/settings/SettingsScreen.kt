package com.example.risaleezanvakticompose.presentation.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val availableSounds by viewModel.availableSounds.collectAsState()

    var showSoundPicker by remember { mutableStateOf(false) }
    var selectedPrayerForSound by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                },

                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Bildirim Ayarları",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            settings?.let { s ->
                item {
                    PrayerNotificationCard(
                        prayerName = "İmsak",
                        enabled = s.imsakEnabled,
                        soundName = s.imsakSound,
                        minutesBefore = s.imsakMinutesBefore,
                        onToggle = { viewModel.togglePrayer("imsak") },
                        onChangSound = {
                            selectedPrayerForSound = "imsak"
                            showSoundPicker = true
                        },
                        onMinutesChange = { viewModel.updateMinutesBefore("imsak", it) }
                    )
                }

                item {
                    PrayerNotificationCard(
                        prayerName = "Güneş",
                        enabled = s.gunesEnabled,
                        soundName = s.gunesSound,
                        minutesBefore = s.gunesMinutesBefore,
                        onToggle = { viewModel.togglePrayer("gunes") },
                        onChangSound = {
                            selectedPrayerForSound = "gunes"
                            showSoundPicker = true
                        },
                        onMinutesChange = { viewModel.updateMinutesBefore("gunes", it) }
                    )
                }

                item {
                    PrayerNotificationCard(
                        prayerName = "Öğle",
                        enabled = s.ogleEnabled,
                        soundName = s.ogleSound,
                        minutesBefore = s.ogleMinutesBefore,
                        onToggle = { viewModel.togglePrayer("ogle") },
                        onChangSound = {
                            selectedPrayerForSound = "ogle"
                            showSoundPicker = true
                        },
                        onMinutesChange = { viewModel.updateMinutesBefore("ogle", it) }
                    )
                }

                item {
                    PrayerNotificationCard(
                        prayerName = "İkindi",
                        enabled = s.ikindiEnabled,
                        soundName = s.ikindiSound,
                        minutesBefore = s.ikindiMinutesBefore,
                        onToggle = { viewModel.togglePrayer("ikindi") },
                        onChangSound = {
                            selectedPrayerForSound = "ikindi"
                            showSoundPicker = true
                        },
                        onMinutesChange = { viewModel.updateMinutesBefore("ikindi", it) }
                    )
                }

                item {
                    PrayerNotificationCard(
                        prayerName = "Akşam",
                        enabled = s.aksamEnabled,
                        soundName = s.aksamSound,
                        minutesBefore = s.aksamMinutesBefore,
                        onToggle = { viewModel.togglePrayer("aksam") },
                        onChangSound = {
                            selectedPrayerForSound = "aksam"
                            showSoundPicker = true
                        },
                        onMinutesChange = { viewModel.updateMinutesBefore("aksam", it) }
                    )
                }

                item {
                    PrayerNotificationCard(
                        prayerName = "Yatsı",
                        enabled = s.yatsiEnabled,
                        soundName = s.yatsiSound,
                        minutesBefore = s.yatsiMinutesBefore,
                        onToggle = { viewModel.togglePrayer("yatsi") },
                        onChangSound = {
                            selectedPrayerForSound = "yatsi"
                            showSoundPicker = true
                        },
                        onMinutesChange = { viewModel.updateMinutesBefore("yatsi", it) }
                    )
                }
            }
        }
    }

    if (showSoundPicker && selectedPrayerForSound != null) {
        SoundPickerDialog(
            sounds = availableSounds,
            currentSound = when (selectedPrayerForSound) {
                "imsak" -> settings?.imsakSound
                "gunes" -> settings?.gunesSound
                "ogle" -> settings?.ogleSound
                "ikindi" -> settings?.ikindiSound
                "aksam" -> settings?.aksamSound
                "yatsi" -> settings?.yatsiSound
                else -> null
            } ?: "default_ezan",
            onSoundSelected = { soundName ->
                viewModel.updateSound(selectedPrayerForSound!!, soundName)
                showSoundPicker = false
                selectedPrayerForSound = null
            },
            onDismiss = {
                showSoundPicker = false
                selectedPrayerForSound = null
            }
        )
    }
}

@Composable
fun PrayerNotificationCard(
    prayerName: String,
    enabled: Boolean,
    soundName: String,
    minutesBefore: Int,
    onToggle: () -> Unit,
    onChangSound: () -> Unit,
    onMinutesChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prayerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Switch(
                    checked = enabled,
                    onCheckedChange = { onToggle() }
                )
            }

            if (enabled) {
                Spacer(modifier = Modifier.height(12.dp))

                // Ses seçimi
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChangSound() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = getSoundDisplayName(soundName),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null
                    )
                }

                HorizontalDivider()

                // Dakika önce ayarı
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kaç dakika önce?",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (minutesBefore > 0) onMinutesChange(minutesBefore - 1) },
                            enabled = minutesBefore > 0
                        ) {
                            Icon(Icons.Default.Remove, "Azalt")
                        }

                        Text(
                            text = "$minutesBefore dk",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.widthIn(min = 50.dp)
                        )

                        IconButton(
                            onClick = { if (minutesBefore < 30) onMinutesChange(minutesBefore + 1) },
                            enabled = minutesBefore < 30
                        ) {
                            Icon(Icons.Default.Add, "Artır")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SoundPickerDialog(
    sounds: List<String>,
    currentSound: String,
    onSoundSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ezan Sesi Seç") },
        text = {
            LazyColumn {
                items(sounds) { sound ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSoundSelected(sound) }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getSoundDisplayName(sound),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        if (sound == currentSound) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Seçili",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}

fun getSoundDisplayName(soundName: String): String {
    return when (soundName) {
        "default_ezan" -> "Varsayılan Ezan"
        "ezan_1" -> "Ezan 1"
        "ezan_2" -> "Ezan 2"
        "ezan_3" -> "Ezan 3"
        else -> soundName
    }
}