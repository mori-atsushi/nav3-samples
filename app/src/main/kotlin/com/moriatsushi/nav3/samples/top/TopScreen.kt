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
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moriatsushi.nav3.samples.R
import com.moriatsushi.nav3.samples.nav.NavTransitions
import com.moriatsushi.nav3.samples.nav.Route

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
    photoDetailPage: Route.PhotoDetail?,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
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
                    photoDetailPage = photoDetailPage,
                    onPhotoClick = onPhotoClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ImageCell(
    @DrawableRes resId: Int,
    sharedTransitionScope: SharedTransitionScope,
    photoDetailPage: Route.PhotoDetail?,
    onPhotoClick: (resId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = photoDetailPage?.resId != resId,
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
                        boundsTransform =  { _, _ ->
                            NavTransitions.animationSpec(Rect.VisibilityThreshold)
                        },
                    )
                    .clickable { onPhotoClick(resId) },
                contentScale = ContentScale.Crop,
            )
        }
    }
}
