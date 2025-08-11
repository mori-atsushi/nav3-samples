package com.moriatsushi.nav3.samples.system

import android.view.Window
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.LifecycleResumeEffect

@Composable
fun StatusBarAppearance(
    isLight: Boolean,
    window: Window = findWindow(),
) {
    LifecycleResumeEffect(Unit) {
        val windowInsetsController =
            WindowInsetsControllerCompat(window, window.decorView)
        val previousAppearance = windowInsetsController.isAppearanceLightStatusBars
        windowInsetsController.isAppearanceLightStatusBars = isLight
        onPauseOrDispose {
            windowInsetsController.isAppearanceLightStatusBars = previousAppearance
        }
    }
}

@Composable
private fun findWindow(): Window =
    LocalActivity.current?.window ?: error("No activity found")
