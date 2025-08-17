package com.moriatsushi.nav3.samples.nav

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.Scene
import androidx.navigation3.ui.SceneStrategy
import java.lang.IllegalStateException

private const val OVERLAY_ENTER_TRANSITION_KEY = "overlay_enter_transition"
private const val OVERLAY_EXIT_TRANSITION_KEY = "overlay_exit_transition"

data class StackScene<T : Any>(
    override val key: Any,
    private val baseEntry: NavEntry<T>,
    private val overlayTransition: Transition<List<NavEntry<T>>>,
    override val previousEntries: List<NavEntry<T>>,
    private val onBack: (Int) -> Unit,
) : Scene<T> {
    private val overlayEntries: List<NavEntry<T>>
        get() = (overlayTransition.currentState + overlayTransition.targetState)
            .distinctBy { it.contentKey }

    override val entries: List<NavEntry<T>>
        get() = listOf(baseEntry) + overlayEntries

    override val content: @Composable () -> Unit = {
        BackHandler(!overlayEntries.isEmpty()) { onBack(1) }

        baseEntry.Content()

        for (entry in overlayEntries) {
            key(entry.contentKey) {
                AnimatedOverlay(entry)
            }
        }
    }

    @Composable
    private fun AnimatedOverlay(entry: NavEntry<T>, modifier: Modifier = Modifier) {
        val enter =
            entry.metadata[OVERLAY_ENTER_TRANSITION_KEY] as? EnterTransition ?: fadeIn()
        val exit =
            entry.metadata[OVERLAY_EXIT_TRANSITION_KEY] as? ExitTransition ?: fadeOut()

        overlayTransition.AnimatedVisibility(
            modifier = modifier,
            visible = { it.contains(entry) },
            enter = enter,
            exit = exit,
        ) {
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                entry.Content()
            }
        }
    }
}

class StackSceneStrategy<T : Any> : SceneStrategy<T> {
    @Composable
    override fun calculateScene(
        entries: List<NavEntry<T>>,
        onBack: (Int) -> Unit,
    ): Scene<T>? {
        val overlayEntries = entries.takeLastWhile { it.isOverlay }
        val overlayCount = overlayEntries.size
        val baseEntry = entries.getOrNull(entries.lastIndex - overlayCount) ?: return null
        val remainingEntries = entries.dropLast(overlayCount + 1)
        val overlayTransition = updateTransition(overlayEntries)
        return StackScene(
            key = baseEntry.contentKey,
            baseEntry = baseEntry,
            overlayTransition = overlayTransition,
            previousEntries = remainingEntries,
            onBack = onBack,
        )
    }

    companion object {
        private const val OVERLAY_KEY = "overlay"

        private val NavEntry<*>.isOverlay: Boolean get() = metadata[OVERLAY_KEY] == true

        fun overlay(
            enter: EnterTransition = fadeIn(),
            exit: ExitTransition = fadeOut(),
        ): Map<String, Any> = mapOf(
            OVERLAY_KEY to true,
            OVERLAY_ENTER_TRANSITION_KEY to enter,
            OVERLAY_EXIT_TRANSITION_KEY to exit,
        )
    }
}

val LocalNavAnimatedVisibilityScope: ProvidableCompositionLocal<AnimatedVisibilityScope> =
    compositionLocalOf {
        throw IllegalStateException(
            "Unexpected access to LocalNavAnimatedVisibilityScope. You should only " +
                "access LocalNavAnimatedVisibilityScope inside a NavEntry with " +
                "StackSceneStrategy.overlay() metadata.",
        )
    }

