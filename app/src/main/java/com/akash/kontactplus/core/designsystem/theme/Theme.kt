package com.akash.kontactplus.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrandViolet,
    onPrimary = DarkOnBackground,
    primaryContainer = BrandViolet,
    onPrimaryContainer = DarkOnBackground,
    secondary = ElectricCyan,
    onSecondary = DarkBackground,
    tertiary = SuccessGreen,
    error = ErrorRed,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurface,
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = BrandViolet,
    onPrimary = LightSurface,
    primaryContainer = BrandViolet,
    onPrimaryContainer = LightSurface,
    secondary = ElectricCyan,
    onSecondary = LightOnBackground,
    tertiary = SuccessGreen,
    error = ErrorRed,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurface,
    outline = LightOutline
)

@Composable
fun KontactPlusTheme(
    themeMode: ThemeMode = ThemeMode.System,
    darkTheme: Boolean = when (themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    },
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDark = if (themeMode != ThemeMode.System) {
        when (themeMode) {
            ThemeMode.Light -> false
            ThemeMode.Dark -> true
            ThemeMode.System -> darkTheme
        }
    } else {
        darkTheme
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
