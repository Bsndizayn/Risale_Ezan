package com.example.risaleezanvakticompose.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = IslamicGreen,
    onPrimary = PureWhite,
    primaryContainer = LightGreen,
    onPrimaryContainer = DeepGreen,

    secondary = GoldAccent,
    onSecondary = PureWhite,
    secondaryContainer = LightCream,
    onSecondaryContainer = TextDark,

    tertiary = BrightGreen,
    onTertiary = PureWhite,
    tertiaryContainer = MediumGreen,
    onTertiaryContainer = DeepGreen,

    background = CreamBackground,
    onBackground = TextDark,

    surface = WarmWhite,
    onSurface = TextDark,
    surfaceVariant = LightCream,
    onSurfaceVariant = TextMedium,

    error = ErrorRed,
    onError = PureWhite,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = DeepGreen,

    outline = DividerLight,
    outlineVariant = OutlineLight,
    scrim = PureBlack.copy(alpha = 0.32f),

    inverseSurface = DarkSurface,
    inverseOnSurface = WarmWhite,
    inversePrimary = SoftGreen
)

private val DarkColorScheme = darkColorScheme(
    primary = LightGreenDark,
    onPrimary = DarkBackground,
    primaryContainer = SoftGreenDark,
    onPrimaryContainer = BrightGreenDark,

    secondary = GoldDark,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = GoldSoftDark,

    tertiary = BrightGreenDark,
    onTertiary = DarkBackground,
    tertiaryContainer = DarkSurfaceVariant,
    onTertiaryContainer = LightGreenDark,

    background = DarkBackground,
    onBackground = TextDarkMode,

    surface = DarkSurface,
    onSurface = TextDarkMode,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextDarkSecondary,

    error = ErrorRed,
    onError = DarkBackground,
    errorContainer = Color(0xFF5D1F1F),
    onErrorContainer = Color(0xFFFFB4AB),

    outline = DividerDark,
    outlineVariant = Color(0xFF4A4A4A),
    scrim = PureBlack.copy(alpha = 0.5f),

    inverseSurface = TextDarkMode,
    inverseOnSurface = DarkBackground,
    inversePrimary = IslamicGreen
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
            window.statusBarColor = if (darkTheme) {
                DarkBackground.toArgb()
            } else {
                CreamBackground.toArgb()
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