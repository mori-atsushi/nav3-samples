package com.moriatsushi.nav3.samples.nav

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.Scene
import androidx.navigation3.ui.SceneStrategy
import java.lang.IllegalStateException

private const val OVERLAY_ENTER_TRANSITION_KEY = "overlay_enter_transition"
private const val OVERLAY_EXIT_TRANSITION_KEY = "overlay_exit_transition"

data class StackScene<T : Any>(
    override val key: Any,
    private val baseEntry: NavEntry<T>,
    private val overlayEntryTransition: Transition<NavEntry<T>?>,
    override val previousEntries: List<NavEntry<T>>,
    private val onBack: (Int) -> Unit,
) : Scene<T> {
    private val overlapEntry: NavEntry<T>?
        get() = overlayEntryTransition.currentState ?: overlayEntryTransition.targetState

    override val entries: List<NavEntry<T>>
        get() = listOfNotNull(baseEntry, overlapEntry)

    override val content: @Composable () -> Unit = {
        BackHandler(overlayEntryTransition.currentState != null) { onBack(1) }

        baseEntry.Content()

        val enter =
            overlapEntry?.metadata[OVERLAY_ENTER_TRANSITION_KEY] as? EnterTransition ?: fadeIn()
        val exit =
            overlapEntry?.metadata[OVERLAY_EXIT_TRANSITION_KEY] as? ExitTransition ?: fadeOut()

        overlayEntryTransition.AnimatedVisibility(
            visible = { it != null },
            enter = enter,
            exit = exit,
        ) {
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                overlapEntry?.Content()
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
        val remainingEntries = entries.toMutableList()
        val overlayEntry = if (remainingEntries.lastOrNull()?.isOverlay == true) {
            remainingEntries.removeLastOrNull()
        } else {
            null
        }
        val baseEntry = remainingEntries.removeLastOrNull() ?: return null
        val transitionState = remember(baseEntry.contentKey) {
            MutableTransitionState(overlayEntry)
        }
        transitionState.targetState = overlayEntry
        val transition = rememberTransition(transitionState)
        return StackScene(
            key = baseEntry.contentKey,
            baseEntry = baseEntry,
            overlayEntryTransition = transition,
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

