package com.moriatsushi.nav3.samples.photodetail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PhotoLayoutState(
    private val dismissThresholdPx: Float,
    private val velocityThresholdPx: Float,
    private val scope: CoroutineScope,
    private val onBack: () -> Unit,
) {
    private val dragOffsetYAnim = Animatable(0f)
    private val dragProgress: Float
        get() = (dragOffsetYAnim.value / dismissThresholdPx).coerceIn(0f, 1f)

    private val navigationProgressAnim = Animatable(0f)

    val offsetY: Float get() = dragOffsetYAnim.value

    val dismissProgress: Float
        get() = maxOf(dragProgress, navigationProgressAnim.value)

    val scale: Float
        get() = 1f - (0.2f * dismissProgress)

    fun onDrag(delta: Float) {
        val newValue = (dragOffsetYAnim.value + delta).coerceAtLeast(0f)
        scope.launch { dragOffsetYAnim.snapTo(newValue) }
    }

    fun onDragStopped(velocity: Float) {
        val shouldDismiss =
            dragOffsetYAnim.value > dismissThresholdPx || velocity > velocityThresholdPx
        if (shouldDismiss) {
            onBack()
        } else {
            scope.launch { dragOffsetYAnim.animateTo(0f, animationSpec = spring()) }
        }
    }

    fun onNavigationProgress(event: NavigationEvent) {
        scope.launch {
            navigationProgressAnim.snapTo(event.progress)
        }
    }

    fun onNavigationBack() {
        onBack()
    }

    fun onNavigationCancel() {
        scope.launch {
            navigationProgressAnim.animateTo(0f)
        }
    }
}

@Composable
fun rememberPhotoLayoutState(
    dismissThreshold: Dp = 80.dp,
    velocityThreshold: Dp = 100.dp,
    onBack: () -> Unit,
): PhotoLayoutState {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val dismissPx = with(density) { dismissThreshold.toPx() }
    val velocityPx = with(density) { velocityThreshold.toPx() }
    val onBackState = rememberUpdatedState(onBack)
    return remember(dismissPx, velocityPx) {
        PhotoLayoutState(
            dismissThresholdPx = dismissPx,
            velocityThresholdPx = velocityPx,
            scope = scope,
            onBack = { onBackState.value.invoke() },
        )
    }
}
