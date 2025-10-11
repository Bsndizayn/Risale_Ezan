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
    primary = BurgundyPrimary,
    onPrimary = Color.White,
    primaryContainer = WhiteSmoke,
    onPrimaryContainer = TextDark,

    secondary = TextMedium,
    onSecondary = Color.White,
    secondaryContainer = BeigeLight,
    onSecondaryContainer = TextDark,

    tertiary = GoldAccent,
    onTertiary = Color.White,
    tertiaryContainer = WarmWhite,
    onTertiaryContainer = TextDark,

    background = CreamBackground,
    onBackground = TextDark,

    surface = Color.White,
    onSurface = TextDark,
    surfaceVariant = WarmWhite,
    onSurfaceVariant = TextMedium,

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    outline = DividerColor,
    outlineVariant = BeigeLight,
    scrim = Color.Black,

    inverseSurface = TextDark,
    inverseOnSurface = WarmWhite,
    inversePrimary = SoftBurgundy
)

private val DarkColorScheme = darkColorScheme(
    primary = LightBurgundy,
    onPrimary = DarkBackground,
    primaryContainer = MutedBurgundy,
    onPrimaryContainer = SoftRose,

    secondary = GoldLight,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TextDarkMode,

    tertiary = GoldAccent,
    onTertiary = DarkBackground,
    tertiaryContainer = DarkSurfaceVariant,
    onTertiaryContainer = GoldLight,

    background = DarkBackground,
    onBackground = TextDarkMode,

    surface = DarkSurface,
    onSurface = TextDarkMode,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextDarkSecondary,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = DividerDark,
    outlineVariant = DarkSurfaceVariant,
    scrim = Color.Black,

    inverseSurface = TextDarkMode,
    inverseOnSurface = DarkBackground,
    inversePrimary = BurgundyPrimary
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
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}