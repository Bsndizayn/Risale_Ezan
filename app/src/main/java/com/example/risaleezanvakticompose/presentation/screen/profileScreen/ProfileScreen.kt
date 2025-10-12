package com.example.risaleezanvakticompose.presentation.screen.profileScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.risaleezanvakticompose.ui.components.RiasalieArkaPlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    onSaveSettings: (settings: String) -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    RiasalieArkaPlan {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Ayarlar") },
                    windowInsets = WindowInsets(0.dp),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Genel Ayarlar Başlığı
                item {
                    Text(
                        text = "Genel Ayarlar",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }

                // Bildirimler - Artık Settings'e gidiyor
                item {
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Bildirimler",
                        subtitle = "Namaz vakti hatırlatmaları",
                        onClick = onNavigateToSettings // Settings ekranına git
                    )
                }

                // Tema
                item {
                    SettingsItem(
                        icon = Icons.Default.DarkMode,
                        title = "Tema",
                        subtitle = "Aydınlık / Karanlık mod",
                        onClick = { }
                    )
                }

                // Dil
                item {
                    SettingsItem(
                        icon = Icons.Default.Language,
                        title = "Dil",
                        subtitle = "Türkçe",
                        onClick = { }
                    )
                }

                // Hakkında Başlığı
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hakkında",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }

                // Uygulama Hakkında
                item {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "Uygulama Hakkında",
                        subtitle = "Sürüm 1.0.0",
                        onClick = { }
                    )
                }

                // Gizlilik Politikası
                item {
                    SettingsItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Gizlilik Politikası",
                        subtitle = "Verilerinizi nasıl kullanıyoruz",
                        onClick = { }
                    )
                }

                // İletişim
                item {
                    SettingsItem(
                        icon = Icons.Default.Email,
                        title = "İletişim",
                        subtitle = "destek@risaleezan.com",
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
fun SettingsItem(
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
            containerColor = MaterialTheme.colorScheme.surface // OPAK YAPILDI
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}