package com.moriatsushi.nav3.samples.nav

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
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
    private val overlayEntriesState: OverlayEntriesState<T>,
    override val previousEntries: List<NavEntry<T>>,
    private val onBack: (Int) -> Unit,
) : Scene<T> {
    override val entries: List<NavEntry<T>>
        get() = listOf(baseEntry) + overlayEntriesState.entries

    override val content: @Composable () -> Unit = {
        BackHandler(!overlayEntriesState.isEmpty()) { onBack(1) }

        baseEntry.Content()

        overlayEntriesState.transitionStateMap.forEach { (key, transitionState) ->
            key(key) {
                AnimatedOverlay(transitionState)
            }
        }
    }

    @Composable
    private fun AnimatedOverlay(
        transitionState: MutableTransitionState<NavEntry<T>?>,
        modifier: Modifier = Modifier,
    ) {
        val entry = transitionState.currentState ?: transitionState.targetState
        val transition = rememberTransition(transitionState)

        val enter =
            entry?.metadata[OVERLAY_ENTER_TRANSITION_KEY] as? EnterTransition ?: fadeIn()
        val exit =
            entry?.metadata[OVERLAY_EXIT_TRANSITION_KEY] as? ExitTransition ?: fadeOut()

        transition.AnimatedVisibility(
            modifier = modifier,
            visible = { it != null },
            enter = enter,
            exit = exit,
        ) {
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                entry?.Content()
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
        val overlayEntriesState = remember { OverlayEntriesState(overlayEntries) }
        overlayEntriesState.update(overlayEntries)
        return StackScene(
            key = baseEntry.contentKey,
            baseEntry = baseEntry,
            overlayEntriesState = overlayEntriesState,
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

class OverlayEntriesState<T : Any>(initialEntries: List<NavEntry<T>>) {
    private val _transitionStateMap: MutableMap<Any, MutableTransitionState<NavEntry<T>?>> =
        initialEntries
            .associate { it.contentKey to MutableTransitionState<NavEntry<T>?>(it) }
            .toMutableMap()
    val transitionStateMap: Map<Any, MutableTransitionState<NavEntry<T>?>> = _transitionStateMap

    val entries: List<NavEntry<T>>
        get() = transitionStateMap.values.mapNotNull { it.currentState ?: it.targetState }

    fun update(entries: List<NavEntry<T>>) {
        val previousKeys = _transitionStateMap.keys.toSet()
        for (entry in entries) {
            val transitionState = _transitionStateMap.getOrPut(entry.contentKey) {
                MutableTransitionState(null)
            }
            transitionState.targetState = entry
        }

        val newKeys = entries.map { it.contentKey }.toSet()
        val removedKeys = previousKeys - newKeys
        for (key in removedKeys) {
            val transitionState = _transitionStateMap[key]
            if (transitionState?.currentState == null && transitionState?.targetState == null) {
                _transitionStateMap.remove(key)
            } else {
                _transitionStateMap[key]?.targetState = null
            }
        }
    }

    fun isEmpty(): Boolean = entries.isEmpty()
}

val LocalNavAnimatedVisibilityScope: ProvidableCompositionLocal<AnimatedVisibilityScope> =
    compositionLocalOf {
        throw IllegalStateException(
            "Unexpected access to LocalNavAnimatedVisibilityScope. You should only " +
                "access LocalNavAnimatedVisibilityScope inside a NavEntry with " +
                "StackSceneStrategy.overlay() metadata.",
        )
    }

