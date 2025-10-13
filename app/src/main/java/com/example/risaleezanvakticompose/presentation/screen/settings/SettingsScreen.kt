package com.example.risaleezanvakticompose.presentation.screen.settings

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsOff
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    val settings by viewModel.settings.collectAsState()
    val availableSounds by viewModel.availableSounds.collectAsState()
    val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsState()
    val showPermissionEducationalDialog by viewModel.showPermissionEducationalDialog.collectAsState()
    val showPermissionSettingsDialog by viewModel.showPermissionSettingsDialog.collectAsState()

    var showSoundPicker by remember { mutableStateOf(false) }
    var selectedPrayerForSound by remember { mutableStateOf<String?>(null) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        activity?.let { viewModel.updateNotificationPermissionState(it) }
    }

    LaunchedEffect(Unit) {
        activity?.let { viewModel.updateNotificationPermissionState(it) }
    }

    if (showPermissionEducationalDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onPermissionEducationalDialogDismissed() },
            title = { Text("Bildirim İzni") },
            text = {
                Text(
                    "Namaz vakti bildirimleri alabilmek için bildirim iznine ihtiyacımız var.\n\n" +
                            "Bu sayede namaz vakitleri geldiğinde sizi bilgilendirebiliriz."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onPermissionEducationalDialogDismissed()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }) {
                    Text("İzin Ver")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onPermissionEducationalDialogDismissed()
                }) {
                    Text("İptal")
                }
            }
        )
    }

    if (showPermissionSettingsDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onPermissionSettingsDialogDismissed() },
            title = { Text("Bildirim İzni Gerekli") },
            text = {
                Text(
                    "Bildirim iznini kalıcı olarak reddetmişsiniz.\n\n" +
                            "Ayarlar > Bildirimler'den bildirim iznini açabilirsiniz."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.openAppSettings()
                }) {
                    Text("Ayarlara Git")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onPermissionSettingsDialogDismissed()
                }) {
                    Text("İptal")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

                if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFF9800).copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.NotificationsOff,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(24.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Bildirim İzni Gerekli",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Bildirimleri aktifleştirmek için izin vermelisiniz",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }

                settings?.let { s ->
                    item {
                        GlassPrayerNotificationCard(
                            prayerName = "İmsak",
                            enabled = s.imsakEnabled,
                            soundName = s.imsakSound,
                            minutesBefore = s.imsakMinutesBefore,
                            hasPermission = hasNotificationPermission,
                            onToggle = {
                                activity?.let { act ->
                                    viewModel.onNotificationSwitchToggled("imsak", act)
                                }
                            },
                            onChangSound = {
                                if (hasNotificationPermission) {
                                    selectedPrayerForSound = "imsak"
                                    showSoundPicker = true
                                } else {
                                    activity?.let { act ->
                                        viewModel.onNotificationSwitchToggled("imsak", act)
                                    }
                                }
                            },
                            onMinutesChange = {
                                if (hasNotificationPermission) {
                                    viewModel.updateMinutesBefore("imsak", it)
                                }
                            }
                        )
                    }

                    item {
                        GlassPrayerNotificationCard(
                            prayerName = "Güneş",
                            enabled = s.gunesEnabled,
                            soundName = s.gunesSound,
                            minutesBefore = s.gunesMinutesBefore,
                            hasPermission = hasNotificationPermission,
                            onToggle = {
                                activity?.let { act ->
                                    viewModel.onNotificationSwitchToggled("gunes", act)
                                }
                            },
                            onChangSound = {
                                if (hasNotificationPermission) {
                                    selectedPrayerForSound = "gunes"
                                    showSoundPicker = true
                                } else {
                                    activity?.let { act ->
                                        viewModel.onNotificationSwitchToggled("gunes", act)
                                    }
                                }
                            },
                            onMinutesChange = {
                                if (hasNotificationPermission) {
                                    viewModel.updateMinutesBefore("gunes", it)
                                }
                            }
                        )
                    }

                    item {
                        GlassPrayerNotificationCard(
                            prayerName = "Öğle",
                            enabled = s.ogleEnabled,
                            soundName = s.ogleSound,
                            minutesBefore = s.ogleMinutesBefore,
                            hasPermission = hasNotificationPermission,
                            onToggle = {
                                activity?.let { act ->
                                    viewModel.onNotificationSwitchToggled("ogle", act)
                                }
                            },
                            onChangSound = {
                                if (hasNotificationPermission) {
                                    selectedPrayerForSound = "ogle"
                                    showSoundPicker = true
                                } else {
                                    activity?.let { act ->
                                        viewModel.onNotificationSwitchToggled("ogle", act)
                                    }
                                }
                            },
                            onMinutesChange = {
                                if (hasNotificationPermission) {
                                    viewModel.updateMinutesBefore("ogle", it)
                                }
                            }
                        )
                    }

                    item {
                        GlassPrayerNotificationCard(
                            prayerName = "İkindi",
                            enabled = s.ikindiEnabled,
                            soundName = s.ikindiSound,
                            minutesBefore = s.ikindiMinutesBefore,
                            hasPermission = hasNotificationPermission,
                            onToggle = {
                                activity?.let { act ->
                                    viewModel.onNotificationSwitchToggled("ikindi", act)
                                }
                            },
                            onChangSound = {
                                if (hasNotificationPermission) {
                                    selectedPrayerForSound = "ikindi"
                                    showSoundPicker = true
                                } else {
                                    activity?.let { act ->
                                        viewModel.onNotificationSwitchToggled("ikindi", act)
                                    }
                                }
                            },
                            onMinutesChange = {
                                if (hasNotificationPermission) {
                                    viewModel.updateMinutesBefore("ikindi", it)
                                }
                            }
                        )
                    }

                    item {
                        GlassPrayerNotificationCard(
                            prayerName = "Akşam",
                            enabled = s.aksamEnabled,
                            soundName = s.aksamSound,
                            minutesBefore = s.aksamMinutesBefore,
                            hasPermission = hasNotificationPermission,
                            onToggle = {
                                activity?.let { act ->
                                    viewModel.onNotificationSwitchToggled("aksam", act)
                                }
                            },
                            onChangSound = {
                                if (hasNotificationPermission) {
                                    selectedPrayerForSound = "aksam"
                                    showSoundPicker = true
                                } else {
                                    activity?.let { act ->
                                        viewModel.onNotificationSwitchToggled("aksam", act)
                                    }
                                }
                            },
                            onMinutesChange = {
                                if (hasNotificationPermission) {
                                    viewModel.updateMinutesBefore("aksam", it)
                                }
                            }
                        )
                    }

                    item {
                        GlassPrayerNotificationCard(
                            prayerName = "Yatsı",
                            enabled = s.yatsiEnabled,
                            soundName = s.yatsiSound,
                            minutesBefore = s.yatsiMinutesBefore,
                            hasPermission = hasNotificationPermission,
                            onToggle = {
                                activity?.let { act ->
                                    viewModel.onNotificationSwitchToggled("yatsi", act)
                                }
                            },
                            onChangSound = {
                                if (hasNotificationPermission) {
                                    selectedPrayerForSound = "yatsi"
                                    showSoundPicker = true
                                } else {
                                    activity?.let { act ->
                                        viewModel.onNotificationSwitchToggled("yatsi", act)
                                    }
                                }
                            },
                            onMinutesChange = {
                                if (hasNotificationPermission) {
                                    viewModel.updateMinutesBefore("yatsi", it)
                                }
                            }
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

fun Context.findActivity(): android.app.Activity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is android.app.Activity) return context
        context = context.baseContext
    }
    return null
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
    hasPermission: Boolean,
    onToggle: () -> Unit,
    onChangSound: () -> Unit,
    onMinutesChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled && hasPermission)
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!hasPermission) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "İzin gerekli",
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = prayerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (enabled && hasPermission)
                            Color.White
                        else
                            Color.White.copy(alpha = 0.5f)
                    )
                }

                Switch(
                    checked = enabled && hasPermission,
                    onCheckedChange = { onToggle() },
                    enabled = true,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.6f),
                        uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                        uncheckedTrackColor = Color.White.copy(alpha = 0.2f),
                        disabledCheckedThumbColor = Color.White.copy(alpha = 0.3f),
                        disabledCheckedTrackColor = Color.White.copy(alpha = 0.1f),
                        disabledUncheckedThumbColor = Color.White.copy(alpha = 0.3f),
                        disabledUncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }

            if (!hasPermission) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bildirim izni gerekli",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF9800),
                    fontWeight = FontWeight.Medium
                )
            }

            if (enabled && hasPermission) {
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