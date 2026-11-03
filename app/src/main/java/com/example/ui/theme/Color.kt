package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Futuristic Neon AI Palette
val DeepVoidBlack = Color(0xFF060810)
val DarkNavyBackground = Color(0xFF090D1A)
val SurfaceNavy = Color(0xFF10172C)
val SurfaceCard = Color(0xFF16203D)
val SurfaceCardGlow = Color(0xFF1E2B52)

val NeonPurple = Color(0xFFB066FE)
val ElectricCyan = Color(0xFF00F0FF)
val NeonPink = Color(0xFFFF2E93)
val CyberBlue = Color(0xFF38BDF8)
val NeonGreen = Color(0xFF10B981)
val NeonAmber = Color(0xFFF59E0B)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val GlassBorder = Color(0x338B5CF6)
val GlassFill = Color(0x3310172C)

// Gradient presets
val OrbGradient = Brush.radialGradient(
    colors = listOf(
        Color(0xFF00F0FF),
        Color(0xFFB066FE),
        Color(0xFFFF2E93),
        Color(0x00090D1A)
    )
)

val NeonHorizontalGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF00F0FF),
        Color(0xFFB066FE),
        Color(0xFFFF2E93)
    )
)

val CardBorderGradient = Brush.linearGradient(
    colors = listOf(
        Color(0x8800F0FF),
        Color(0x88B066FE),
        Color(0x33FF2E93)
    )
)
