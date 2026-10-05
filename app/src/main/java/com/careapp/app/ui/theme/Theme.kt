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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ═══════════════════════════════════════════════════════════════
// CAREAPP LIGHT THEME - Senior-Optimized (Teal/Coral)
// High contrast, large text, accessible design
// ═══════════════════════════════════════════════════════════════
private val CareAppLightColorScheme = lightColorScheme(
    primary = PrimaryTeal,            // Deep teal for main actions
    onPrimary = White,                // White text on teal
    primaryContainer = PrimaryTealLight, // Bright teal for emphasis
    onPrimaryContainer = White,       // White text on bright teal

    secondary = SecondaryCoralDark,   // Deep coral for secondary
    onSecondary = White,              // White text on coral
    secondaryContainer = SecondaryCoral, // Bright coral
    onSecondaryContainer = White,     // White text

    tertiary = AccentAmberDark,       // Amber for warnings
    onTertiary = White,               // White text on amber
    tertiaryContainer = AccentAmber,  // Bright amber
    onTertiaryContainer = TextDark,   // Dark text on bright amber

    error = DangerRed,                // Red for critical only (911)
    onError = White,                  // White text on red
    errorContainer = DangerRedLight,  // Light red background
    onErrorContainer = DangerRed,     // Dark red text on light

    background = BackgroundLight,     // Off-white background
    onBackground = TextDark,          // Dark text on light background
    surface = White,                  // Cards and surfaces
    onSurface = TextDark,             // Text on surfaces
    surfaceVariant = BackgroundMedium, // Light gray variant
    onSurfaceVariant = TextGray,      // Medium gray text
    outline = BorderGray              // Borders and dividers
)

// ═══════════════════════════════════════════════════════════════
// CAREAPP DARK THEME - Optional high contrast mode (Teal/Coral)
// ═══════════════════════════════════════════════════════════════
private val CareAppDarkColorScheme = darkColorScheme(
    primary = PrimaryTealLight,       // Bright teal on dark
    onPrimary = Black,                // Black text on bright teal
    primaryContainer = PrimaryTeal,   // Deep teal container
    onPrimaryContainer = White,       // White text on dark

    secondary = SecondaryCoral,       // Bright coral on dark
    onSecondary = Black,              // Black text on coral
    secondaryContainer = SecondaryCoralDark, // Deep coral container
    onSecondaryContainer = White,     // White text

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

@Composable
fun CareAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,    // Disabled - use our accessible palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> CareAppDarkColorScheme
        else -> CareAppLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SeniorAccessibleTypography,
        content = content
    )
}

// ═══════════════════════════════════════════════════════════════
// Senior-Accessible Theme Configuration
// - Large minimum font sizes (18sp+)
// - Clear hierarchy
// - High contrast ratios
// - Extra padding and spacing
// ═══════════════════════════════════════════════════════════════
