package com.bazaar.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Light Color Scheme using the new "Modern Spice Market" palette
private val LightColorScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = PureWhite,
    secondary = RichTeal,
    onSecondary = PureWhite,
    background = WarmBackground,
    onBackground = DarkBrownText,
    surface = WarmSurface,
    onSurface = DarkBrownText,
    onSurfaceVariant = MutedText,
    error = MaterialError,
    onError = PureWhite
)

// Dark theme
private val DarkColorScheme = darkColorScheme(
    primary = Terracotta,
    onPrimary = PureWhite,
    secondary = RichTeal,
    onSecondary = PureWhite,
    background = Color(0xFF1C1B19),
    onBackground = WarmBackground,
    surface = Color(0xFF2A2826),
    onSurface = WarmBackground,
    onSurfaceVariant = MutedText,
    error = MaterialError,
    onError = PureWhite
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
