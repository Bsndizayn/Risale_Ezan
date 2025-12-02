package com.example.risaleezanvakticompose.presentation.screen.mainScreen

// ==============================================================================
// BÖLÜM 1: ANDROID VE COMPOSE İMPORTLARI
// ==============================================================================
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.ArrowBackIosNew // YENİ: Sol Ok
import androidx.compose.material.icons.filled.ArrowForwardIos // YENİ: Sağ Ok
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch // YENİ: Scroll işlemi için coroutine
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.floor
import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Settings
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource
import java.time.format.TextStyle
import androidx.compose.animation.core.rememberInfiniteTransition
import com.example.risaleezanvakticompose.ui.theme.BarlaFamily
// ==============================================================================
// BÖLÜM 2: UYGULAMA İMPORTLARI (Kendi Modelleri ve Tema)
// ==============================================================================
import com.example.risaleezanvakticompose.R
import com.example.risaleezanvakticompose.data.local.entities.NotificationSettings
import com.example.risaleezanvakticompose.data.local.entities.PrayerTimesEntity
import com.example.risaleezanvakticompose.data.local.entities.SavedLocation
import com.example.risaleezanvakticompose.ui.theme.DarkTextPrimary
import com.example.risaleezanvakticompose.ui.theme.DividerLight
import com.example.risaleezanvakticompose.ui.theme.GoldColor
import com.example.risaleezanvakticompose.ui.theme.GoldLight
import com.example.risaleezanvakticompose.ui.theme.PageBeige
import com.example.risaleezanvakticompose.ui.theme.PageCream
import com.example.risaleezanvakticompose.ui.theme.RisaleRed
import com.example.risaleezanvakticompose.ui.theme.RisaleRedDark
import com.example.risaleezanvakticompose.ui.theme.RisaleSans
import com.example.risaleezanvakticompose.ui.theme.ScheherazadeFamily
import com.example.risaleezanvakticompose.util.PermissionManager
import com.example.risaleezanvakticompose.util.PermissionState


// ==============================================================================
// BÖLÜM 3: ANA EKRAN COMPOSE FONKSİYONU
// ==============================================================================

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

    var isQuoteExpanded by remember { mutableStateOf(true) }
    var isWeeklyExpanded by remember { mutableStateOf(true) }

    var showLocationPermissionEducationalDialog by remember { mutableStateOf(false) }
    var showLocationPermissionSettingsDialog by remember { mutableStateOf(false) }

    var cancellationTokenSource: CancellationTokenSource? by remember { mutableStateOf(null) }
    var timeoutHandler: Handler? by remember { mutableStateOf(null) }

    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startGpsLocationRequest(
                context = context,
                viewModel = viewModel,
                onTimeout = { onLocationClick() },
                cancellationTokenSourceSetter = { cancellationTokenSource = it },
                timeoutHandlerSetter = { timeoutHandler = it }
            )
        } else {
            onLocationClick()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        PermissionManager(context).markLocationPermissionRequested()

        if (isGranted) {
            checkLocationSettingsAndRequest(
                context = context,
                onSettingsOk = {
                    startGpsLocationRequest(
                        context = context,
                        viewModel = viewModel,
                        onTimeout = { onLocationClick() },
                        cancellationTokenSourceSetter = { cancellationTokenSource = it },
                        timeoutHandlerSetter = { timeoutHandler = it }
                    )
                },
                onSettingsNeedChange = { intentSenderRequest ->
                    locationSettingsLauncher.launch(intentSenderRequest)
                },
                onSettingsCheckFailed = {
                    startGpsLocationRequest(
                        context = context,
                        viewModel = viewModel,
                        onTimeout = { onLocationClick() },
                        cancellationTokenSourceSetter = { cancellationTokenSource = it },
                        timeoutHandlerSetter = { timeoutHandler = it }
                    )
                }
            )
        }
    }

    fun requestGpsLocation() {
        val activity = context.findActivity() ?: return
        val permissionManager = PermissionManager(context)

        when (permissionManager.getLocationPermissionState(activity)) {
            is PermissionState.GRANTED -> {
                checkLocationSettingsAndRequest(
                    context = context,
                    onSettingsOk = {
                        startGpsLocationRequest(
                            context = context,
                            viewModel = viewModel,
                            onTimeout = { onLocationClick() },
                            cancellationTokenSourceSetter = { cancellationTokenSource = it },
                            timeoutHandlerSetter = { timeoutHandler = it }
                        )
                    },
                    onSettingsNeedChange = { intentSenderRequest ->
                        locationSettingsLauncher.launch(intentSenderRequest)
                    },
                    onSettingsCheckFailed = {
                        startGpsLocationRequest(
                            context = context,
                            viewModel = viewModel,
                            onTimeout = { onLocationClick() },
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
                    "Konum izni kalıcı olarak reddedilmiş.\n\n" +
                            "Uygulama ayarlarından konum iznini etkinleştirebilirsiniz."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLocationPermissionSettingsDialog = false
                    val permissionManager = PermissionManager(context)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            GlassTopBar(
                location = currentLocation,
                currentDate = LocalDate.now().toString(),
                onLocationClick = onLocationClick,
                onProfileClick = onProfileClick
            )

            when (val state = uiState) {
                is MainUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GoldColor)
                    }
                }

                is MainUiState.NoLocation -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOff,
                            contentDescription = "Konum yok",
                            modifier = Modifier.size(80.dp),
                            tint = GoldColor.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Konum Seçilmedi",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = ScheherazadeFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Namaz vakitlerini görebilmek için konum seçmeniz gerekiyor",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { requestGpsLocation() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldColor
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "GPS",
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF1A1A1A)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GPS ile Konumu Bul",
                                color = Color(0xFF1A1A1A),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = onLocationClick,
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, GoldColor.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Manuel konum",
                                tint = GoldColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Manuel Konum Seç",
                                color = GoldColor
                            )
                        }
                    }
                }

                is MainUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        if (!hasNotificationPermission) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = GoldColor.copy(alpha = 0.9f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
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
                                            Icon(
                                                imageVector = Icons.Default.NotificationsOff,
                                                contentDescription = "Bildirim kapalı",
                                                tint = Color(0xFF1A1A1A),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = "Bildirim İzni Gerekli",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = Color(0xFF1A1A1A),
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Namaz vakti bildirimlerini almak için izin verin",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFF1A1A1A).copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                        IconButton(
                                            onClick = {
                                                val activity = context.findActivity()
                                                activity?.let {
                                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                        data = Uri.fromParts("package", context.packageName, null)
                                                    }
                                                    context.startActivity(intent)
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = "Ayarlara git",
                                                tint = Color(0xFF1A1A1A)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            GlassHeroCard(
                                nextPrayer = state.nextPrayer,
                                countdown = countdown,
                                currentDate = LocalDate.now().toString()
                            )
                        }

                        item {
                            RisaleQuoteExpandable(
                                quote = currentQuote,
                                isExpanded = isQuoteExpanded,
                                onToggle = { isQuoteExpanded = !isQuoteExpanded }
                            )
                        }

                        item {
                            CompactPrayerTimesCard(
                                prayerTimes = state.prayerTimes,
                                notificationSettings = notificationSettings,
                                hasNotificationPermission = hasNotificationPermission,
                                onNotificationToggle = { prayerName ->
                                    viewModel.toggleNotification(prayerName)
                                }
                            )
                        }

                        item {
                            val weeklyTimes by viewModel.getWeeklyPrayerTimes(currentLocation?.placeId ?: 0)
                                .collectAsState(initial = emptyList())

                            WeeklyPrayerTimesExpandable(
                                weeklyTimes = weeklyTimes.take(30),
                                isExpanded = isWeeklyExpanded,
                                onToggle = { isWeeklyExpanded = !isWeeklyExpanded }
                            )
                        }
                    }
                }

                is MainUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { viewModel.refreshPrayerTimes() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldColor,
                                    contentColor = Color(0xFF1A1A1A)
                                )
                            ) {
                                Text("Tekrar Dene", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Image(
            painter = painterResource(id = R.drawable.ust_plan),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        GlassTopBar(
            location = currentLocation,
            currentDate = LocalDate.now().toString(),
            onLocationClick = onLocationClick,
            onProfileClick = onProfileClick
        )
    }
}


// ==============================================================================
// BÖLÜM 6: UI BİLEŞENLERİ (COMPOSE FONKSİYONLARI)
// ==============================================================================

@Composable
fun GlassTopBar(
    location: SavedLocation?,
    currentDate: String,
    onLocationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onLocationClick() }
                    .padding(start = 12.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = location?.placeName ?: "Konum Seç",
                    color = GoldColor,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = ScheherazadeFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = GoldColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun GlassHeroCard(
    nextPrayer: NextPrayerInfo,
    countdown: CountdownTime?,
    currentDate: String
) {
    val date = try { LocalDate.parse(currentDate) } catch (e: Exception) { LocalDate.now() }
    val gregorianFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("tr"))
    val gregorianDate = date.format(gregorianFormatter)
    val hijriDate = gregorianToHijri(date)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(0.dp),
        border = null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // DÜZELTME: Font Family BarlaFamily olarak değiştirildi.
            Text(
                text = nextPrayer.name.uppercase(Locale("tr")),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = BarlaFamily, // BarlaFamily geri getirildi
                    letterSpacing = 6.sp,
                    fontSize = 100.sp
                ),
                color = GoldColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = nextPrayer.time,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "EZAN VAKTİNE KALAN SÜRE",
                style = MaterialTheme.typography.labelSmall,
                color = GoldColor.copy(alpha = 0.6f),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            countdown?.let {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CountdownBox(value = it.hours, label = "SA")

                    Text(
                        text = ":",
                        color = GoldColor,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = RisaleSans,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )

                    CountdownBox(value = it.minutes, label = "DK")

                    Text(
                        text = ":",
                        color = GoldColor,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = RisaleSans,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )

                    CountdownBox(value = it.seconds, label = "SN")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = gregorianDate,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = ScheherazadeFamily,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "|",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = hijriDate,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = ScheherazadeFamily,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun CountdownBox(value: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = value.toString().padStart(2, '0'),
            color = GoldColor,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = RisaleSans
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp
        )
    }
}

@Composable
fun RisaleQuoteExpandable(
    quote: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val context = LocalContext.current
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF8B3A3A).copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFCFBF2),
                        Color(0xFFF2EBD4)
                    )
                )
            )
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
                            tint = Color(0xFF8B3A3A),
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Risale-i Nur'dan",
                                color = Color(0xFF8B3A3A),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = ScheherazadeFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Vecize",
                                color = Color(0xFF8B3A3A),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = ScheherazadeFamily
                                )
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color(0xFF8B3A3A),
                        modifier = Modifier.rotate(rotationAngle)
                    )
                }

                if (isExpanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color(0xFF8B3A3A).copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = quote,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = RisaleSans,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 22.sp
                        ),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "$quote\n\n RİSALE EZAN")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Vecizeyi Paylaş")
                                context.startActivity(shareIntent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Paylaş",
                                tint = Color(0xFF8B3A3A),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Paylaş",
                                color = Color(0xFF8B3A3A),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
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
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(0.dp),
        border = null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                color = GoldColor,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = ScheherazadeFamily,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PrayerTimeRowItem(
                    name = "İmsak", time = prayerTimes.imsak,
                    isNotificationEnabled = notificationSettings?.imsakEnabled ?: true,
                    hasPermission = hasNotificationPermission, onToggle = { onNotificationToggle("imsak") }
                )
                Divider(color = Color.White.copy(alpha = 0.1f))

                PrayerTimeRowItem(
                    name = "Güneş", time = prayerTimes.gunes,
                    isNotificationEnabled = notificationSettings?.gunesEnabled ?: false,
                    hasPermission = hasNotificationPermission, onToggle = { onNotificationToggle("gunes") }
                )
                Divider(color = Color.White.copy(alpha = 0.1f))

                PrayerTimeRowItem(
                    name = "Öğle", time = prayerTimes.ogle,
                    isNotificationEnabled = notificationSettings?.ogleEnabled ?: true,
                    hasPermission = hasNotificationPermission, onToggle = { onNotificationToggle("ogle") }
                )
                Divider(color = Color.White.copy(alpha = 0.1f))

                PrayerTimeRowItem(
                    name = "İkindi", time = prayerTimes.ikindi,
                    isNotificationEnabled = notificationSettings?.ikindiEnabled ?: true,
                    hasPermission = hasNotificationPermission, onToggle = { onNotificationToggle("ikindi") }
                )
                Divider(color = Color.White.copy(alpha = 0.1f))

                PrayerTimeRowItem(
                    name = "Akşam", time = prayerTimes.aksam,
                    isNotificationEnabled = notificationSettings?.aksamEnabled ?: true,
                    hasPermission = hasNotificationPermission, onToggle = { onNotificationToggle("aksam") }
                )
                Divider(color = Color.White.copy(alpha = 0.1f))

                PrayerTimeRowItem(
                    name = "Yatsı", time = prayerTimes.yatsi,
                    isNotificationEnabled = notificationSettings?.yatsiEnabled ?: true,
                    hasPermission = hasNotificationPermission, onToggle = { onNotificationToggle("yatsi") }
                )
            }
        }
    }
}

@Composable
fun PrayerTimeRowItem(
    name: String,
    time: String,
    isNotificationEnabled: Boolean,
    hasPermission: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = RisaleSans,
                fontWeight = FontWeight.Medium
            )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = time,
                color = GoldColor,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = RisaleSans,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(end = 8.dp)
            )

            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(32.dp),
                enabled = hasPermission
            ) {
                val iconColor = when {
                    !hasPermission -> Color.White.copy(alpha = 0.3f)
                    isNotificationEnabled -> GoldColor
                    else -> Color.White.copy(alpha = 0.4f)
                }

                val icon = when {
                    !hasPermission -> Icons.Default.NotificationsOff
                    isNotificationEnabled -> Icons.Default.Notifications
                    else -> Icons.Default.NotificationsOff
                }

                Icon(
                    imageVector = icon,
                    contentDescription = "Bildirim",
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


/**
 * Gelecek 30 Günlük Namaz Vakitlerini Gösteren Genişletilebilir Kart
 * GÜNCELLEME: Sağ ve Sol kaydırma okları eklendi.
 */
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

    var isLoading by remember { mutableStateOf(false) }
    var hasLoadedOnce by remember { mutableStateOf(false) }

    // GÜNCELLEME: Scroll state ve Coroutine Scope
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(weeklyTimes) {
        if (weeklyTimes.isNotEmpty()) {
            isLoading = false
            hasLoadedOnce = true
        }
    }

    LaunchedEffect(isExpanded) {
        if (isExpanded && weeklyTimes.isEmpty() && !hasLoadedOnce) {
            isLoading = true
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = RisaleRedDark
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GoldColor.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gelecek 30 Gün",
                    color = GoldColor,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = GoldColor,
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
                // GÜNCELLEME: İçeriği Box içine alıp okları ekliyoruz
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))

                        if (isLoading && weeklyTimes.isEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(scrollState),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                repeat(3) {
                                    SkeletonWeeklyCard()
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(scrollState),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                weeklyTimes.forEach { dayTimes ->
                                    WeeklyDayCard(dayTimes)
                                }
                            }
                        }
                    }

                    // GÜNCELLEME: Sol Ok
                    if (scrollState.value > 0) { // Sadece scroll başınd değilse göster
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    scrollState.animateScrollTo(scrollState.value - 300)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 4.dp)
                                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Geri",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // GÜNCELLEME: Sağ Ok
                    if (scrollState.value < scrollState.maxValue) { // Sadece scroll sonunda değilse göster
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    scrollState.animateScrollTo(scrollState.value + 300)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 4.dp)
                                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "İleri",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ... (SkeletonWeeklyCard, WeeklyDayCard, PrayerTimeRow, WeeklyTimeRow)

@Composable
fun SkeletonWeeklyCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(16.dp)
                    .background(
                        GoldColor.copy(alpha = alpha),
                        RoundedCornerShape(4.dp)
                    )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(12.dp)
                    .background(
                        Color.White.copy(alpha = alpha * 0.5f),
                        RoundedCornerShape(4.dp)
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Vakitler için placeholder'lar
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(6) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(10.dp)
                                .background(
                                    Color.White.copy(alpha = alpha * 0.4f),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                        Box(
                            modifier = Modifier
                                .width(35.dp)
                                .height(10.dp)
                                .background(
                                    GoldColor.copy(alpha = alpha * 0.6f),
                                    RoundedCornerShape(4.dp)
                                )
                        )
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
        TextStyle.SHORT,
        Locale("tr")
    )?.replaceFirstChar { it.uppercase() } ?: ""

    val formattedDate = date?.format(
        DateTimeFormatter.ofPattern("dd MMM", Locale("tr"))
    ) ?: ""

    Card(
        modifier = Modifier
            .width(120.dp)
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dayName,
                style = MaterialTheme.typography.titleSmall,
                color = GoldColor,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PrayerTimeRow("İmsak", prayerTimes.imsak)
                PrayerTimeRow("Güneş", prayerTimes.gunes)
                PrayerTimeRow("Öğle", prayerTimes.ogle)
                PrayerTimeRow("İkindi", prayerTimes.ikindi)
                PrayerTimeRow("Akşam", prayerTimes.aksam)
                PrayerTimeRow("Yatsı", prayerTimes.yatsi)
            }
        }
    }
}

@Composable
fun PrayerTimeRow(name: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 10.sp
        )
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = GoldColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
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
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = ScheherazadeFamily,
                fontSize = 11.sp
            )
        )
        Text(
            text = time,
            color = GoldColor,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = ScheherazadeFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        )
    }
}


// ==============================================================================
// BÖLÜM 7: YARDIMCI FONKSİYONLAR (LOCATION, TIME, UTILS)
// ==============================================================================

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
        .setAlwaysShow(true)

    val client: SettingsClient = LocationServices.getSettingsClient(context)
    val task = client.checkLocationSettings(builder.build())

    task.addOnSuccessListener {
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
    cancellationTokenSourceSetter: (CancellationTokenSource) -> Unit,
    timeoutHandlerSetter: (Handler) -> Unit
) {
    if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        Log.e("MainScreen", "İzin yok, GPS başlatılamadı")
        return
    }

    Log.d("MainScreen", "GPS konumu alınıyor...")
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    try {
        val cancellationTokenSource = CancellationTokenSource()
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

        val timeoutHandler = Handler(Looper.getMainLooper())
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

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

/**
 * Miladi tarihi ayrıştırır, Hicri tarihi hesaplar ve her ikisini birleştirir.
 */
private fun formatDateWithHijri(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)

        val gregorianFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("tr"))
        val gregorianDate = date.format(gregorianFormatter)

        val hijriDate = gregorianToHijri(date)

        "$gregorianDate\n$hijriDate"
    } catch (e: Exception) {
        dateString
    }
}

/**
 * Verilen Miladi tarihi Hicri takvime çevirir (Basit algoritma).
 */
private fun gregorianToHijri(gregorianDate: LocalDate): String {
    val year = gregorianDate.year
    val month = gregorianDate.monthValue
    val day = gregorianDate.dayOfMonth

    val a = floor((14 - month) / 12.0).toInt()
    val y = year + 4800 - a
    val m = month + 12 * a - 3

    val jd = day + floor((153 * m + 2) / 5.0).toInt() +
            365 * y + floor(y / 4.0).toInt() -
            floor(y / 100.0).toInt() + floor(y / 400.0).toInt() - 32045

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

    // Hata giderildi: Hesaplanan değişkenler burada String olarak birleştirilir.
    return "$hijriDay $monthName $hijriYear"
}