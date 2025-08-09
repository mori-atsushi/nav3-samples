package com.moriatsushi.nav3.samples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.moriatsushi.nav3.samples.nav.Route
import com.moriatsushi.nav3.samples.photodetail.PhotoDetailScreen
import com.moriatsushi.nav3.samples.top.TopScreen

@Composable
fun MainNavDisplay(modifier: Modifier = Modifier) {
    val backStack = remember { mutableStateListOf<Any>(Route.Top) }
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Route.Top> {
                TopScreen(
                    onPhotoClick = { resId ->
                        backStack.add(Route.PhotoDetail(resId))
                    },
                )
            }
            entry<Route.PhotoDetail> { entry ->
                PhotoDetailScreen(
                    resId = entry.resId,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
