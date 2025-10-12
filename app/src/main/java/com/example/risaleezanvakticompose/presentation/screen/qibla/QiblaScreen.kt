package com.example.risaleezanvakticompose.presentation.screen.qibla

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.risaleezanvakticompose.R
import com.example.risaleezanvakticompose.util.QiblaAccuracy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    onBackClick: () -> Unit = {},
    viewModel: QiblaViewModel = hiltViewModel()
) {
    val qiblaDirection by viewModel.qiblaDirection.collectAsState()
    val qiblaArrowRotation by viewModel.qiblaArrowRotation.collectAsState()
    val qiblaAccuracy by viewModel.qiblaAccuracy.collectAsState()
    val isPhoneFlat by viewModel.isPhoneFlat.collectAsState()
    val isSensorAvailable by viewModel.isSensorAvailable.collectAsState()

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
        if (!isSensorAvailable) {
            NoSensorAvailable(modifier = Modifier.padding(padding))
        } else if (qiblaDirection == null) {
            LoadingQibla(modifier = Modifier.padding(padding))
        } else {
            QiblaContent(
                qiblaDirection = qiblaDirection!!,
                qiblaAccuracy = qiblaAccuracy,
                isPhoneFlat = isPhoneFlat,
                qiblaArrowRotation = qiblaArrowRotation,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun QiblaContent(
    qiblaDirection: Float,
    qiblaAccuracy: QiblaAccuracy?,
    isPhoneFlat: Boolean,
    qiblaArrowRotation: Float,
    modifier: Modifier = Modifier
) {
    val animatedRotation by animateFloatAsState(
        targetValue = qiblaArrowRotation,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearEasing
        ),
        label = "qibla_rotation",
        visibilityThreshold = 0.5f
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "KIBLE PUSULASI",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (!isPhoneFlat) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Telefonu yatay tutun",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFFF9800)
                    )
                }
            } else {
                Text(
                    text = "Kıble Yönü: ${qiblaDirection.toInt()}°",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                when (qiblaAccuracy) {
                    QiblaAccuracy.EXACT -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "KIBLE YÖNÜNDESİNİZ",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Kilitlendi",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF4CAF50)
                                )
                            }
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
                        Text(
                            text = "SAĞA DÖNÜN",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                    QiblaAccuracy.TURN_LEFT -> {
                        Text(
                            text = "SOLA DÖNÜN",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
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
                contentDescription = "Kıble Yönü",
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(animatedRotation),
                colorFilter = if (qiblaAccuracy == QiblaAccuracy.EXACT) {
                    androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFF4CAF50))
                } else {
                    null
                }
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        if (qiblaAccuracy == QiblaAccuracy.EXACT)
                            Color(0xFF4CAF50)
                        else
                            MaterialTheme.colorScheme.primary,
                        CircleShape
                    )
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InstructionRow(
                    icon = Icons.Default.PhoneAndroid,
                    text = "Telefonu yatay tutun"
                )
                InstructionRow(
                    icon = Icons.Default.CheckCircle,
                    text = "Seccade yeşil olunca Kıbleyi gösterir"
                )
                InstructionRow(
                    icon = Icons.Default.TrendingUp,
                    text = "Doğruluk için telefonu 8 şeklinde hareket ettirin"
                )
            }
        }
    }
}


@Composable
fun InstructionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
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
                text = "Konum bilgisi bekleniyor...",
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