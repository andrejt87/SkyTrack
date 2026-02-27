package com.skytrack.core.design.theme

import androidx.compose.ui.graphics.Color

/**
 * SkyTrack color system. ADR-006: Dark Mode as default theme.
 * Optimized for AMOLED displays in dark cabin environments.
 */
object SkyTrackColors {
    // Dark Theme (Default)
    val Background = Color(0xFF0A0E1A)
    val BackgroundDark = Color(0xFF060810)
    val Surface = Color(0xFF141829)
    val SurfaceVariant = Color(0xFF1E2338)
    val Accent = Color(0xFF4FC3F7)        // Sky blue
    val AccentSecondary = Color(0xFF81C784) // Success green
    val TextPrimary = Color(0xFFECEFF1)
    val TextSecondary = Color(0xFF90A4AE)
    val Error = Color(0xFFEF5350)
    val Warning = Color(0xFFFFB74D)
    val GpsActive = Color(0xFF66BB6A)
    val GpsLost = Color(0xFFEF5350)
    val ProgressArc = Color(0xFF4FC3F7)
    val ProgressArcBackground = Color(0xFF1E2338)
    val RouteFlown = Color(0xFF4FC3F7)
    val RouteRemaining = Color(0x804FC3F7)
    val DepartureMarker = Color(0xFF66BB6A)
    val ArrivalMarker = Color(0xFFFF7043)

    // Light Theme
    val BackgroundLight = Color(0xFFF5F7FA)
    val SurfaceLight = Color(0xFFFFFFFF)
    val SurfaceVariantLight = Color(0xFFE8EBF0)
    val AccentLight = Color(0xFF0288D1)
    val AccentSecondaryLight = Color(0xFF388E3C)
    val TextPrimaryLight = Color(0xFF1A1A2E)
    val TextSecondaryLight = Color(0xFF546E7A)
}
