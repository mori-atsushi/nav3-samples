package com.moriatsushi.nav3.samples.nav

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import com.moriatsushi.nav3.samples.photodetail.PhotoDetailScreen
import com.moriatsushi.nav3.samples.top.TopScreen

@OptIn(ExperimentalSharedTransitionApi::class)
fun EntryProviderBuilder<Route>.entries(
    backStack: MutableList<Route>,
    sharedTransitionScope: SharedTransitionScope,
) {
    entry<Route.Top> {
        TopScreen(
            onPhotoClick = { resId ->
                backStack.add(Route.PhotoDetail(resId))
            },
            sharedTransitionScope = sharedTransitionScope,
            photoDetailPage = backStack.getOrNull(1) as? Route.PhotoDetail,
        )
    }
    entry<Route.PhotoDetail>(
        metadata = StackSceneStrategy.overlay(
            enter = NavTransitions.fadeIn,
            exit = NavTransitions.fadeOut,
        ),
    ) { entry ->
        PhotoDetailScreen(
            resId = entry.resId,
            onBack = { backStack.removeLastOrNull() },
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current,
        )
    }
}
