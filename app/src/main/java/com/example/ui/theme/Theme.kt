package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EyeSafeDarkColorScheme = darkColorScheme(
    primary = VividEmerald,
    secondary = LightGlowMint,
    tertiary = LightGlowMint,
    background = ObsidianBlack,
    surface = ForestCard,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force comforting dark theme for eye strain relief
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // We enforce our custom crafted premium dark green-mint palette
    MaterialTheme(
        colorScheme = EyeSafeDarkColorScheme,
        typography = Typography,
        content = content
    )
}
