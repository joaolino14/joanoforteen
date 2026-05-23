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
    primary = IndigoSecondary,
    secondary = CyanSecondary,
    tertiary = AccentPurple,
    background = Slate900,
    surface = Slate800,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    primaryContainer = Slate800,
    surfaceVariant = Slate700
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    secondary = CyanSecondary,
    tertiary = AccentPurple,
    background = Color.White,
    surface = Slate50,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Slate900,
    onSurface = Slate800,
    primaryContainer = Slate100,
    surfaceVariant = Slate100
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic colors to enforce branding
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
