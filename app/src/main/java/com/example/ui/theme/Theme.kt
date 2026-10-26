package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PlaySphereDarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004E58),
    onPrimaryContainer = Color(0xFF70F5FF),

    secondary = NeonPurple,
    onSecondary = Color(0xFF2C0066),
    secondaryContainer = Color(0xFF450D8A),
    onSecondaryContainer = Color(0xFFEADBFF),

    tertiary = CoralPink,
    onTertiary = Color(0xFF4C0015),
    tertiaryContainer = Color(0xFF8F002E),
    onTertiaryContainer = Color(0xFFFFD9DF),

    background = BackgroundDark,
    onBackground = TextPrimary,

    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,

    outline = BorderDark,
    outlineVariant = Color(0xFF1E283D)
)

@Composable
fun PlaySphereTheme(
    darkTheme: Boolean = true, // Gaming apps are best experienced with custom immersive dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = PlaySphereDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = BackgroundDark.toArgb()
                window.navigationBarColor = BackgroundDark.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
