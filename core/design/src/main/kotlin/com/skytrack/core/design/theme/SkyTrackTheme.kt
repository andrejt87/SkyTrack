package com.skytrack.core.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SkyTrackColors.Accent,
    onPrimary = SkyTrackColors.TextPrimary,
    secondary = SkyTrackColors.AccentSecondary,
    onSecondary = SkyTrackColors.TextPrimary,
    background = SkyTrackColors.Background,
    onBackground = SkyTrackColors.TextPrimary,
    surface = SkyTrackColors.Surface,
    onSurface = SkyTrackColors.TextPrimary,
    surfaceVariant = SkyTrackColors.SurfaceVariant,
    onSurfaceVariant = SkyTrackColors.TextSecondary,
    error = SkyTrackColors.Error,
    onError = SkyTrackColors.TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = SkyTrackColors.AccentLight,
    onPrimary = SkyTrackColors.BackgroundDark,
    secondary = SkyTrackColors.AccentSecondaryLight,
    background = SkyTrackColors.BackgroundLight,
    onBackground = SkyTrackColors.TextPrimaryLight,
    surface = SkyTrackColors.SurfaceLight,
    onSurface = SkyTrackColors.TextPrimaryLight,
    surfaceVariant = SkyTrackColors.SurfaceVariantLight,
    onSurfaceVariant = SkyTrackColors.TextSecondaryLight,
    error = SkyTrackColors.Error,
    onError = SkyTrackColors.TextPrimary
)

@Composable
fun SkyTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SkyTrackTypography,
        content = content
    )
}
