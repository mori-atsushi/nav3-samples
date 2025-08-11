package com.moriatsushi.nav3.samples.system

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.LifecycleResumeEffect

@Composable
fun StatusBarAppearance(isLight: Boolean) {
    val activity = LocalActivity.current ?: return

    LifecycleResumeEffect(Unit) {
        val windowInsetsController =
            WindowInsetsControllerCompat(activity.window, activity.window.decorView)
        val previousAppearance = windowInsetsController.isAppearanceLightStatusBars
        windowInsetsController.isAppearanceLightStatusBars = isLight
        onPauseOrDispose {
            windowInsetsController.isAppearanceLightStatusBars = previousAppearance
        }
    }
}
