package com.example.wifisecuritylabbd.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0B6E4F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCDEFE0),
    onPrimaryContainer = Color(0xFF00210F),
    secondary = Color(0xFF4A6358),
    tertiary = Color(0xFF8A5A00),
    background = Color(0xFFF7FAF8),
    surface = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF88D8B5),
    onPrimary = Color(0xFF003824),
    primaryContainer = Color(0xFF00513A),
    onPrimaryContainer = Color(0xFFA4F4D0),
    secondary = Color(0xFFB1CCBE),
    tertiary = Color(0xFFF2BD6B),
    background = Color(0xFF101412),
    surface = Color(0xFF181C1A)
)

@Composable
fun WifiLabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}
