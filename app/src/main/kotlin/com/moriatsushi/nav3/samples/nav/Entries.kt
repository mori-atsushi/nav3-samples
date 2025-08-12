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
    backStack: MutableList<Route>,
    sharedTransitionScope: SharedTransitionScope,
) {
    entry<Route.Top> {
        TopScreen(
            onPhotoClick = { resId ->
                backStack.add(Route.PhotoDetail(resId))
            },
            onPhotoLongClick = { resId ->
                backStack.add(Route.PhotoPreview(resId))
            },
            sharedTransitionScope = sharedTransitionScope,
            photoDetailPage = backStack.getOrNull(1) as? Route.PhotoDetail,
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
            onBack = { backStack.removeLastOrNull() },
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
            onInfoClick = { backStack.add(Route.PhotoInfo) },
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
