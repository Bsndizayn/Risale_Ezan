package com.example.risaleezanvakticompose.presentation.screen.mainScreen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.risaleezanvakticompose.R
import com.example.risaleezanvakticompose.data.local.entities.NotificationSettings
import com.example.risaleezanvakticompose.data.local.entities.PrayerTimesEntity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    val currentQuote by viewModel.currentQuote.collectAsState()

    var showGpsDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var isQuoteExpanded by remember { mutableStateOf(false) }
    var isWeeklyExpanded by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    fun isGpsEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

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
                            viewModel.fetchCurrentLocationFromGps(
                                location.latitude,
                                location.longitude
                            )
                        } else {
                            fusedLocationClient.getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                null
                            )
                                .addOnSuccessListener { currentLoc ->
                                    if (currentLoc != null) {
                                        viewModel.fetchCurrentLocationFromGps(
                                            currentLoc.latitude,
                                            currentLoc.longitude
                                        )
                                    } else {
                                        showGpsDialog = true
                                    }
                                }
                        }
                    }
                } catch (e: SecurityException) {
                    showPermissionDialog = true
                }
            }
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Konum İzni Gerekli") },
            text = { Text("GPS konumunuzu almak için konum iznine ihtiyaç var.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    locationPermissionState.launchPermissionRequest()
                }) { Text("İzin Ver") }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) { Text("İptal") }
            }
        )
    }

    if (showGpsDialog) {
        AlertDialog(
            onDismissRequest = { showGpsDialog = false },
            title = { Text("GPS Kapalı") },
            text = { Text("Konumunuzu alabilmek için GPS'inizi açın veya manuel konum seçin.") },
            confirmButton = {
                TextButton(onClick = {
                    showGpsDialog = false
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }) { Text("GPS Ayarları") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showGpsDialog = false
                    onLocationClick()
                }) { Text("Manuel Seç") }
            }
        )
    }

//    Box(modifier = Modifier.fillMaxSize()) {
//        // Gradient Background
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    brush = Brush.verticalGradient(
//                        colors = listOf(
//                            Color(0xFF1E3A8A), // Koyu mavi
//                            Color(0xFF7C3AED), // Mor
//                            Color(0xFFF97316)  // Turuncu (gün batımı)
//                        )
//                    )
//                )
//        )
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        when (val state = uiState) {
            is MainUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Text(
                            text = "Namaz vakitleri yükleniyor...",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            is MainUiState.Success -> {
                val weeklyTimes by viewModel.getWeeklyPrayerTimes(state.prayerTimes.locationPlaceId)
                    .collectAsState(initial = emptyList())

                Column(modifier = Modifier.fillMaxSize()) {
                    GlassTopBar(
                        location = currentLocation,
                        currentDate = state.prayerTimes.date,
                        onLocationClick = onLocationClick,
                        onProfileClick = onProfileClick
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (currentQuote.isNotEmpty()) {
                            RisaleQuoteExpandable(
                                quote = currentQuote,
                                isExpanded = isQuoteExpanded,
                                onToggle = { isQuoteExpanded = !isQuoteExpanded }
                            )
                        }

                        GlassHeroCard(
                            nextPrayer = state.nextPrayer,
                            countdown = countdown
                        )

                        CompactPrayerTimesCard(
                            prayerTimes = state.prayerTimes,
                            notificationSettings = notificationSettings,
                            onNotificationToggle = { viewModel.toggleNotification(it) }
                        )

                        if (weeklyTimes.isNotEmpty()) {
                            WeeklyPrayerTimesExpandable(
                                weeklyTimes = weeklyTimes.take(5),
                                isExpanded = isWeeklyExpanded,
                                onToggle = { isWeeklyExpanded = !isWeeklyExpanded }
                            )
                        }
                    }
                }
            }

            is MainUiState.Error -> {
                ErrorScreen(
                    message = state.message,
                    onRetry = { currentLocation?.let { viewModel.refreshPrayerTimes() } }
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


@Composable
fun GlassTopBar(
    location: com.example.risaleezanvakticompose.data.local.entities.SavedLocation?,
    currentDate: String,
    onLocationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.10f))
                    .clickable { onLocationClick() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = location?.placeName ?: "Konum Seç",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = formatDateShort(currentDate),
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color.White.copy(alpha = 0.15f),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddLocationAlt,
                        contentDescription = "Konum Değiştir",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onProfileClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profil",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


@Composable
fun RisaleQuoteExpandable(
    quote: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Risale-i Nur'dan",
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Günün Sözü",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationAngle)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "\"$quote\"",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}


@Composable
fun GlassHeroCard(
    nextPrayer: NextPrayerInfo,
    countdown: CountdownTime?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "S O N R A K İ",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 2.sp
                )
            }

            Text(
                text = nextPrayer.name.uppercase(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = nextPrayer.time,
                style = MaterialTheme.typography.displaySmall,
                color = Color.White.copy(alpha = 0.9f)
            )

            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )

            countdown?.let {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CountdownBox(value = it.hours, label = "SA")
                    Text(":", color = Color.White, fontSize = 32.sp)
                    CountdownBox(value = it.minutes, label = "DK")
                    Text(":", color = Color.White, fontSize = 32.sp)
                    CountdownBox(value = it.seconds, label = "SN")
                }
            }
        }
    }
}

@Composable
fun CountdownBox(value: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = value.toString().padStart(2, '0'),
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp
        )
    }
}


@Composable
fun CompactPrayerTimesCard(
    prayerTimes: PrayerTimesEntity,
    notificationSettings: NotificationSettings?,
    onNotificationToggle: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "━━ Bugünün Vakitleri ━━",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CompactPrayerColumn(
                    "İmsak",
                    prayerTimes.imsak,
                    notificationSettings?.imsakEnabled ?: true
                ) {
                    onNotificationToggle("imsak")
                }
                CompactPrayerColumn(
                    "Öğle",
                    prayerTimes.ogle,
                    notificationSettings?.ogleEnabled ?: true
                ) {
                    onNotificationToggle("ogle")
                }
                CompactPrayerColumn(
                    "İkindi",
                    prayerTimes.ikindi,
                    notificationSettings?.ikindiEnabled ?: true
                ) {
                    onNotificationToggle("ikindi")
                }
                CompactPrayerColumn(
                    "Yatsı",
                    prayerTimes.yatsi,
                    notificationSettings?.yatsiEnabled ?: true
                ) {
                    onNotificationToggle("yatsi")
                }
            }
        }
    }
}

@Composable
fun CompactPrayerColumn(
    name: String,
    time: String,
    isNotificationEnabled: Boolean,
    onToggle: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = name,
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = time,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(
            onClick = onToggle,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = if (isNotificationEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                contentDescription = null,
                tint = if (isNotificationEnabled) Color(0xFFFCD34D) else Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun WeeklyPrayerTimesExpandable(
    weeklyTimes: List<PrayerTimesEntity>,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gelecek 5 Gün",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationAngle)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        weeklyTimes.forEach { dayTimes ->
                            WeeklyDayCard(dayTimes)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyDayCard(prayerTimes: PrayerTimesEntity) {
    val date = try {
        LocalDate.parse(prayerTimes.date)
    } catch (e: Exception) {
        null
    }

    val dayName = date?.dayOfWeek?.getDisplayName(
        java.time.format.TextStyle.SHORT,
        Locale("tr")
    )?.replaceFirstChar { it.uppercase() } ?: ""

    val formattedDate = date?.format(
        DateTimeFormatter.ofPattern("dd MMM", Locale("tr"))
    ) ?: ""

    Card(
        modifier = Modifier
            .width(140.dp)
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayName,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formattedDate,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.White.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                WeeklyTimeRow("İmsak", prayerTimes.imsak)
                WeeklyTimeRow("Öğle", prayerTimes.ogle)
                WeeklyTimeRow("İkindi", prayerTimes.ikindi)
                WeeklyTimeRow("Akşam", prayerTimes.aksam)
                WeeklyTimeRow("Yatsı", prayerTimes.yatsi)
            }
        }
    }
}

@Composable
fun WeeklyTimeRow(name: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            color = Color.White.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp
        )
        Text(
            text = time,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}


@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.White
            )
            Text(
                text = "Bir Hata Oluştu",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tekrar Dene", color = Color(0xFF1E3A8A))
            }
        }
    }
}

@Composable
fun NoLocationScreen(
    onSelectManualLocation: () -> Unit,
    onRequestGpsLocation: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.White
                )
            }

            Text(
                text = "Konum Seçilmedi",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Namaz vakitlerini görmek için\nkonumunuzu seçin",
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Button(
                    onClick = onRequestGpsLocation,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = Color(0xFF1E3A8A)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("GPS Konumumu Al", color = Color(0xFF1E3A8A))
                }

                OutlinedButton(
                    onClick = onSelectManualLocation,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddLocation, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manuel Konum Seç")
                }
            }
        }
    }
}


private fun formatDateShort(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("tr"))
        date.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}