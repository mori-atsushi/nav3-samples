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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PhotoDraggableState(
    private val dismissThresholdPx: Float,
    private val velocityThresholdPx: Float,
    private val scope: CoroutineScope,
    private val onBack: () -> Unit,
) {
    private val offsetYAnim = Animatable(0f)

    val offsetY: Float get() = offsetYAnim.value

    fun onDrag(delta: Float) {
        val newValue = (offsetYAnim.value + delta).coerceAtLeast(0f)
        scope.launch { offsetYAnim.snapTo(newValue) }
    }

    val progress: Float
        get() = (offsetYAnim.value / dismissThresholdPx).coerceIn(0f, 1f)

    fun onDragStopped(velocity: Float) {
        val shouldDismiss =
            offsetYAnim.value > dismissThresholdPx || velocity > velocityThresholdPx
        if (shouldDismiss) {
            onBack()
        } else {
            scope.launch { offsetYAnim.animateTo(0f, animationSpec = spring()) }
        }
    }
}

@Composable
fun rememberPhotoDraggableState(
    dismissThreshold: Dp = 80.dp,
    velocityThreshold: Dp = 100.dp,
    onBack: () -> Unit,
): PhotoDraggableState {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val dismissPx = with(density) { dismissThreshold.toPx() }
    val velocityPx = with(density) { velocityThreshold.toPx() }
    val onBackState = rememberUpdatedState(onBack)
    return remember(dismissPx, velocityPx) {
        PhotoDraggableState(
            dismissThresholdPx = dismissPx,
            velocityThresholdPx = velocityPx,
            scope = scope,
            onBack = { onBackState.value.invoke() },
        )
    }
}
