package com.orca.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = OrcaPrimary,
    onPrimary = OrcaOnPrimary,
    primaryContainer = OrcaSurfaceElevated,
    onPrimaryContainer = OrcaPrimaryVariant,
    secondary = OrcaPrimaryVariant,
    onSecondary = OrcaOnPrimary,
    background = OrcaBackground,
    onBackground = OrcaOnBackground,
    surface = OrcaSurface,
    onSurface = OrcaOnSurface,
    surfaceVariant = OrcaSurfaceVariant,
    onSurfaceVariant = OrcaOnSurfaceVariant,
    outline = OrcaOutline,
    outlineVariant = OrcaOutlineVariant,
    error = OrcaError,
)

@Composable
fun OrcaTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = OrcaBackground.toArgb()
            window.navigationBarColor = OrcaBackground.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OrcaTypography,
        content = content,
    )
}
