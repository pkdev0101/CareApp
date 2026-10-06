package com.careapp.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

// ═══════════════════════════════════════════════════════════════
// CAREAPP LIGHT THEME - Senior-Optimized (Teal/Coral)
// High contrast, large text, accessible design
// ═══════════════════════════════════════════════════════════════
private val CareAppLightColorScheme = lightColorScheme(
    primary = PrimaryTeal,            // Deep teal for main actions
    onPrimary = White,                // White text on teal
    primaryContainer = PrimaryTealVeryLight,
    onPrimaryContainer = TextDark,

    secondary = SecondaryCoralDark,   // Deep coral for secondary
    onSecondary = White,              // White text on coral
    secondaryContainer = PrimaryTealVeryLight,
    onSecondaryContainer = TextDark,

    tertiary = AccentAmberDark,       // Amber for warnings
    onTertiary = White,               // White text on amber
    tertiaryContainer = AccentAmberLight,
    onTertiaryContainer = TextDark,   // Dark text on bright amber

    error = DangerRed,                // Red for critical only (911)
    onError = White,                  // White text on red
    errorContainer = DangerRedLight,  // Light red background
    onErrorContainer = DangerRed,     // Dark red text on light

    background = Color(0xFFF4F7F6),
    onBackground = TextDark,          // Dark text on light background
    surface = Color(0xFFE8EFEE),
    onSurface = TextDark,             // Text on surfaces
    surfaceVariant = BackgroundMedium, // Light gray variant
    onSurfaceVariant = TextGray,      // Medium gray text
    outline = BorderGray              // Borders and dividers
)

// ═══════════════════════════════════════════════════════════════
// CAREAPP DARK THEME - Optional high contrast mode (Teal/Coral)
// ═══════════════════════════════════════════════════════════════
private val CareAppDarkColorScheme = darkColorScheme(
    primary = Color(0xFF8DB9BE),
    onPrimary = Color(0xFF102D32),
    primaryContainer = Color(0xFF21464D),
    onPrimaryContainer = Color(0xFFD8EAEC),

    secondary = Color(0xFF8DB9BE),
    onSecondary = Color(0xFF102D32),
    secondaryContainer = Color(0xFF21464D),
    onSecondaryContainer = Color(0xFFD8EAEC),

    tertiary = AccentAmber,           // Bright amber
    onTertiary = Black,               // Black text
    tertiaryContainer = AccentAmberDark, // Dark amber
    onTertiaryContainer = White,      // White text

    error = DangerRed,                // Bright red on dark
    onError = Black,                  // Black text
    errorContainer = DangerRedDark,   // Dark red container
    onErrorContainer = White,         // White text

    background = Color(0xFF121212),   // Very dark background
    onBackground = White,             // White text on dark
    surface = Color(0xFF1E1E1E),      // Dark surfaces
    onSurface = White,                // White text on surfaces
    surfaceVariant = Color(0xFF2A2A2A), // Darker variant
    onSurfaceVariant = Color(0xFFCCCCCC), // Light gray text
    outline = Color(0xFF666666)       // Gray borders
)

private val CareAppHighContrastLightColorScheme = lightColorScheme(
    primary = Color(0xFF214F63), onPrimary = White,
    primaryContainer = Color(0xFFD8EAF1), onPrimaryContainer = Color(0xFF102C39),
    secondary = Color(0xFF7A3E35), onSecondary = White,
    secondaryContainer = Color(0xFFF4DDD8), onSecondaryContainer = Color(0xFF35110D),
    tertiary = Color(0xFF755100), onTertiary = White,
    tertiaryContainer = Color(0xFFFFE7B0), onTertiaryContainer = Color(0xFF2D1B00),
    error = Color(0xFF8F2222), onError = White,
    errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFFFF), onBackground = Color(0xFF101416),
    surface = Color.White, onSurface = Color(0xFF101416),
    surfaceVariant = Color(0xFFE6EEF1), onSurfaceVariant = Color(0xFF27363B),
    outline = Color(0xFF526166)
)

@Composable
fun CareAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrast: Boolean = false,
    textScale: Float = 1f,
    dynamicColor: Boolean = false,    // Disabled - use our accessible palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> CareAppDarkColorScheme
        highContrast -> CareAppHighContrastLightColorScheme
        else -> CareAppLightColorScheme
    }

    val density = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(density.density, density.fontScale * textScale)) {
        MaterialTheme(colorScheme = colorScheme, typography = SeniorAccessibleTypography, content = content)
    }
}

// ═══════════════════════════════════════════════════════════════
// Senior-Accessible Theme Configuration
// - Large minimum font sizes (18sp+)
// - Clear hierarchy
// - High contrast ratios
// - Extra padding and spacing
// ═══════════════════════════════════════════════════════════════
