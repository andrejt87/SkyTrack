package com.skytrack.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SkyTrackDarkColorScheme = darkColorScheme(
    primary              = Amber,
    onPrimary            = TextOnAmber,
    primaryContainer     = AmberDark,
    onPrimaryContainer   = AmberLight,
    secondary            = RouteBlue,
    onSecondary          = DarkBackground,
    secondaryContainer   = DeepBlue,
    onSecondaryContainer = TextPrimary,
    tertiary             = AltitudeCyan,
    onTertiary           = DarkBackground,
    tertiaryContainer    = Color(0xFF0D4A4D),
    onTertiaryContainer  = AltitudeCyan,
    background           = DarkBackground,
    onBackground         = TextPrimary,
    surface              = DarkSurface,
    onSurface            = TextPrimary,
    surfaceVariant       = DarkSurface2,
    onSurfaceVariant     = TextSecondary,
    outline              = DarkDivider,
    outlineVariant       = TextTertiary,
    error                = Error,
    onError              = TextPrimary,
    errorContainer       = Color(0xFF4A0505),
    onErrorContainer     = ErrorLight,
    scrim                = Color(0x99000000),
    inverseSurface       = TextPrimary,
    inverseOnSurface     = DarkBackground,
    inversePrimary       = DeepBlue,
    surfaceTint          = Amber
)

@Composable
fun SkyTrackTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SkyTrackDarkColorScheme,
        typography  = SkyTrackTypography,
        shapes      = SkyTrackShapes,
        content     = content
    )
}
