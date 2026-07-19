package com.omnipulse.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF5B4DFF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE5DEFF),
    onPrimaryContainer = Color(0xFF1A0060),
    secondary = Color(0xFF006C4C),
    secondaryContainer = Color(0xFF8CF8C6),
    background = Color(0xFFF9F7FF),
    surface = Color(0xFFFFFBFF),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFC8BFFF),
    onPrimary = Color(0xFF2B168E),
    primaryContainer = Color(0xFF4332A5),
    onPrimaryContainer = Color(0xFFE5DEFF),
    secondary = Color(0xFF70DBAB),
    secondaryContainer = Color(0xFF005138),
    background = Color(0xFF121118),
    surface = Color(0xFF1A191F),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
)

@Composable
fun OmniPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
