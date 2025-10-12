package com.example.risaleezanvakticompose.presentation.screen.profileScreen

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    onSaveSettings: (settings: String) -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {

    val context = LocalContext.current

//    Box(modifier = Modifier.fillMaxSize()) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    brush = Brush.verticalGradient(
//                        colors = listOf(
//                            Color(0xFF1E3A8A),
//                            Color(0xFF7C3AED),
//                            Color(0xFFF97316)
//                        )
//                    )
//                )
//        )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            GlassProfileTopBar()

            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    Text(
                        text = "Genel Ayarlar",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }

                item {
                    GlassSettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Bildirimler",
                        subtitle = "Namaz vakti hatırlatmaları",
                        onClick = onNavigateToSettings
                    )
                }

                item {
                    GlassSettingsItem(
                        icon = Icons.Default.DarkMode,
                        title = "Tema",
                        subtitle = "Aydınlık / Karanlık mod",
                        onClick = { Toast.makeText(
                            context,
                            "Bu özellik yakınca eklenecek",
                            Toast.LENGTH_SHORT
                        ).show() }
                    )
                }

                item {
                    GlassSettingsItem(
                        icon = Icons.Default.Language,
                        title = "Dil",
                        subtitle = "Türkçe",
                        onClick = { Toast.makeText(
                            context,
                            "Bu özellik yakınca eklenecek",
                            Toast.LENGTH_SHORT
                        ).show() }
                    )
                }

                item {
                    Text(
                        text = "Hakkında",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }

                item {
                    GlassSettingsItem(
                        icon = Icons.Default.Info,
                        title = "Uygulama Hakkında",
                        subtitle = "Sürüm 1.0.0",
                        onClick = {
                        }
                    )
                }

                item {
                    GlassSettingsItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Gizlilik Politikası",
                        subtitle = "Verilerinizi nasıl kullanıyoruz",
                        onClick = { }
                    )
                }

                item {
                    GlassSettingsItem(
                        icon = Icons.Default.Email,
                        title = "İletişim",
                        subtitle = "izmir@toros.com",
                        onClick = { }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun GlassProfileTopBar() {
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
            Text(
                text = "Profil & Ayarlar",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GlassSettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}