package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ImperialGold,
    onPrimary = NightSky,
    primaryContainer = DragonOrange,
    onPrimaryContainer = Color.White,
    secondary = ChineseRed,
    onSecondary = Color.White,
    tertiary = JadeLight,
    onTertiary = Color.White,
    background = NightSky,
    onBackground = Color.White,
    surface = CardBackground,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2242),
    onSurfaceVariant = Color(0xFFD6CEE5)
)

private val LightColorScheme = lightColorScheme(
    primary = DragonOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0B2),
    onPrimaryContainer = Color(0xFF4E2600),
    secondary = ChineseRed,
    onSecondary = Color.White,
    tertiary = JadeGreen,
    onTertiary = Color.White,
    background = SoftCream,
    onBackground = TextDark,
    surface = SoftSurface,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFF0EBE1),
    onSurfaceVariant = TextMuted
)

@Composable
fun ChineseBossBattleTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
