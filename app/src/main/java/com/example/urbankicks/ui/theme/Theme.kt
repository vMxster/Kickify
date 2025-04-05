package com.example.urbankicks.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

val DarkGray = Color(0xFF1A1A1A)
val NeonGreen = Color(0xFF00FF88)
val OffWhite = Color(0xFFF5F5F5)

private val DarkColorPalette = darkColorScheme(
    primary = NeonGreen,
    secondary = DarkGray,
    background = DarkGray,
    surface = DarkGray,
    onPrimary = DarkGray,
    onSecondary = OffWhite,
    onBackground = OffWhite,
    onSurface = OffWhite
)

private val LightColorPalette = lightColorScheme(
    primary = DarkGray,
    secondary = NeonGreen,
    background = OffWhite,
    surface = OffWhite,
    onPrimary = OffWhite,
    onSecondary = DarkGray,
    onBackground = DarkGray,
    onSurface = DarkGray
)

val UrbanTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp,
        letterSpacing = 1.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 16.sp,
        letterSpacing = 1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

val UrbanShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun UrbanKicksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = UrbanTypography,
        shapes = UrbanShapes,
        content = content
    )
}