package com.example.risaleezanvakticompose.presentation.screen.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.risaleezanvakticompose.R

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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            GlassSettingsTopBar(onBack = onBack)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Namaz Vakti Bildirimleri",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }

                settings?.let { s ->
                    item {
                        GlassPrayerNotificationCard(
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
                        GlassPrayerNotificationCard(
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
                        GlassPrayerNotificationCard(
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
                        GlassPrayerNotificationCard(
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
                        GlassPrayerNotificationCard(
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
                        GlassPrayerNotificationCard(
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

                item {
                    Spacer(modifier = Modifier.height(16.dp))
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
fun GlassSettingsTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()

            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    "Geri",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Bildirim Ayarları",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GlassPrayerNotificationCard(
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
                Color.White.copy(alpha = 0.2f)
            else
                Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(20.dp)
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
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color.White else Color.White.copy(alpha = 0.5f)
                )

                Switch(
                    checked = enabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.6f),
                        uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                        uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )
            }

            if (enabled) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable { onChangSound() }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = getSoundDisplayName(soundName),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kaç dakika önce?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (minutesBefore > 0) onMinutesChange(minutesBefore - 1) },
                                enabled = minutesBefore > 0,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (minutesBefore > 0) Color.White.copy(alpha = 0.15f)
                                        else Color.White.copy(alpha = 0.05f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    "Azalt",
                                    tint = if (minutesBefore > 0) Color.White else Color.White.copy(
                                        alpha = 0.3f
                                    ),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = "$minutesBefore",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            IconButton(
                                onClick = { if (minutesBefore < 30) onMinutesChange(minutesBefore + 1) },
                                enabled = minutesBefore < 30,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (minutesBefore < 30) Color.White.copy(alpha = 0.15f)
                                        else Color.White.copy(alpha = 0.05f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    "Artır",
                                    tint = if (minutesBefore < 30) Color.White else Color.White.copy(
                                        alpha = 0.3f
                                    ),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
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
        containerColor = Color(0xFF1E3A8A),
        title = {
            Text(
                "Ezan Sesi Seç",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sounds) { sound ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSoundSelected(sound) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (sound == currentSound)
                                Color.White.copy(alpha = 0.2f)
                            else
                                Color.White.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = getSoundDisplayName(sound),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )

                            if (sound == currentSound) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Seçili",
                                    tint = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("Kapat", fontWeight = FontWeight.Bold)
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