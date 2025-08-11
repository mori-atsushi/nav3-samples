package com.moriatsushi.nav3.samples.photodetail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.res.painterResource
import androidx.navigationevent.compose.NavigationEventHandler
import com.moriatsushi.nav3.samples.nav.NavTransitions
import com.moriatsushi.nav3.samples.system.StatusBarAppearance
import kotlinx.coroutines.CancellationException
import kotlin.math.roundToInt

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PhotoDetailScreen(
    @DrawableRes resId: Int,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    StatusBarAppearance(isLight = false)

    val photoLayoutState = rememberPhotoLayoutState(onBack = onBack)

    NavigationEventHandler({ true }) { navEvent ->
        try {
            navEvent.collect { event ->
                photoLayoutState.onNavigationProgress(event)
            }
            photoLayoutState.onNavigationBack()
        } catch (_: CancellationException) {
            photoLayoutState.onNavigationCancel()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Black.copy(alpha = 1f - (0.75f * photoLayoutState.dismissProgress)),
        topBar = {
            PhotoTopBar(
                modifier = modifier.graphicsLayer { alpha = 1f - photoLayoutState.dismissProgress },
                onBack = onBack,
            )
        },
    ) { contentPadding ->
        with(sharedTransitionScope) {
            PhotoDetailScreenContent(
                modifier = Modifier.padding(contentPadding),
                resId = resId,
                photoLayoutState = photoLayoutState,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier,
        title = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            navigationIconContentColor = Color.White,
        ),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        },
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PhotoDetailScreenContent(
    @DrawableRes resId: Int,
    photoLayoutState: PhotoLayoutState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .draggable(
                state = rememberDraggableState { delta -> photoLayoutState.onDrag(delta) },
                orientation = Orientation.Vertical,
                onDragStopped = { velocity -> photoLayoutState.onDragStopped(velocity) },
            )
            .offset { IntOffset(0, photoLayoutState.offsetY.roundToInt()) }
            .wrapContentSize()
            .fillMaxSize(photoLayoutState.scale),
        contentAlignment = Alignment.Center,
    ) {
        with(sharedTransitionScope) {
            val painter = painterResource(id = resId)
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
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
