package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NaturalPrimaryLight,
    onPrimary = NaturalPrimaryDark,
    primaryContainer = NaturalPrimaryDark,
    onPrimaryContainer = NaturalPrimaryContainer,
    secondary = NaturalSecondary,
    onSecondary = Color.Black,
    secondaryContainer = OnNaturalSecondaryContainer,
    onSecondaryContainer = NaturalSecondaryContainer,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFFE2E8F0),
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = NaturalPrimary,
    onPrimary = Color.White,
    primaryContainer = NaturalPrimaryContainer,
    onPrimaryContainer = OnNaturalPrimaryContainer,
    secondary = NaturalSecondary,
    onSecondary = Color.White,
    secondaryContainer = NaturalSecondaryContainer,
    onSecondaryContainer = OnNaturalSecondaryContainer,
    background = NaturalBackground,
    surface = NaturalSurface,
    surfaceVariant = NaturalSurfaceWarm,
    onBackground = NaturalTextPrimary,
    onSurface = NaturalTextPrimary,
    onSurfaceVariant = NaturalTextSecondary,
    outline = NaturalBorder
)

@Composable
fun JimpitanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our community branded palette for consistency
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
