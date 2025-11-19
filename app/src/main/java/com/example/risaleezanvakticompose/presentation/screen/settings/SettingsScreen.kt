package com.example.risaleezanvakticompose.presentation.screen.settings

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.risaleezanvakticompose.R

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

    // MediaPlayer state
    var currentPlayingSound by remember { mutableStateOf<String?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Cleanup MediaPlayer on dispose
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    BackHandler {
        // Önce ses çalıyorsa durdur
        mediaPlayer?.release()
        mediaPlayer = null
        currentPlayingSound = null
        onBack()
    }

    LaunchedEffect(Unit) {
        activity?.let { viewModel.updateNotificationPermissionState(it) }
    }

    if (showPermissionEducationalDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onPermissionEducationalDialogDismissed() },
            title = { Text("Bildirim İzni Gerekli") },
            text = { Text("Namaz vakti bildirimlerini alabilmek için bildirim iznine ihtiyacımız var.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onPermissionEducationalDialogDismissed()
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
                            "Ayarlar > İzinler > Bildirimler'den bildirim iznini açabilirsiniz."
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
            GlassSettingsTopBar(onBack = {
                mediaPlayer?.release()
                mediaPlayer = null
                currentPlayingSound = null
                onBack()
            })

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
            context = context,
            sounds = availableSounds,
            currentSound = when (selectedPrayerForSound) {
                "imsak" -> settings?.imsakSound
                "gunes" -> settings?.gunesSound
                "ogle" -> settings?.ogleSound
                "ikindi" -> settings?.ikindiSound
                "aksam" -> settings?.aksamSound
                "yatsi" -> settings?.yatsiSound
                else -> null
            } ?: "system_ringtone",
            currentPlayingSound = currentPlayingSound,
            onSoundSelected = { soundName ->
                // Ses çalmayı durdur
                mediaPlayer?.release()
                mediaPlayer = null
                currentPlayingSound = null

                viewModel.updateSound(selectedPrayerForSound!!, soundName)
                showSoundPicker = false
                selectedPrayerForSound = null
            },
            onPlaySound = { soundName ->
                mediaPlayer?.release()

                if (currentPlayingSound == soundName) {
                    mediaPlayer = null
                    currentPlayingSound = null
                } else {
                    try {
                        if (soundName == "system_ringtone") {
                            val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                            mediaPlayer = MediaPlayer().apply {
                                setDataSource(context, ringtoneUri)
                                prepare()
                            }
                        } else {
                            val resourceId = context.resources.getIdentifier(
                                soundName,
                                "raw",
                                context.packageName
                            )

                            if (resourceId != 0) {
                                mediaPlayer = MediaPlayer.create(context, resourceId)
                            }
                        }

                        mediaPlayer?.setOnCompletionListener {
                            it.release()
                            mediaPlayer = null
                            currentPlayingSound = null
                        }
                        mediaPlayer?.start()
                        currentPlayingSound = soundName
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            onDismiss = {
                // Ses çalmayı durdur
                mediaPlayer?.release()
                mediaPlayer = null
                currentPlayingSound = null

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
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prayerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (!hasPermission) {
                        Text(
                            text = "İzin gerekli",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFF9800)
                        )
                    }
                }

                Switch(
                    checked = enabled && hasPermission,
                    onCheckedChange = { onToggle() },
                    enabled = hasPermission,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF4CAF50),
                        checkedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                        uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )
            }

            if (enabled && hasPermission) {
                Divider(color = Color.White.copy(alpha = 0.2f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChangSound() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ezan Sesi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = getSoundDisplayName(soundName),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        "Seç",
                        tint = Color.White.copy(alpha = 0.5f)
                    )
                }

                Divider(color = Color.White.copy(alpha = 0.2f))

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dakika Önce",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (minutesBefore > 0) {
                                        onMinutesChange(minutesBefore - 1)
                                    }
                                },
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
                                    contentDescription = "Azalt",
                                    tint = if (minutesBefore > 0) Color.White else Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$minutesBefore",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (minutesBefore < 30) {
                                        onMinutesChange(minutesBefore + 1)
                                    }
                                },
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
                                    contentDescription = "Artır",
                                    tint = if (minutesBefore < 30) Color.White else Color.White.copy(alpha = 0.3f),
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
    context: Context,
    sounds: List<String>,
    currentSound: String,
    currentPlayingSound: String?,
    onSoundSelected: (String) -> Unit,
    onPlaySound: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
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
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // ✅ Play/Pause butonu
                                IconButton(
                                    onClick = { onPlaySound(sound) },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            Color.White.copy(alpha = 0.15f),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = if (currentPlayingSound == sound)
                                            Icons.Default.Stop
                                        else
                                            Icons.Default.PlayArrow,
                                        contentDescription = if (currentPlayingSound == sound) "Durdur" else "Dinle",
                                        tint = Color.White
                                    )
                                }

                                Text(
                                    text = getSoundDisplayName(sound),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White
                                )
                            }

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
        "system_ringtone" -> "Sistem Zil Sesi"
        "kus_sesi" -> "Kuş Sesi"
        "ezan_mekke" -> "Mekke Ezanı"
        "ezan_medine" -> "Medine Ezanı"
        "ezan_istanbul" -> "İstanbul Ezanı"
        "ezan_hafiz" -> "Hafız Ezanı"
        else -> "Sistem Zil Sesi"
    }
}