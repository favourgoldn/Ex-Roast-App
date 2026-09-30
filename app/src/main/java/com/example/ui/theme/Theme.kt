package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class CustomThemeColors(
    val bg: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val border: Color,
    val text: Color,
    val textMuted: Color,
    val accent: Color,
    val accentHot: Color,
    val accentGradient: Brush,
    val gold: Color,
    val success: Color,
    val danger: Color,
    val info: Color
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomThemeColors(
        bg = ExDarkBg,
        surface = ExDarkSurface,
        surfaceRaised = ExDarkSurfaceRaised,
        border = ExDarkBorder,
        text = ExDarkText,
        textMuted = ExDarkTextMuted,
        accent = ExFlame,
        accentHot = ExFlameHot,
        accentGradient = ExFlameGradient,
        gold = ExGold,
        success = ExSuccess,
        danger = ExDanger,
        info = ExInfo
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = ExFlame,
    onPrimary = Color.White,
    primaryContainer = ExDarkSurfaceRaised,
    onPrimaryContainer = ExFlameHot,
    secondary = ExFlameHot,
    onSecondary = Color.White,
    background = ExDarkBg,
    onBackground = ExDarkText,
    surface = ExDarkSurface,
    onSurface = ExDarkText,
    surfaceVariant = ExDarkSurfaceRaised,
    onSurfaceVariant = ExDarkTextMuted,
    outline = ExDarkBorder,
    error = ExDanger,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ExFlame,
    onPrimary = Color.White,
    primaryContainer = ExLightSurfaceRaised,
    onPrimaryContainer = ExFlameHot,
    secondary = ExFlameHot,
    onSecondary = Color.White,
    background = ExLightBg,
    onBackground = ExLightText,
    surface = ExLightSurface,
    onSurface = ExLightText,
    surfaceVariant = ExLightSurfaceRaised,
    onSurfaceVariant = ExLightTextMuted,
    outline = ExLightBorder,
    error = ExDanger,
    onError = Color.White
)

@Composable
fun ExRoastTheme(
    darkTheme: Boolean = true, // Default to dark theme per spec
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val customColors = if (darkTheme) {
        CustomThemeColors(
            bg = ExDarkBg,
            surface = ExDarkSurface,
            surfaceRaised = ExDarkSurfaceRaised,
            border = ExDarkBorder,
            text = ExDarkText,
            textMuted = ExDarkTextMuted,
            accent = ExFlame,
            accentHot = ExFlameHot,
            accentGradient = ExFlameGradient,
            gold = ExGold,
            success = ExSuccess,
            danger = ExDanger,
            info = ExInfo
        )
    } else {
        CustomThemeColors(
            bg = ExLightBg,
            surface = ExLightSurface,
            surfaceRaised = ExLightSurfaceRaised,
            border = ExLightBorder,
            text = ExLightText,
            textMuted = ExLightTextMuted,
            accent = ExFlame,
            accentHot = ExFlameHot,
            accentGradient = ExFlameGradient,
            gold = ExGold,
            success = ExSuccess,
            danger = ExDanger,
            info = ExInfo
        )
    }

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object ExRoastTheme {
    val colors: CustomThemeColors
        @Composable
        get() = LocalCustomColors.current
}
