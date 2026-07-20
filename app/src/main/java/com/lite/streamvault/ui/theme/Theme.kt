package com.lite.streamvault.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme

private val MoradColorScheme = darkColorScheme(
    primary = Blue500,
    onPrimary = TextPrimary,
    primaryContainer = Blue700,
    onPrimaryContainer = Blue100,
    secondary = Blue400,
    onSecondary = TextPrimary,
    secondaryContainer = Blue800,
    onSecondaryContainer = Blue200,
    tertiary = Blue300,
    onTertiary = TextPrimary,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondary,
    surfaceTint = Blue400,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
    error = StatusError,
    onError = TextPrimary,
    errorContainer = StatusError,
    onErrorContainer = TextPrimary
)

@Composable
fun MoradTvTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Always dark theme per spec.
    MaterialTheme(
        colorScheme = MoradColorScheme,
        typography = MoradTvTypography,
        content = content
    )
}
