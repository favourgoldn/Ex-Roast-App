package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Dark Theme Colors (Default)
val ExDarkBg = Color(0xFF0B0B0F)
val ExDarkSurface = Color(0xFF15151C)
val ExDarkSurfaceRaised = Color(0xFF1E1E28)
val ExDarkBorder = Color(0xFF2A2A36)
val ExDarkText = Color(0xFFF5F5F7)
val ExDarkTextMuted = Color(0xFF9A9AAE)

// Light Theme Colors
val ExLightBg = Color(0xFFFAF8F5)
val ExLightSurface = Color(0xFFFFFFFF)
val ExLightSurfaceRaised = Color(0xFFF2EFE9)
val ExLightBorder = Color(0xFFE5E1D8)
val ExLightText = Color(0xFF16161D)
val ExLightTextMuted = Color(0xFF6B6B7B)

// Shared Brand Colors
val ExFlame = Color(0xFFFF4D2E)
val ExFlameHot = Color(0xFFFF7A1A)
val ExGold = Color(0xFFFFC93C)
val ExSuccess = Color(0xFF2ECC8F)
val ExDanger = Color(0xFFFF3B5C)
val ExInfo = Color(0xFF5B8CFF)

// Reactions
val ReactFlameColor = Color(0xFFFF4D2E)
val ReactSkullColor = Color(0xFFE0E0E6)
val ReactCringeColor = Color(0xFFFF9F1C)
val ReactLaughColor = Color(0xFFFFD166)
val ReactRedFlagColor = Color(0xFFFF3B5C)

// Gradients
val ExFlameGradient = Brush.linearGradient(
    colors = listOf(ExFlame, ExFlameHot)
)
val ExGoldGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFDF00), Color(0xFFFFB300))
)
