package com.moriatsushi.nav3.samples.photodetail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.annotation.DrawableRes
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.res.painterResource
import com.moriatsushi.nav3.samples.nav.NavTransitions
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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Black,
        topBar = { PhotoTopBar(onBack) },
    ) { contentPadding ->
        with(sharedTransitionScope) {
            PhotoDetailScreenContent(
                modifier = Modifier.padding(contentPadding),
                resId = resId,
                onBack = onBack,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
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
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val photoDraggableState = rememberPhotoDraggableState(onBack = onBack)

    with(sharedTransitionScope) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = modifier
                .fillMaxSize()
                .draggable(
                    state = rememberDraggableState { delta -> photoDraggableState.onDrag(delta) },
                    orientation = Orientation.Vertical,
                    onDragStopped = { velocity -> photoDraggableState.onDragStopped(velocity) },
                )
                .offset { IntOffset(0, photoDraggableState.offsetY.roundToInt()) }
                .sharedBounds(
                    rememberSharedContentState(key = resId),
                    animatedVisibilityScope,
                    boundsTransform = BoundsTransform { _, _ ->
                        NavTransitions.animationSpec(Rect.VisibilityThreshold)
                    },
                ),
            contentScale = ContentScale.Fit,
        )
    }
}
