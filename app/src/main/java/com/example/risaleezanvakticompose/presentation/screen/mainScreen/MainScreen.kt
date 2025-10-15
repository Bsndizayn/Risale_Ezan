package com.example.risaleezanvakticompose.presentation.screen.mainScreen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.risaleezanvakticompose.data.local.entities.NotificationSettings
import com.example.risaleezanvakticompose.data.local.entities.PrayerTimesEntity
import com.example.risaleezanvakticompose.util.PermissionState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onProfileClick: () -> Unit,
    onLocationClick: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    val uiState by viewModel.uiState.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val countdown by viewModel.countdown.collectAsState()
    val notificationSettings by viewModel.notificationSettings.collectAsState()
    val currentQuote by viewModel.currentQuote.collectAsState()
    val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsState()

    var showGpsTimeoutDialog by remember { mutableStateOf(false) }
    var isQuoteExpanded by remember { mutableStateOf(false) }
    var isWeeklyExpanded by remember { mutableStateOf(false) }

    var showLocationPermissionEducationalDialog by remember { mutableStateOf(false) }
    var showLocationPermissionSettingsDialog by remember { mutableStateOf(false) }

    var cancellationTokenSource: com.google.android.gms.tasks.CancellationTokenSource? by remember { mutableStateOf(null) }
    var timeoutHandler: android.os.Handler? by remember { mutableStateOf(null) }

    // Location Settings Launcher - Android'in native GPS açma dialogu için
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // Kullanıcı GPS'i açtı, şimdi konum alabilirz
            startGpsLocationRequest(
                context = context,
                viewModel = viewModel,
                onTimeout = { showGpsTimeoutDialog = true },
                cancellationTokenSourceSetter = { cancellationTokenSource = it },
                timeoutHandlerSetter = { timeoutHandler = it }
            )
        } else {
            // Kullanıcı GPS açmayı reddetti, manuel seçim sunalım
            onLocationClick()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // İzin istendi olarak işaretle
        com.example.risaleezanvakticompose.util.PermissionManager(context).markLocationPermissionRequested()

        if (isGranted) {
            // İzin verildiyse, GPS kontrolü yap
            checkLocationSettingsAndRequest(
                context = context,
                onSettingsOk = {
                    startGpsLocationRequest(
                        context = context,
                        viewModel = viewModel,
                        onTimeout = { showGpsTimeoutDialog = true },
                        cancellationTokenSourceSetter = { cancellationTokenSource = it },
                        timeoutHandlerSetter = { timeoutHandler = it }
                    )
                },
                onSettingsNeedChange = { intentSenderRequest ->
                    locationSettingsLauncher.launch(intentSenderRequest)
                },
                onSettingsCheckFailed = {
                    // Ayarlar kontrol edilemedi ama yine de deneyelim
                    startGpsLocationRequest(
                        context = context,
                        viewModel = viewModel,
                        onTimeout = { showGpsTimeoutDialog = true },
                        cancellationTokenSourceSetter = { cancellationTokenSource = it },
                        timeoutHandlerSetter = { timeoutHandler = it }
                    )
                }
            )
        }
    }

    fun requestGpsLocation() {
        val activity = context.findActivity() ?: return
        val permissionManager = com.example.risaleezanvakticompose.util.PermissionManager(context)

        when (permissionManager.getLocationPermissionState(activity)) {
            is PermissionState.GRANTED -> {
                // İzin var, GPS ayarlarını kontrol et
                checkLocationSettingsAndRequest(
                    context = context,
                    onSettingsOk = {
                        startGpsLocationRequest(
                            context = context,
                            viewModel = viewModel,
                            onTimeout = { showGpsTimeoutDialog = true },
                            cancellationTokenSourceSetter = { cancellationTokenSource = it },
                            timeoutHandlerSetter = { timeoutHandler = it }
                        )
                    },
                    onSettingsNeedChange = { intentSenderRequest ->
                        locationSettingsLauncher.launch(intentSenderRequest)
                    },
                    onSettingsCheckFailed = {
                        // Ayarlar kontrol edilemedi ama yine de deneyelim
                        startGpsLocationRequest(
                            context = context,
                            viewModel = viewModel,
                            onTimeout = { showGpsTimeoutDialog = true },
                            cancellationTokenSourceSetter = { cancellationTokenSource = it },
                            timeoutHandlerSetter = { timeoutHandler = it }
                        )
                    }
                )
            }
            is PermissionState.DENIED -> {
                showLocationPermissionEducationalDialog = true
            }
            is PermissionState.PERMANENTLY_DENIED -> {
                showLocationPermissionSettingsDialog = true
            }
        }
    }

    LaunchedEffect(Unit) {
        activity?.let { viewModel.updateNotificationPermissionState(it) }
    }

    DisposableEffect(Unit) {
        onDispose {
            cancellationTokenSource?.cancel()
            timeoutHandler?.removeCallbacksAndMessages(null)
        }
    }

    if (showLocationPermissionEducationalDialog) {
        AlertDialog(
            onDismissRequest = { showLocationPermissionEducationalDialog = false },
            title = { Text("Konum İzni") },
            text = {
                Text(
                    "GPS konumunuzu alabilmek için konum iznine ihtiyacımız var.\n\n" +
                            "Bu sayede bulunduğunuz yerin namaz vakitlerini otomatik gösterebiliriz."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLocationPermissionEducationalDialog = false
                    // İzin istemeden önce kaydet
                    com.example.risaleezanvakticompose.util.PermissionManager(context).markLocationPermissionRequested()
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }) {
                    Text("İzin Ver")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showLocationPermissionEducationalDialog = false
                    onLocationClick()
                }) {
                    Text("Manuel Seç")
                }
            }
        )
    }

    if (showLocationPermissionSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showLocationPermissionSettingsDialog = false },
            title = { Text("Konum İzni Gerekli") },
            text = {
                Text(
                    "Konum iznini kalıcı olarak reddetmişsiniz.\n\n" +
                            "Ayarlar > İzinler > Konum'dan konum iznini açabilirsiniz."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLocationPermissionSettingsDialog = false
                    val permissionManager = com.example.risaleezanvakticompose.util.PermissionManager(context)
                    permissionManager.openAppSettings()
                }) {
                    Text("Ayarlara Git")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showLocationPermissionSettingsDialog = false
                    onLocationClick()
                }) {
                    Text("Manuel Seç")
                }
            }
        )
    }

    if (showGpsTimeoutDialog) {
        AlertDialog(
            onDismissRequest = { showGpsTimeoutDialog = false },
            title = { Text("GPS Konumu Alınamadı") },
            text = {
                Text(
                    "GPS sinyali bulunamadı veya çok zayıf.\n\n" +
                            "Lütfen:\n" +
                            "• Dışarı çıkın veya pencere kenarına gidin\n" +
                            "• Birkaç saniye bekleyin\n" +
                            "• Veya manuel olarak konum seçin"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showGpsTimeoutDialog = false
                    requestGpsLocation()
                }) {
                    Text("Tekrar Dene")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showGpsTimeoutDialog = false
                    onLocationClick()
                }) {
                    Text("Manuel Seç")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                            hasNotificationPermission = hasNotificationPermission,
                            onNotificationToggle = { }
                        )

                        if (weeklyTimes.isNotEmpty()) {
                            WeeklyPrayerTimesExpandable(
                                weeklyTimes = weeklyTimes.take(30),
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

private fun checkLocationSettingsAndRequest(
    context: Context,
    onSettingsOk: () -> Unit,
    onSettingsNeedChange: (IntentSenderRequest) -> Unit,
    onSettingsCheckFailed: () -> Unit
) {
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10000L
    ).build()

    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .setAlwaysShow(true) // GPS kapalıysa dialogu göster

    val client: SettingsClient = LocationServices.getSettingsClient(context)
    val task = client.checkLocationSettings(builder.build())

    task.addOnSuccessListener {
        // GPS açık ve hazır
        Log.d("MainScreen", "Location settings OK")
        onSettingsOk()
    }

    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            try {
                Log.d("MainScreen", "Location settings need to be changed, showing dialog")
                val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                onSettingsNeedChange(intentSenderRequest)
            } catch (sendEx: IntentSender.SendIntentException) {
                Log.e("MainScreen", "Error showing location settings dialog", sendEx)
                onSettingsCheckFailed()
            }
        } else {
            Log.e("MainScreen", "Location settings check failed", exception)
            onSettingsCheckFailed()
        }
    }
}

private fun startGpsLocationRequest(
    context: Context,
    viewModel: MainViewModel,
    onTimeout: () -> Unit,
    cancellationTokenSourceSetter: (com.google.android.gms.tasks.CancellationTokenSource) -> Unit,
    timeoutHandlerSetter: (android.os.Handler) -> Unit
) {
    if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
        Log.e("MainScreen", "İzin yok, GPS başlatılamadı")
        return
    }

    Log.d("MainScreen", "GPS konumu alınıyor...")
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    try {
        val cancellationTokenSource = com.google.android.gms.tasks.CancellationTokenSource()
        cancellationTokenSourceSetter(cancellationTokenSource)

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                Log.d("MainScreen", "Konum alındı: ${location.latitude}, ${location.longitude}")
                viewModel.fetchCurrentLocationFromGps(location.latitude, location.longitude)
            } else {
                Log.d("MainScreen", "getCurrentLocation null")
                onTimeout()
            }
        }.addOnFailureListener { exception ->
            Log.e("MainScreen", "getCurrentLocation başarısız: ${exception.message}")
            onTimeout()
        }

        val timeoutHandler = android.os.Handler(android.os.Looper.getMainLooper())
        timeoutHandlerSetter(timeoutHandler)

        timeoutHandler.postDelayed({
            Log.e("MainScreen", "GPS timeout - 20 saniye içinde konum alınamadı")
            cancellationTokenSource.cancel()
            onTimeout()
        }, 20000)

    } catch (e: SecurityException) {
        Log.e("MainScreen", "SecurityException: ${e.message}", e)
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
                    // ✅ Hem Miladi hem Hicri tarih
                    Text(
                        text = formatDateWithHijri(currentDate),
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
                            text = "Vecize",
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
    hasNotificationPermission: Boolean,
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
            val today = LocalDate.now().toString()
            val isShowingTomorrow = prayerTimes.date != today

            val title = if (isShowingTomorrow) {
                "Yarının Vakitleri"
            } else {
                "Bugünün Vakitleri"
            }

            Text(
                text = title,
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
                    name = "İmsak",
                    time = prayerTimes.imsak,
                    isNotificationEnabled = notificationSettings?.imsakEnabled ?: true,
                    hasPermission = hasNotificationPermission,
                    onToggle = { onNotificationToggle("imsak") }
                )
                CompactPrayerColumn(
                    name = "Güneş",
                    time = prayerTimes.gunes,
                    isNotificationEnabled = notificationSettings?.gunesEnabled ?: false,
                    hasPermission = hasNotificationPermission,
                    onToggle = { onNotificationToggle("gunes") }
                )
                CompactPrayerColumn(
                    name = "Öğle",
                    time = prayerTimes.ogle,
                    isNotificationEnabled = notificationSettings?.ogleEnabled ?: true,
                    hasPermission = hasNotificationPermission,
                    onToggle = { onNotificationToggle("ogle") }
                )
                CompactPrayerColumn(
                    name = "İkindi",
                    time = prayerTimes.ikindi,
                    isNotificationEnabled = notificationSettings?.ikindiEnabled ?: true,
                    hasPermission = hasNotificationPermission,
                    onToggle = { onNotificationToggle("ikindi") }
                )
                CompactPrayerColumn(
                    name = "Yatsı",
                    time = prayerTimes.yatsi,
                    isNotificationEnabled = notificationSettings?.yatsiEnabled ?: true,
                    hasPermission = hasNotificationPermission,
                    onToggle = { onNotificationToggle("yatsi") }
                )
            }
        }
    }
}

@Composable
fun CompactPrayerColumn(
    name: String,
    time: String,
    isNotificationEnabled: Boolean,
    hasPermission: Boolean,
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
            onClick = { },
            modifier = Modifier.size(28.dp),
            enabled = false
        ) {
            val iconColor = when {
                !hasPermission -> Color.White.copy(alpha = 0.3f)
                isNotificationEnabled -> Color(0xFFFCD34D)
                else -> Color.White.copy(alpha = 0.4f)
            }

            val icon = when {
                !hasPermission -> Icons.Default.NotificationsOff
                isNotificationEnabled -> Icons.Default.Notifications
                else -> Icons.Default.NotificationsOff
            }

            Icon(
                imageVector = icon,
                contentDescription = if (!hasPermission) {
                    "Bildirim izni gerekli"
                } else if (isNotificationEnabled) {
                    "Bildirim açık"
                } else {
                    "Bildirim kapalı"
                },
                tint = iconColor,
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
                    text = "Gelecek 30 Gün", // ✅ 5 Gün -> 30 Gün
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
                WeeklyTimeRow("Güneş", prayerTimes.gunes)
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

private fun formatDateWithHijri(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)

        // Miladi tarih
        val gregorianFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("tr"))
        val gregorianDate = date.format(gregorianFormatter)

        // Hicri tarih hesaplama
        val hijriDate = gregorianToHijri(date)

        "$gregorianDate\n$hijriDate"
    } catch (e: Exception) {
        dateString
    }
}

private fun gregorianToHijri(gregorianDate: LocalDate): String {
    val year = gregorianDate.year
    val month = gregorianDate.monthValue
    val day = gregorianDate.dayOfMonth

    // Julian Day hesaplama
    val a = floor((14 - month) / 12.0).toInt()
    val y = year + 4800 - a
    val m = month + 12 * a - 3

    val jd = day + floor((153 * m + 2) / 5.0).toInt() +
            365 * y + floor(y / 4.0).toInt() -
            floor(y / 100.0).toInt() + floor(y / 400.0).toInt() - 32045

    // Hicri tarihe çevirme
    val l = jd - 1948440 + 10632
    val n = floor((l - 1) / 10631.0).toInt()
    val l2 = l - 10631 * n + 354
    val j = (floor((10985 - l2) / 5316.0).toInt()) *
            (floor((50 * l2) / 17719.0).toInt()) +
            (floor(l2 / 5670.0).toInt()) *
            (floor((43 * l2) / 15238.0).toInt())
    val l3 = l2 - (floor((30 - j) / 15.0).toInt()) *
            (floor((17719 * j) / 50.0).toInt()) -
            (floor(j / 16.0).toInt()) *
            (floor((15238 * j) / 43.0).toInt()) + 29

    val hijriMonth = floor((24 * l3) / 709.0).toInt()
    val hijriDay = l3 - floor((709 * hijriMonth) / 24.0).toInt()
    val hijriYear = 30 * n + j - 30

    // Hicri ay isimleri
    val hijriMonthNames = listOf(
        "Muharrem", "Safer", "Rebiülevvel", "Rebiülahir",
        "Cemaziyelevvel", "Cemaziyelahir", "Recep", "Şaban",
        "Ramazan", "Şevval", "Zilkade", "Zilhicce"
    )

    val monthName = if (hijriMonth in 1..12) {
        hijriMonthNames[hijriMonth - 1]
    } else {
        "Muharrem"
    }

    return "$hijriDay $monthName $hijriYear"
}