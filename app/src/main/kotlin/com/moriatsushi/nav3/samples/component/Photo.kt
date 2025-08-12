package com.moriatsushi.nav3.samples.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Constraints
import com.moriatsushi.nav3.samples.nav.NavTransitions
import kotlin.math.roundToInt

@ExperimentalSharedTransitionApi
@Composable
fun Photo(
    @DrawableRes resId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {
        val painter = painterResource(id = resId)
        Image(
            painter = painter,
            contentDescription = null,
            modifier = modifier
                // Shared element transitions do not support animating `ContentScale`.
                // So we lay out the image with a custom modifier.
                .contentScaleFit(painter.intrinsicSize)
                .sharedElement(
                    rememberSharedContentState(key = resId),
                    animatedVisibilityScope,
                    boundsTransform = { _, _ ->
                        NavTransitions.animationSpec(Rect.VisibilityThreshold)
                    },
                ),
            contentScale = ContentScale.Crop, // Use crop for shared bounds
        )
    }
}


/**
 * Layouts the child at the largest size that fits within the given constraints
 * while preserving aspect ratio — equivalent to [ContentScale.Fit].
 */
private fun Modifier.contentScaleFit(intrinsicSize: Size): Modifier =
    layout { measurable, constraints ->
        val dstSize = Size(
            constraints.maxWidth.toFloat(),
            constraints.maxHeight.toFloat(),
        )
        val scale = ContentScale.Fit.computeScaleFactor(intrinsicSize, dstSize)

        val targetWidth = (intrinsicSize.width * scale.scaleX).roundToInt()
            .coerceIn(constraints.minWidth, constraints.maxWidth)
        val targetHeight = (intrinsicSize.height * scale.scaleY).roundToInt()
            .coerceIn(constraints.minHeight, constraints.maxHeight)

        val placeable = measurable.measure(
            Constraints.fixed(targetWidth, targetHeight),
        )
        layout(targetWidth, targetHeight) {
            placeable.place(0, 0)
        }
    }
