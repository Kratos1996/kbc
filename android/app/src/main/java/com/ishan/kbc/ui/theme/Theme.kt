package com.ishan.kbc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = Gold,
    onPrimary = DeepBlue,
    secondary = GoldDark,
    onSecondary = DeepBlue,
    background = DeepBlue,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceMuted,
    error = ErrorRed,
    onError = OnSurface,
)

private val LightColors = lightColorScheme(
    primary = Gold,
    onPrimary = DeepBlue,
    secondary = GoldDark,
    background = DeepBlue,
    surface = Surface,
    onSurface = OnSurface,
    error = ErrorRed,
)

@Composable
fun KbcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = KbcTypography,
        content = content,
    )
}
