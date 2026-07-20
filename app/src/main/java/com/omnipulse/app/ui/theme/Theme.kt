package com.omnipulse.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OmniPulseColorScheme = lightColorScheme(
    primary = PulseTeal,
    onPrimary = Color.White,
    primaryContainer = PulseMint,
    onPrimaryContainer = PulseInk,
    secondary = PulseCoral,
    onSecondary = Color.White,
    background = PulsePaper,
    onBackground = PulseInk,
    surface = PulseSurface,
    onSurface = PulseInk,
    surfaceVariant = Color(0xFFD9F5F0),
    onSurfaceVariant = PulseMuted,
    outline = PulseLine,
    error = PulseCoral
)

@Composable
fun OmniPulseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OmniPulseColorScheme,
        typography = OmniPulseTypography,
        content = content
    )
}
