package com.example.risaleezanvakticompose.presentation.screen.tesbihat

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.BorderStroke
import com.example.risaleezanvakticompose.R
import com.example.risaleezanvakticompose.domain.model.TesbihatCategory
import com.example.risaleezanvakticompose.ui.theme.GoldColor
import com.example.risaleezanvakticompose.ui.theme.RisaleRedDark
import com.example.risaleezanvakticompose.ui.theme.RisaleSans
import com.example.risaleezanvakticompose.ui.theme.ScheherazadeFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TesbihatListScreen(
    onCategoryClick: (TesbihatCategory) -> Unit,
    onBackClick: () -> Unit,
    viewModel: TesbihatViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // KATMAN 1: İÇERİK (En altta)
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Üst görselin (yeşil alan) altından başlaması için boşluk
                item {
                    Spacer(modifier = Modifier.height(180.dp))
                }

                // TEK BAŞLIK VE İKON (Buraya taşındı)
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = GoldColor,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Namaz Tesbihatları",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = ScheherazadeFamily
                            ),
                            fontWeight = FontWeight.Bold,
                            color = GoldColor
                        )
                    }
                }

                items(categories) { category ->
                    GlassTesbihatCategoryCard(
                        category = category,
                        onClick = {
                            viewModel.selectCategory(category)
                            onCategoryClick(category)
                        }
                    )
                }
            }
        }

        // KATMAN 2: ÜST GÖRSEL (Overlay)
        Image(
            painter = painterResource(id = R.drawable.ust_plan),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // KATMAN 3: TOP BAR KALDIRILDI (Tek başlık aşağıda kullanıldı)
    }
}

@Composable
fun GlassTesbihatCategoryCard(
    category: TesbihatCategory,
    onClick: () -> Unit
) {
    // İkon mantığı
    val icon = when (category) {
        TesbihatCategory.SABAH -> Icons.Default.WbTwilight // Sabah'a İkindi ikonu
        TesbihatCategory.OGLE -> Icons.Default.LightMode
        TesbihatCategory.IKINDI -> Icons.Default.WbTwilight // İkindi kendi ikonu
        TesbihatCategory.AKSAM -> Icons.Default.NightlightRound
        TesbihatCategory.YATSI -> Icons.Default.DarkMode
    }

    // İkindi namazı için rotasyon (ters çevirme)
    val iconRotation = if (category == TesbihatCategory.IKINDI) 180f else 0f

    val iconColor = GoldColor

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = RisaleRedDark
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, GoldColor.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // İkon Kutusu
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(iconRotation)
                )
            }

            // Metinler
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = ScheherazadeFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = RisaleSans
                    ),
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 1
                )
            }

            // Sağ Ok
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = GoldColor.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}