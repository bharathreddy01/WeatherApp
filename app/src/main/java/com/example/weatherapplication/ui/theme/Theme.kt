package com.example.weatherapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SkyMid,
    onPrimary = CloudWhite,
    secondary = SunAccent,
    onSecondary = InkPrimary,
    tertiary = SkyDeep,
    background = SkyPale,
    onBackground = InkPrimary,
    surface = CloudWhite,
    onSurface = InkPrimary,
    surfaceVariant = Color(0xFFD7EAF7),
    onSurfaceVariant = InkMuted,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = SkyLight,
    onPrimary = InkPrimary,
    secondary = SunAccent,
    onSecondary = InkPrimary,
    tertiary = SkyMid,
    background = Color(0xFF0B1C28),
    onBackground = CloudWhite,
    surface = Color(0xFF13293A),
    onSurface = CloudWhite,
    surfaceVariant = Color(0xFF1E3A4F),
    onSurfaceVariant = Color(0xFFB7CDDB),
    error = Color(0xFFFFB4AB)
)

@Composable
fun WeatherApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
