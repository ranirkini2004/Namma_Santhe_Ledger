package com.nammasanthe.ledger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = ForestGreen,
    onPrimary = WarmCream,
    primaryContainer = SandBeige,
    onPrimaryContainer = SurfaceDark,
    secondary = ClayBrown,
    onSecondary = WarmCream,
    secondaryContainer = ColorPalette.SecondaryContainer,
    onSecondaryContainer = SurfaceDark,
    tertiary = SunsetTerracotta,
    onTertiary = WarmCream,
    background = WarmCream,
    onBackground = SurfaceDark,
    surface = CardIvory,
    onSurface = SurfaceDark,
    surfaceVariant = ColorPalette.SurfaceVariant,
    onSurfaceVariant = ColorPalette.OnSurfaceVariant,
    outline = DividerBrown,
    error = CreditOrange
)

private val DarkColors = darkColorScheme(
    primary = SoftOlive,
    onPrimary = BackgroundDark,
    primaryContainer = ForestGreen,
    onPrimaryContainer = WarmCream,
    secondary = SandBeige,
    onSecondary = BackgroundDark,
    secondaryContainer = ClayBrown,
    onSecondaryContainer = WarmCream,
    tertiary = SunsetTerracotta,
    onTertiary = BackgroundDark,
    background = BackgroundDark,
    onBackground = WarmCream,
    surface = SurfaceDark,
    onSurface = WarmCream,
    surfaceVariant = ColorPalette.DarkSurfaceVariant,
    onSurfaceVariant = ColorPalette.DarkOnSurfaceVariant,
    outline = ColorPalette.DarkOutline,
    error = ColorPalette.DarkError
)

object ColorPalette {
    val SecondaryContainer = androidx.compose.ui.graphics.Color(0xFFF1E0C6)
    val SurfaceVariant = androidx.compose.ui.graphics.Color(0xFFEDE2D1)
    val OnSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF5D5045)
    val DarkSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF332B24)
    val DarkOnSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFE6D6C3)
    val DarkOutline = androidx.compose.ui.graphics.Color(0x335D8A64)
    val DarkError = androidx.compose.ui.graphics.Color(0xFFE48C62)
}

@Composable
fun NammaSantheLedgerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
