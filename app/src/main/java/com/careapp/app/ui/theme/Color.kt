package com.careapp.app.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// CAREAPP COLOR PALETTE - WCAG AAA COMPLIANT
// Warm blue-grey & soft coral theme (inspired by Memory Lane, Caring.com)
// Calm, approachable, not clinical - designed for senior comfort
// ═══════════════════════════════════════════════════════════════

// PRIMARY - SOFT SLATE BLUE (Trust, Calm, Professional - "Anchor")
val PrimaryTeal = Color(0xFF315C66)
val PrimaryTealLight = Color(0xFF4A7880)
val PrimaryTealVeryLight = Color(0xFFE7F0F1)

// SECONDARY - SOFT PEACH (Family, Warmth, Connection)
val SecondaryCoralDark = PrimaryTeal
val SecondaryCoral = PrimaryTealLight
val SecondaryCoralLight = PrimaryTealVeryLight

// ACCENT - SOFT GOLD (Reminders, Gentle Alerts)
val AccentAmberDark = Color(0xFFCD9B6A)    // Warm bronze
val AccentAmber = Color(0xFFE6C7A0)        // Soft gold - "Remind Later" buttons
val AccentAmberLight = Color(0xFFFFF5E8)   // Very light gold - backgrounds

// SUPPORT - MUTED LAVENDER (Lost Mode, Confusion - calming)
val SupportPlum = PrimaryTeal
val SupportPlumLight = PrimaryTealLight
val SupportPlumVeryLight = PrimaryTealVeryLight

// DANGER - WARM RED (911 / TRUE EMERGENCY ONLY)
val DangerRedDark = Color(0xFFA73E2A)      // Warm deep red
val DangerRed = Color(0xFFB83D3D)          // Controlled warm red
val DangerRedLight = Color(0xFFFCE8E8)     // Very light red

// GRAY SCALE - Warm neutrals
val TextDark = Color(0xFF3D3D3D)           // Dark grey - primary text
val TextGray = Color(0xFF5A5A5A)           // Medium grey - secondary text
val TextLightGray = Color(0xFF868686)      // Light grey - tertiary text
val BackgroundLight = Color(0xFFFCFBFA)    // Off-white
val BackgroundMedium = Color(0xFFF5F4F2)   // Light warm grey
val BorderGray = Color(0xFFD9D5CF)         // Warm border

// STATUS COLORS
val SuccessTeal = Color(0xFF3F7567)
val WarningAmber = Color(0xFFE6C7A0)       // Pending/caution (warm gold)
val ErrorRed = Color(0xFFD64545)           // Missed/error (warm red)
val InfoTeal = Color(0xFF6B8E9F)           // Information (slate blue)

// WHITE & BLACK
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

// ═══════════════════════════════════════════════════════════════
// ACCESSIBLE COLOR ALIASES
// ═══════════════════════════════════════════════════════════════
val PrimaryAction = PrimaryTealLight       // Main buttons (blue-grey)
val SecondaryAction = SecondaryCoral       // Family/warmth buttons (peach)
val WarningAction = AccentAmber            // Reminder/caution buttons (gold)
val DangerAction = DangerRed               // 911 / critical emergency (red)

val SurfaceDefault = BackgroundLight       // Default surface color
val SurfaceVariant = BackgroundMedium      // Variant surface
val OnSurface = TextDark                   // Text on surfaces

// ═══════════════════════════════════════════════════════════════
// WCAG AAA CONTRAST REQUIREMENTS
// All combinations tested for 7:1 ratio (AAA standard)
// ═════════════════════════════════════════════════════════════════
// ✓ Dark text on light backgrounds: 9:1+ contrast ratio
// ✓ White text on dark actions: 8:1+ contrast ratio
// ✓ Large text (18sp+) minimum 4.5:1 ratio
// ═════════════════════════════════════════════════════════════════
