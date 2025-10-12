package com.example.risaleezanvakticompose.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    // Ana Kırmızı Renkler
    primary = IslamicRed,                   // Ana kırmızı
    onPrimary = TextOnRed,                  // Beyaz text
    primaryContainer = LightRed,            // Çok açık kırmızı container
    onPrimaryContainer = DeepRed,           // Koyu kırmızı text

    // İkincil Renkler (Altın)
    secondary = GoldAccent,                 // Altın
    onSecondary = TextDark,                 // Koyu text
    secondaryContainer = GoldLight.copy(alpha = 0.2f), // Açık altın container
    onSecondaryContainer = TextDark,

    // Üçüncül Renkler
    tertiary = BrightRed,                   // Parlak kırmızı
    onTertiary = TextOnRed,
    tertiaryContainer = MediumRed,          // Orta açık kırmızı
    onTertiaryContainer = DeepRed,

    // Arka Plan ve Yüzeyler
    background = CreamBackground,           // Açık krem (pattern üzerine)
    onBackground = TextDark,

    surface = WarmWhite,                    // Beyaz kartlar
    onSurface = TextDark,
    surfaceVariant = LightCream,            // Açık krem variant
    onSurfaceVariant = TextMedium,

    // Hata Renkleri
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = DeepRed,

    // Outline ve Scrim
    outline = DividerLight,
    outlineVariant = OutlineLight,
    scrim = Color.Black.copy(alpha = 0.32f),

    // Inverse Renkler
    inverseSurface = DarkBackground,
    inverseOnSurface = CreamBackground,
    inversePrimary = SoftRed
)

private val DarkColorScheme = darkColorScheme(
    // Ana Kırmızı Renkler (Dark)
    primary = LightRedDark,
    onPrimary = DarkBackground,
    primaryContainer = DeepRed,
    onPrimaryContainer = SoftRedDark,

    // İkincil Renkler (Dark)
    secondary = GoldDark,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = GoldSoftDark,

    // Üçüncül Renkler (Dark)
    tertiary = BrightRedDark,
    onTertiary = DarkBackground,
    tertiaryContainer = DarkSurfaceVariant,
    onTertiaryContainer = SoftRedDark,

    // Arka Plan ve Yüzeyler (Dark)
    background = DarkBackground,
    onBackground = TextDarkMode,

    surface = DarkSurface,
    onSurface = TextDarkMode,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextDarkSecondary,

    // Hata Renkleri (Dark)
    error = ErrorRed,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // Outline ve Scrim (Dark)
    outline = DividerDark,
    outlineVariant = DarkSurfaceVariant,
    scrim = Color.Black.copy(alpha = 0.5f),

    // Inverse Renkler (Dark)
    inverseSurface = TextDarkMode,
    inverseOnSurface = DarkBackground,
    inversePrimary = IslamicRed
)

@Composable
fun RisaleEzanVaktiComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Status bar için hafif transparan arka plan
            window.statusBarColor = if (darkTheme) {
                colorScheme.background.toArgb()
            } else {
                Color(0xFFFFFBF5).copy(alpha = 0.95f).toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}