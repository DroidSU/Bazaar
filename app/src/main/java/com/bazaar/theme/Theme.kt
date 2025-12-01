package com.bazaar.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = SageGreen,
    onPrimary = White,
    primaryContainer = PaleSage,
    onPrimaryContainer = DeepForest,

    secondary = WarmAmber,
    onSecondary = White,
    secondaryContainer = LightAmber,
    onSecondaryContainer = DarkAmber,

    error = MutedRed,
    onError = White,
    errorContainer = LightPink,
    onErrorContainer = DarkestRed,

    background = OffWhite,
    onBackground = DarkCharcoal,
    surface = LightGray,
    onSurface = DarkCharcoal,
    surfaceVariant = LightGray,
    onSurfaceVariant = MediumGray
)

private val DarkColorScheme = darkColorScheme(
    primary = SageGreen,
    onPrimary = White,
    primaryContainer = DeepForest,
    onPrimaryContainer = PaleSage,

    secondary = WarmAmber,
    onSecondary = White,
    secondaryContainer = DarkAmber,
    onSecondaryContainer = LightAmber,

    error = MutedRed,
    onError = White,
    errorContainer = DarkestRed,
    onErrorContainer = LightPink,

    background = DarkBackground,
    onBackground = PaleWhite,
    surface = DarkSurface,
    onSurface = PaleWhite,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = MediumGray
)

@Composable
fun BazaarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
