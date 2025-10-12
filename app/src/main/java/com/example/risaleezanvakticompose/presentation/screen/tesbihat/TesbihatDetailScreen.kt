package com.example.risaleezanvakticompose.presentation.screen.tesbihat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.risaleezanvakticompose.domain.model.TesbihatItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TesbihatDetailScreen(
    onBackClick: () -> Unit,
    viewModel: TesbihatViewModel = hiltViewModel()
) {
    val content by viewModel.selectedContent.collectAsState()
    val counters by viewModel.counters.collectAsState()
    val totalProgress by remember {
        derivedStateOf { viewModel.getTotalProgress() }
    }

//    Box(modifier = Modifier.fillMaxSize()) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    brush = Brush.verticalGradient(
//                        colors = listOf(
//                            Color(0xFF8B0000),  // Koyu bordo (üst)
//                            Color(0xFFB22222),  // FireBrick kırmızı (orta)
//                            Color(0xFFD4AF37)   // Altın yaldız (alt)
//                        )
//                    )
//                )
//        )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            GlassTesbihatTopBar(
                title = content?.category?.displayName ?: "",
                progress = totalProgress,
                onBackClick = onBackClick
            )

            if (totalProgress > 0) {
                LinearProgressIndicator(
                    progress = { totalProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = Color(0xFF4CAF50),
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                content?.items?.let { items ->
                    items(items) { item ->
                        GlassTesbihatItemCard(
                            item = item,
                            currentCount = counters[item.id] ?: 0,
                            isCompleted = viewModel.isItemCompleted(item.id)
                        )
                    }
                }

                if (totalProgress >= 1.0f) {
                    item {
                        GlassCompletionCard()
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun GlassTesbihatTopBar(
    title: String,
    progress: Float,
    onBackClick: () -> Unit
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onBackClick,
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

                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (progress > 0) {
                        Text(
                            text = "${(progress * 100).toInt()}% tamamlandı",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GlassTesbihatItemCard(
    item: TesbihatItem,
    currentCount: Int,
    isCompleted: Boolean
) {
    var showDetails by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 1.03f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val rotationAngle by animateFloatAsState(
        targetValue = if (showDetails) 180f else 0f,
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { showDetails = !showDetails },
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                Color.White.copy(alpha = 0.25f)
            else
                Color.White.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isCompleted) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50).copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Tamamlandı",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Medium,
                            color = Color.White
                        )

                        Text(
                            text = "$currentCount / ${item.count} kez",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                IconButton(
                    onClick = { showDetails = !showDetails },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (showDetails) "Gizle" else "Göster",
                        tint = Color.White,
                        modifier = Modifier.rotate(rotationAngle)
                    )
                }
            }

            AnimatedVisibility(
                visible = showDetails,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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

                    if (item.arabicText.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = item.arabicText,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth(),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (item.transcription?.isNotEmpty() == true) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Okunuşu:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = item.transcription,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    if (item.translation?.isNotEmpty() == true) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Anlamı:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = item.translation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    modifier = Modifier.fillMaxWidth()
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
fun GlassCompletionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(48.dp)
                )
            }

            Text(
                text = "Maşallah!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Tesbihatı tamamladınız",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Allah kabul etsin",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}