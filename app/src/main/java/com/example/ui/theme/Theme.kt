package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JBEpinColorScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = GamingDarkBackground,
    primaryContainer = GamingDarkSurfaceVariant,
    onPrimaryContainer = OrangeGlow,
    secondary = AmberSecondary,
    onSecondary = GamingDarkBackground,
    tertiary = RustTertiary,
    onTertiary = TextPrimary,
    background = GamingDarkBackground,
    onBackground = TextPrimary,
    surface = GamingDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = GamingDarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = GamingBrownBorder
)

@Composable
fun JBEpinTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JBEpinColorScheme,
        typography = Typography,
        content = content
    )
}

