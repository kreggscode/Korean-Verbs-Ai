package com.kreggscode.koreanverbs.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PremiumPurple,
    onPrimary = Color.White,
    primaryContainer = PremiumPurple.copy(alpha = 0.2f),
    onPrimaryContainer = PremiumPurple,
    secondary = PremiumTeal,
    onSecondary = Color(0xFF0A0A0A),
    secondaryContainer = PremiumTeal.copy(alpha = 0.2f),
    onSecondaryContainer = PremiumTeal,
    tertiary = PremiumPink,
    onTertiary = Color.White,
    tertiaryContainer = PremiumPink.copy(alpha = 0.2f),
    onTertiaryContainer = PremiumPink,
    background = Color(0xFF0A0A0A),
    onBackground = Color(0xFFE8E8E8),
    surface = Color(0xFF1C1C1E),
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = Color(0xFF2C2C2E),
    onSurfaceVariant = Color(0xFFB8B8B8),
    error = Error,
    onError = Color.White,
    errorContainer = Error.copy(alpha = 0.2f),
    onErrorContainer = Error,
    outline = Color.White.copy(alpha = 0.15f),
    outlineVariant = Color.White.copy(alpha = 0.08f),
    scrim = Color.Black.copy(alpha = 0.7f)
)

private val LightColorScheme = lightColorScheme(
    primary = PremiumIndigo,
    onPrimary = Color.White,
    primaryContainer = PremiumIndigo.copy(alpha = 0.1f),
    onPrimaryContainer = PremiumIndigo,
    secondary = PremiumTeal,
    onSecondary = Color.White,
    secondaryContainer = PremiumTeal.copy(alpha = 0.1f),
    onSecondaryContainer = PremiumTeal,
    tertiary = PremiumPink,
    onTertiary = Color.White,
    tertiaryContainer = PremiumPink.copy(alpha = 0.1f),
    onTertiaryContainer = PremiumPink,
    background = Color(0xFFF5F5F7),
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = CardLight,
    onSurfaceVariant = TextSecondary,
    error = Error,
    onError = Color.White,
    errorContainer = Error.copy(alpha = 0.1f),
    onErrorContainer = Error,
    outline = Color(0xFFE0E0E0),
    outlineVariant = Color(0xFFF0F0F0),
    scrim = Color.Black.copy(alpha = 0.3f)
)

@Composable
fun KoreanVerbsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar color (deprecated in Android 16, but still functional)
            @Suppress("DEPRECATION")
            window.statusBarColor = Color.Transparent.toArgb()
            // Set navigation bar color to match app background exactly
            val navBarColor = if (darkTheme) {
                // Dark mode: match dark background
                colorScheme.surface
            } else {
                // Light mode: match light background
                colorScheme.background
            }
            // Set navigation bar color (deprecated in Android 16, but still functional)
            @Suppress("DEPRECATION")
            window.navigationBarColor = navBarColor.toArgb()
            // Set window background to transparent to prevent white background behind nav bar
            window.setBackgroundDrawableResource(android.R.color.transparent)
            
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
            
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
