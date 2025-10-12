package com.example.risaleezanvakticompose.presentation.screen.mainScreen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.risaleezanvakticompose.data.local.entities.NotificationSettings
import com.example.risaleezanvakticompose.data.local.entities.PrayerTimesEntity
import com.example.risaleezanvakticompose.ui.components.RiasalieArkaPlan
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    onProfileClick: () -> Unit,
    onLocationClick: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val countdown by viewModel.countdown.collectAsState()
    val notificationSettings by viewModel.notificationSettings.collectAsState()

    var showGpsDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // GPS açık mı kontrol et
    fun isGpsEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // GPS konumunu al - DÜZELTME: ViewModel state'ine güveniyoruz
    fun requestGpsLocation() {
        when {
            !locationPermissionState.status.isGranted -> {
                showPermissionDialog = true
            }

            !isGpsEnabled() -> {
                showGpsDialog = true
            }

            else -> {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            Log.d(
                                "MainScreen",
                                " GPS Location obtained: ${location.latitude}, ${location.longitude}"
                            )
                            viewModel.fetchCurrentLocationFromGps(
                                location.latitude,
                                location.longitude
                            )
                        } else {
                            fusedLocationClient.getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                null
                            ).addOnSuccessListener { currentLoc ->
                                if (currentLoc != null) {
                                    Log.d(
                                        "MainScreen",
                                        " Current Location obtained: ${currentLoc.latitude}, ${currentLoc.longitude}"
                                    )
                                    viewModel.fetchCurrentLocationFromGps(
                                        currentLoc.latitude,
                                        currentLoc.longitude
                                    )
                                } else {
                                    Log.e(
                                        "MainScreen",
                                        "Both lastLocation and currentLocation are null"
                                    )
                                    showGpsDialog = true
                                }
                            }.addOnFailureListener { e ->
                                Log.e("MainScreen", "getCurrentLocation failed: ${e.message}")
                                showGpsDialog = true
                            }
                        }
                    }.addOnFailureListener { e ->
                        Log.e("MainScreen", "getLastLocation failed: ${e.message}")
                        showGpsDialog = true
                    }
                } catch (e: SecurityException) {
                    Log.e("MainScreen", "SecurityException: ${e.message}")
                    showPermissionDialog = true
                }
            }
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Konum İzni Gerekli") },
            text = { Text("GPS konumunuzu almak için konum iznine ihtiyaç var. Lütfen izin verin.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    locationPermissionState.launchPermissionRequest()
                }) {
                    Text("İzin Ver")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    if (showGpsDialog) {
        AlertDialog(
            onDismissRequest = { showGpsDialog = false },
            title = { Text("GPS Kapalı veya Konum Alınamadı") },
            text = { Text("Konumunuzu alabilmek için GPS'inizi açmanız gerekir. Ayarlardan GPS'i açabilir veya manuel olarak konum seçebilirsiniz.") },
            confirmButton = {
                TextButton(onClick = {
                    showGpsDialog = false
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }) {
                    Text("GPS Ayarları")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showGpsDialog = false
                    onLocationClick()
                }) {
                    Text("Manuel Seç")
                }
            }
        )
    }

    RiasalieArkaPlan {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0.dp),
            topBar = {
                TopAppBar(
                    title = {
                        val location = currentLocation

                        Column(
                            modifier = Modifier.clickable(onClick = onLocationClick)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Konum",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = location?.placeName ?: "Konum Seç",
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1
                                )
                            }

                            location?.let {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = "${it.placeName}, ${it.region}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Konum Değiştir",
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = onLocationClick) {
                            Icon(
                                Icons.Default.AddLocation,
                                "Yer Ekle",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = onProfileClick) {
                            Icon(
                                Icons.Default.Person,
                                "Profil",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    ),
                    windowInsets = WindowInsets(0.dp)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
            ) {
                when (val state = uiState) {
                    is MainUiState.Loading -> {
                        // DÜZELTME: Daha bilgilendirici loading mesajı
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator()
                                Text(
                                    text = "Konumunuz alınıyor ve namaz vakitleri yükleniyor...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )
                            }
                        }
                    }

                    is MainUiState.Success -> {
                        val weeklyTimes by viewModel.getWeeklyPrayerTimes(state.prayerTimes.locationPlaceId)
                            .collectAsState(initial = emptyList())

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            NextPrayerCard(
                                nextPrayer = state.nextPrayer,
                                countdown = countdown
                            )

                            PrayerTimesList(
                                prayerTimes = state.prayerTimes,
                                notificationSettings = notificationSettings,
                                onNotificationToggle = { prayerName ->
                                    viewModel.toggleNotification(prayerName)
                                }
                            )

                            if (weeklyTimes.isNotEmpty()) {
                                WeeklyPrayerTimesSection(weeklyTimes)
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    is MainUiState.Error -> {
                        ErrorScreen(
                            message = state.message,
                            onRetry = {
                                currentLocation?.let {
                                    viewModel.refreshPrayerTimes()
                                }
                            }
                        )
                    }

                    is MainUiState.NoLocation -> {
                        NoLocationScreen(
                            onSelectManualLocation = onLocationClick,
                            onRequestGpsLocation = { requestGpsLocation() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoLocationScreen(
    onSelectManualLocation: () -> Unit,
    onRequestGpsLocation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Konum Seçilmedi",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Namaz vakitlerini görmek için\nkonumunuzu seçin",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Button(
                onClick = onRequestGpsLocation,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("GPS Konumumu Al")
            }

            OutlinedButton(
                onClick = onSelectManualLocation,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AddLocation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Manuel Konum Seç")
            }
        }
    }
}

@Composable
fun NextPrayerCard(
    nextPrayer: NextPrayerInfo,
    countdown: CountdownTime?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Sonraki Namaz",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = nextPrayer.name,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = nextPrayer.time,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Normal
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline
            )

            Text(
                text = "Kalan Süre",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            countdown?.let {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TimeUnit(value = it.hours, label = "Saat")
                    Text(":", style = MaterialTheme.typography.headlineMedium)
                    TimeUnit(value = it.minutes, label = "Dakika")
                    Text(":", style = MaterialTheme.typography.headlineMedium)
                    TimeUnit(value = it.seconds, label = "Saniye")
                }
            }
        }
    }
}

@Composable
fun PrayerTimesList(
    prayerTimes: PrayerTimesEntity,
    notificationSettings: NotificationSettings?,
    onNotificationToggle: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bugünün Vakitleri",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            PrayerTimeRow(
                "İmsak",
                prayerTimes.imsak,
                isNotificationEnabled = notificationSettings?.imsakEnabled ?: true,
                onNotificationToggle = { onNotificationToggle("imsak") }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            PrayerTimeRow(
                "Güneş",
                prayerTimes.gunes,
                isNotificationEnabled = notificationSettings?.gunesEnabled ?: false,
                onNotificationToggle = { onNotificationToggle("gunes") }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            PrayerTimeRow(
                "Öğle",
                prayerTimes.ogle,
                isNotificationEnabled = notificationSettings?.ogleEnabled ?: true,
                onNotificationToggle = { onNotificationToggle("ogle") }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            PrayerTimeRow(
                "İkindi",
                prayerTimes.ikindi,
                isNotificationEnabled = notificationSettings?.ikindiEnabled ?: true,
                onNotificationToggle = { onNotificationToggle("ikindi") }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            PrayerTimeRow(
                "Akşam",
                prayerTimes.aksam,
                isNotificationEnabled = notificationSettings?.aksamEnabled ?: true,
                onNotificationToggle = { onNotificationToggle("aksam") }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            PrayerTimeRow(
                "Yatsı",
                prayerTimes.yatsi,
                isNotificationEnabled = notificationSettings?.yatsiEnabled ?: true,
                onNotificationToggle = { onNotificationToggle("yatsi") }
            )
        }
    }
}

@Composable
fun PrayerTimeRow(
    name: String,
    time: String,
    isNotificationEnabled: Boolean,
    onNotificationToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(onClick = onNotificationToggle) {
                Icon(
                    imageVector = if (isNotificationEnabled)
                        Icons.Default.Notifications
                    else
                        Icons.Default.NotificationsOff,
                    contentDescription = if (isNotificationEnabled)
                        "Bildirimi Kapat"
                    else
                        "Bildirimi Aç",
                    tint = if (isNotificationEnabled)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun WeeklyPrayerTimesSection(weeklyTimes: List<PrayerTimesEntity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Önümüzdeki 7 Gün",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            weeklyTimes.forEach { dayTimes ->
                WeeklyDayItem(dayTimes)
                if (dayTimes != weeklyTimes.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyDayItem(prayerTimes: PrayerTimesEntity) {
    var expanded by remember { mutableStateOf(false) }

    val date = try {
        java.time.LocalDate.parse(prayerTimes.date)
    } catch (e: Exception) {
        null
    }

    val dayName = date?.dayOfWeek?.getDisplayName(
        java.time.format.TextStyle.FULL,
        java.util.Locale("tr")
    )?.replaceFirstChar { it.uppercase() } ?: ""

    val formattedDate = date?.format(
        java.time.format.DateTimeFormatter.ofPattern("dd MMMM", java.util.Locale("tr"))
    ) ?: ""

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prayerTimes.imsak,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (expanded) {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SimplePrayerRow("İmsak", prayerTimes.imsak)
                SimplePrayerRow("Güneş", prayerTimes.gunes)
                SimplePrayerRow("Öğle", prayerTimes.ogle)
                SimplePrayerRow("İkindi", prayerTimes.ikindi)
                SimplePrayerRow("Akşam", prayerTimes.aksam)
                SimplePrayerRow("Yatsı", prayerTimes.yatsi)
            }
        }
    }
}

@Composable
fun SimplePrayerRow(name: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TimeUnit(value: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bir Hata Oluştu",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tekrar Dene")
        }
    }
}