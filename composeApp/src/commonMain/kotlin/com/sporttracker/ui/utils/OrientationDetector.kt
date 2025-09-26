package com.sporttracker.ui.utils

import androidx.compose.runtime.Composable

enum class OrientationMode {
    PORTRAIT,
    LANDSCAPE
}

enum class WindowSizeClass {
    COMPACT,    // Phone portrait
    MEDIUM,     // Phone landscape, small tablet
    EXPANDED    // Large tablet, desktop
}

/**
 * Platform-specific orientation detection
 * Each platform provides its own implementation
 */
@Composable
expect fun getOrientationMode(): OrientationMode

/**
 * Platform-specific window size class detection
 * Each platform provides its own implementation
 */
@Composable
expect fun getWindowSizeClass(): WindowSizeClass