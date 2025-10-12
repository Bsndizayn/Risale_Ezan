package com.example.risaleezanvakticompose.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = RisaleRed,
    onPrimary = White,
    primaryContainer = RisaleRedLight.copy(alpha = 0.12f),
    onPrimaryContainer = RisaleRedDark,

    secondary = GoldYaldiz,
    onSecondary = TextBrown,
    secondaryContainer = GoldLight.copy(alpha = 0.15f),
    onSecondaryContainer = GoldDark,

    tertiary = IslamicGreen,
    onTertiary = White,
    tertiaryContainer = IslamicGreenSoft.copy(alpha = 0.12f),
    onTertiaryContainer = IslamicGreen,

    background = PageCream,
    onBackground = TextInk,

    surface = PageBeige,
    onSurface = TextInk,
    surfaceVariant = PageOld,
    onSurfaceVariant = TextMedium,

    surfaceTint = RisaleRed.copy(alpha = 0.05f),

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed,

    outline = DividerMedium,
    outlineVariant = DividerLight,
    scrim = Black.copy(alpha = 0.32f),

    inverseSurface = DarkSurface,
    inverseOnSurface = PageBeige,
    inversePrimary = RisaleRedSoft
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkRed,
    onPrimary = DarkBackground,
    primaryContainer = RisaleRedDark,
    onPrimaryContainer = DarkRed,

    secondary = DarkGold,
    onSecondary = DarkBackground,
    secondaryContainer = GoldDark,
    onSecondaryContainer = GoldLight,

    tertiary = IslamicGreenSoft,
    onTertiary = DarkBackground,
    tertiaryContainer = IslamicGreen,
    onTertiaryContainer = IslamicGreenSoft,

    background = DarkBackground,
    onBackground = DarkTextPrimary,

    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,

    surfaceTint = DarkGold.copy(alpha = 0.08f),

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRed.copy(alpha = 0.15f),
    onErrorContainer = DarkRed,

    outline = DividerDark,
    outlineVariant = DividerMedium.copy(alpha = 0.3f),
    scrim = Black.copy(alpha = 0.5f),

    inverseSurface = PageBeige,
    inverseOnSurface = DarkBackground,
    inversePrimary = RisaleRed
)

@Composable
fun RisaleEzanVaktiComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RisaleTypography,
        content = content
    )
}