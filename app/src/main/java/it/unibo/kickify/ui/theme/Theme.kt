package it.unibo.kickify.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorPalette = darkColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    background = Black,
    surface = MediumGray,
    onBackground = Color.White,
    onSurface = LightGray
)

private val LightColorPalette = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    background = Color.White,
    surface = GhostWhite,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF424242),
)

val KickifyShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun KickifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colorScheme = colors,
        typography = KickifyTypography,
        shapes = KickifyShapes,
        content = {
            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    WindowCompat.getInsetsController((view.context as Activity).window, view)
                        .isAppearanceLightStatusBars = !darkTheme
                }
            }
            content()
        }
    )
}