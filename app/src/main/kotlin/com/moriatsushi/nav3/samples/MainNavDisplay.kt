package com.moriatsushi.nav3.samples

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.moriatsushi.nav3.samples.nav.BackStack
import com.moriatsushi.nav3.samples.nav.BottomSheetModalSceneStrategy
import com.moriatsushi.nav3.samples.nav.NavTransitions
import com.moriatsushi.nav3.samples.nav.Route
import com.moriatsushi.nav3.samples.nav.StackSceneStrategy
import com.moriatsushi.nav3.samples.nav.entries

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNavDisplay(modifier: Modifier = Modifier) {
    val backStack = remember { BackStack() }

    SharedTransitionLayout(modifier = modifier) {
        NavDisplay(
            backStack = backStack.list,
            onBack = { backStack.back(it) },
            sceneStrategy = remember {
                BottomSheetModalSceneStrategy<Route>()
                    .then(StackSceneStrategy())
            },
            transitionSpec = { NavTransitions.fadeInOut },
            popTransitionSpec = { NavTransitions.fadeInOut },
            entryProvider = entryProvider {
                entries(
                    backStack = backStack,
                    sharedTransitionScope = this@SharedTransitionLayout,
                )
            },
        )
    }
}
