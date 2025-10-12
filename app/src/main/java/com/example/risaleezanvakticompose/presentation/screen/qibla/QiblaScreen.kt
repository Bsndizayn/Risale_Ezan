package com.example.risaleezanvakticompose.presentation.screen.qibla

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.risaleezanvakticompose.R
import com.example.risaleezanvakticompose.presentation.navigation.Screen
import com.example.risaleezanvakticompose.util.QiblaAccuracy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    navController: NavController,
    viewModel: QiblaViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val qiblaDirection by viewModel.qiblaDirection.collectAsState()
    val qiblaArrowRotation by viewModel.qiblaArrowRotation.collectAsState()
    val qiblaAccuracy by viewModel.qiblaAccuracy.collectAsState()
    val isPhoneFlat by viewModel.isPhoneFlat.collectAsState()
    val isSensorAvailable by viewModel.isSensorAvailable.collectAsState()
    val hasLocation by viewModel.hasLocation.collectAsState()

    var showPermissionDialog by remember { mutableStateOf(false) }
    var showLocationSettingsDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.checkLocationAndRefresh()
        } else {
            showPermissionDialog = true
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

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Konum İzni Gerekli") },
            text = { Text("Kıble yönünü gösterebilmek için konum izni gereklidir. Lütfen ayarlardan konum iznini verin.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Ayarlara Git")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    if (showLocationSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showLocationSettingsDialog = false },
            title = { Text("GPS Kapalı") },
            text = { Text("GPS servisiniz kapalı görünüyor. Kıble yönünü gösterebilmek için GPS'i açmanız gerekir.") },
            confirmButton = {
                TextButton(onClick = {
                    showLocationSettingsDialog = false
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }) {
                    Text("GPS'i Aç")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationSettingsDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kıble Pusulası") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { padding ->
        when {
            !isSensorAvailable -> {
                NoSensorAvailable(modifier = Modifier.padding(padding))
            }
            !hasLocation -> {
                NoLocationAvailable(
                    modifier = Modifier.padding(padding),
                    onAddLocation = {
                        navController.navigate(Screen.Main.LocationSelection.ROUTE)
                    },
                    onCheckGPS = {
                        showLocationSettingsDialog = true
                    }
                )
            }
            qiblaDirection == null -> {
                LoadingQibla(modifier = Modifier.padding(padding))
            }
            else -> {
                QiblaContent(
                    qiblaDirection = qiblaDirection!!,
                    qiblaArrowRotation = qiblaArrowRotation,
                    qiblaAccuracy = qiblaAccuracy,
                    isPhoneFlat = isPhoneFlat,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun NoLocationAvailable(
    modifier: Modifier = Modifier,
    onAddLocation: () -> Unit,
    onCheckGPS: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOff,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Text(
                text = "Konum Bulunamadı",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Kıble yönünü gösterebilmek için:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "• GPS'iniz açık olmalı",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Konum izni verilmiş olmalı",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Kayıtlı bir konumunuz olmalı",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = onCheckGPS,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.GpsFixed, null)
                Spacer(Modifier.width(8.dp))
                Text("GPS Ayarlarını Kontrol Et")
            }

            OutlinedButton(
                onClick = onAddLocation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AddLocation, null)
                Spacer(Modifier.width(8.dp))
                Text("Konum Ekle")
            }
        }
    }
}

@Composable
fun QiblaContent(
    qiblaDirection: Float,
    qiblaArrowRotation: Float,
    qiblaAccuracy: QiblaAccuracy?,
    isPhoneFlat: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (!isPhoneFlat) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF5722).copy(alpha = 0.9f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ScreenRotation,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = "Telefonu yatay tutun",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Kıble Yönü",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
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
                                color = Color(0xFFFF9800)
                            )
                        }
                        QiblaAccuracy.TURN_RIGHT -> {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TurnRight,
                                    contentDescription = null
                                )
                                Text(
                                    text = "SAĞA DÖNÜN",
                                    style = MaterialTheme.typography.titleMedium
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
                                    contentDescription = null
                                )
                                Text(
                                    text = "SOLA DÖNÜN",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        else -> {
                            Text(
                                text = "Doğruluk: Bekleniyor...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .size(320.dp)
                .weight(1f, fill = false),
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Kıble: ${qiblaDirection.toInt()}°",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Pusulayı kalibre etmek için telefonu 8 şeklinde hareket ettirin",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LoadingQibla(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Konum bilgisi yükleniyor...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun NoSensorAvailable(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SensorsOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Sensör Bulunamadı",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Cihazınızda pusula sensörü bulunamadı. Kıble yönünü manuel olarak bulmanız gerekecek.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}