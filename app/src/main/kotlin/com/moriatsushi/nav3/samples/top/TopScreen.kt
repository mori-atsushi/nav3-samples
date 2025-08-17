package com.moriatsushi.nav3.samples.top

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moriatsushi.nav3.samples.R
import com.moriatsushi.nav3.samples.nav.LocalOverlayTransition
import com.moriatsushi.nav3.samples.nav.NavTransitions
import com.moriatsushi.nav3.samples.nav.OverlayType

private val PhotoResIds: List<Int> = listOf(
    R.drawable.photo_1,
    R.drawable.photo_2,
    R.drawable.photo_3,
    R.drawable.photo_4,
    R.drawable.photo_5,
    R.drawable.photo_6,
    R.drawable.photo_7,
    R.drawable.photo_8,
    R.drawable.photo_9,
    R.drawable.photo_10,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TopScreen(
    onPhotoClick: (resId: Int) -> Unit,
    onPhotoLongClick: (resId: Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
    @DrawableRes selectedPhoto: Int? = null,
) {
    val blurRadius = blurRadius()
    Scaffold(
        modifier = modifier
            .blur(blurRadius)
            .fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Photos") }) },
    ) { contentPadding ->
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            columns = GridCells.Adaptive(minSize = 120.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = contentPadding,
        ) {
            items(PhotoResIds) { resId ->
                ImageCell(
                    resId = resId,
                    sharedTransitionScope = sharedTransitionScope,
                    visible = selectedPhoto != resId,
                    onClick = { onPhotoClick(resId) },
                    onLongClick = { onPhotoLongClick(resId) },
                )
            }
        }
    }
}

@Composable
private fun blurRadius(): Dp {
    val overlayTransition = LocalOverlayTransition.current
    val initialOverlayType =
        overlayTransition.currentState.lastOrNull()?.metadata?.let { OverlayType.fromMetadata(it) }
    val targetOverlayType =
        overlayTransition.targetState.lastOrNull()?.metadata?.let { OverlayType.fromMetadata(it) }

    return when {
        targetOverlayType == OverlayType.FLOATING -> 10.dp
        initialOverlayType == OverlayType.FLOATING &&
            targetOverlayType == OverlayType.FULLSCREEN -> 10.dp

        else -> 0.dp
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ImageCell(
    @DrawableRes resId: Int,
    sharedTransitionScope: SharedTransitionScope,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = NavTransitions.fadeIn,
        exit = NavTransitions.fadeOut,
    ) {
        with(sharedTransitionScope) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .sharedElement(
                        rememberSharedContentState(key = resId),
                        this@AnimatedVisibility,
                        boundsTransform = { _, _ ->
                            NavTransitions.animationSpec(Rect.VisibilityThreshold)
                        },
                    )
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick,
                    ),
                contentScale = ContentScale.Crop,
            )
        }
    }
}
