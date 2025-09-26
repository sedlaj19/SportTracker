package com.sporttracker.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
actual fun getOrientationMode(): OrientationMode {
    val configuration = LocalConfiguration.current
    return if (configuration.screenWidthDp > configuration.screenHeightDp) {
        OrientationMode.LANDSCAPE
    } else {
        OrientationMode.PORTRAIT
    }
}

@Composable
actual fun getWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    return when {
        configuration.screenWidthDp < 600 -> WindowSizeClass.COMPACT
        configuration.screenWidthDp < 840 -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }
}