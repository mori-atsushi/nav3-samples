package com.moriatsushi.nav3.samples.nav

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import com.moriatsushi.nav3.samples.photodetail.PhotoDetailScreen
import com.moriatsushi.nav3.samples.photoinfo.PhotoInfoScreen
import com.moriatsushi.nav3.samples.photopreview.PhotoPreviewScreen
import com.moriatsushi.nav3.samples.top.TopScreen

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
fun EntryProviderBuilder<Route>.entries(
    backStack: BackStack,
    sharedTransitionScope: SharedTransitionScope,
) {
    entry<Route.Top> {
        TopScreen(
            onPhotoClick = { resId ->
                backStack.navigateTo(Route.PhotoDetail(resId))
            },
            onPhotoLongClick = { resId ->
                backStack.navigateTo(Route.PhotoPreview(resId))
            },
            sharedTransitionScope = sharedTransitionScope,
            selectedPhoto = backStack.selectedPhoto,
        )
    }
    entry<Route.PhotoPreview>(
        metadata = StackSceneStrategy.overlay(
            enter = NavTransitions.fadeIn,
            exit = NavTransitions.fadeOut,
        ),
    ) { entry ->
        PhotoPreviewScreen(
            resId = entry.resId,
            onBack = { backStack.back() },
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current,
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
            onBack = { backStack.back() },
            onInfoClick = { backStack.navigateTo(Route.PhotoInfo) },
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current,
        )
    }
    entry<Route.PhotoInfo>(
        metadata = BottomSheetModalSceneStrategy.bottomSheetModal(),
    ) {
        PhotoInfoScreen()
    }
}
