package com.example.risaleezanvakticompose.presentation.screen.qibla

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.risaleezanvakticompose.R
import com.example.risaleezanvakticompose.presentation.navigation.Screen
import com.example.risaleezanvakticompose.util.PermissionState
import com.example.risaleezanvakticompose.util.QiblaAccuracy
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    navController: NavController,
    viewModel: QiblaViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    val qiblaDirection by viewModel.qiblaDirection.collectAsState()
    val qiblaArrowRotation by viewModel.qiblaArrowRotation.collectAsState()
    val qiblaAccuracy by viewModel.qiblaAccuracy.collectAsState()
    val isPhoneFlat by viewModel.isPhoneFlat.collectAsState()
    val isSensorAvailable by viewModel.isSensorAvailable.collectAsState()
    val hasLocation by viewModel.hasLocation.collectAsState()

    var showPermissionEducationalDialog by remember { mutableStateOf(false) }
    var showPermissionSettingsDialog by remember { mutableStateOf(false) }

    // Location Settings Launcher - Android'in native GPS açma dialogu için
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // Kullanıcı GPS'i açtı, konum bilgisini yenile
            viewModel.checkLocationAndRefresh()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // İzin istendi olarak işaretle
        com.example.risaleezanvakticompose.util.PermissionManager(context).markLocationPermissionRequested()

        if (isGranted) {
            // İzin verildiyse GPS kontrolü yap
            checkLocationSettingsAndRequest(
                context = context,
                onSettingsOk = {
                    viewModel.checkLocationAndRefresh()
                },
                onSettingsNeedChange = { intentSenderRequest ->
                    locationSettingsLauncher.launch(intentSenderRequest)
                },
                onSettingsCheckFailed = {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }
            )
        }
    }

    // GPS Ayarlarını Kontrol Et butonuna basıldığında çağrılacak fonksiyon
    fun handleGpsButtonClick() {
        val activity = context.findActivity() ?: return
        val permissionManager = com.example.risaleezanvakticompose.util.PermissionManager(context)

        when (permissionManager.getLocationPermissionState(activity)) {
            is PermissionState.GRANTED -> {
                // İzin var, GPS ayarlarını kontrol et
                checkLocationSettingsAndRequest(
                    context = context,
                    onSettingsOk = {
                        viewModel.checkLocationAndRefresh()
                    },
                    onSettingsNeedChange = { intentSenderRequest ->
                        locationSettingsLauncher.launch(intentSenderRequest)
                    },
                    onSettingsCheckFailed = {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    }
                )
            }
            is PermissionState.DENIED -> {
                // İlk ret veya normal ret - Educational dialog göster
                showPermissionEducationalDialog = true
            }
            is PermissionState.PERMANENTLY_DENIED -> {
                // Kalıcı ret - Ayarlar dialogu göster
                showPermissionSettingsDialog = true
            }
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    if (showPermissionEducationalDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionEducationalDialog = false },
            title = { Text("Konum İzni Gerekli") },
            text = {
                Text(
                    "Kıble yönünü gösterebilmek için konum iznine ihtiyacımız var.\n\n" +
                            "Lütfen konum iznini verin."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionEducationalDialog = false
                    // İzin istemeden önce kaydet
                    com.example.risaleezanvakticompose.util.PermissionManager(context).markLocationPermissionRequested()
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }) {
                    Text("İzin Ver")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPermissionEducationalDialog = false
                }) {
                    Text("İptal")
                }
            }
        )
    }

    if (showPermissionSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionSettingsDialog = false },
            title = { Text("Konum İzni Gerekli") },
            text = {
                Text(
                    "Konum iznini kalıcı olarak reddetmişsiniz.\n\n" +
                            "Ayarlar > İzinler > Konum'dan konum iznini açabilirsiniz."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionSettingsDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Ayarlara Git")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPermissionSettingsDialog = false
                }) {
                    Text("İptal")
                }
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GlassQiblaTopBar()

            when {
                !isSensorAvailable -> {
                    NoSensorAvailable()
                }
                !hasLocation -> {
                    NoLocationAvailable(
                        onAddLocation = {
                            navController.navigate(Screen.Main.LocationSelection.ROUTE)
                        },
                        onCheckGPS = {
                            handleGpsButtonClick()
                        }
                    )
                }
                qiblaDirection == null -> {
                    LoadingQibla()
                }
                else -> {
                    QiblaContent(
                        qiblaDirection = qiblaDirection!!,
                        qiblaArrowRotation = qiblaArrowRotation,
                        qiblaAccuracy = qiblaAccuracy,
                        isPhoneFlat = isPhoneFlat
                    )
                }
            }
        }
    }
}
fun android.content.Context.findActivity(): android.app.Activity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is android.app.Activity) return context
        context = context.baseContext
    }
    return null
}

private fun checkLocationSettingsAndRequest(
    context: android.content.Context,
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
        Log.d("QiblaScreen", "Location settings OK")
        onSettingsOk()
    }

    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            // GPS kapalı veya ayarlar uygun değil - Android'in native dialogunu göster
            try {
                Log.d("QiblaScreen", "Location settings need to be changed, showing dialog")
                val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                onSettingsNeedChange(intentSenderRequest)
            } catch (sendEx: IntentSender.SendIntentException) {
                Log.e("QiblaScreen", "Error showing location settings dialog", sendEx)
                onSettingsCheckFailed()
            }
        } else {
            Log.e("QiblaScreen", "Location settings check failed", exception)
            onSettingsCheckFailed()
        }
    }
}

@Composable
fun GlassQiblaTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Kıble Pusulası",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NoLocationAvailable(
    onAddLocation: () -> Unit,
    onCheckGPS: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOff,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = Color.White
                )
            }

            Text(
                text = "Konum Bulunamadı",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Kıble yönünü gösterebilmek için:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    QiblaRequirement("GPS'iniz açık olmalı")
                    QiblaRequirement("Konum izni verilmiş olmalı")
                    QiblaRequirement("Kayıtlı bir konumunuz olmalı")
                }
            }

            Button(
                onClick = onCheckGPS,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.GpsFixed, null)
                Spacer(Modifier.width(8.dp))
                Text("GPS Ayarlarını Kontrol Et", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onAddLocation,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.AddLocation, null)
                Spacer(Modifier.width(8.dp))
                Text("Konum Ekle", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun QiblaRequirement(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun QiblaContent(
    qiblaDirection: Float,
    qiblaArrowRotation: Float,
    qiblaAccuracy: QiblaAccuracy?,
    isPhoneFlat: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isPhoneFlat) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF5722).copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ScreenRotation,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Telefonu yatay tutun",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Kıble Yönü",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (qiblaAccuracy) {
                        QiblaAccuracy.EXACT -> {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "DOĞRU YÖN",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                        QiblaAccuracy.VERY_CLOSE, QiblaAccuracy.CLOSE -> {
                            Text(
                                text = "YAKLAŞIYORSUNUZ",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFFF9800),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        QiblaAccuracy.TURN_RIGHT -> {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TurnRight,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Text(
                                    text = "SAĞA DÖNÜN",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        QiblaAccuracy.TURN_LEFT -> {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TurnLeft,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Text(
                                    text = "SOLA DÖNÜN",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        else -> {
                            Text(
                                text = "Doğruluk: Bekleniyor...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.size(320.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.compass_background),
                contentDescription = "Pusula",
                modifier = Modifier.fillMaxSize()
            )

            Image(
                painter = painterResource(id = R.drawable.qibla_arrow),
                contentDescription = "Kıble Ok",
                modifier = Modifier
                    .size(280.dp)
                    .rotate(qiblaArrowRotation)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Text(
                text = "Kıble: ${qiblaDirection.toInt()}°",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Pusulayı kalibre etmek için telefonu 8 şeklinde hareket ettirin",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun LoadingQibla() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
            Text(
                text = "Konum bilgisi yükleniyor...",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}

@Composable
fun NoSensorAvailable() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SensorsOff,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = Color.White
                )
            }
            Text(
                text = "Sensör Bulunamadı",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Text(
                text = "Cihazınızda pusula sensörü bulunamadı. Kıble yönünü manuel olarak bulmanız gerekecek.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}