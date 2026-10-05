package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA421A4),
    secondary = Color(0xFFCE93D8),
    tertiary = Color(0xFFE1BEE7),
    background = Color(0xFF100E14),
    surface = Color(0xFF19161F)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFA421A4),
    secondary = Color(0xFF6B1874),
    tertiary = Color(0xFFE4D6F7),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFAFAFA)
)

@Composable
fun CalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
