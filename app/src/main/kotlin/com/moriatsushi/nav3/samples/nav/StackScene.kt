package com.moriatsushi.nav3.samples.nav

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.Scene
import androidx.navigation3.ui.SceneStrategy

data class StackScene<T : Any>(
    override val key: Any,
    private val baseEntry: NavEntry<T>,
    private val overlayEntry: NavEntry<T>?,
    override val previousEntries: List<NavEntry<T>>,
) : Scene<T> {
    override val entries: List<NavEntry<T>> =
        listOfNotNull(baseEntry, overlayEntry)

    override val content: @Composable () -> Unit = {
        baseEntry.Content()
        overlayEntry?.Content()
    }
}

class StackSceneStrategy<T : Any> : SceneStrategy<T> {
    @Composable
    override fun calculateScene(
        entries: List<NavEntry<T>>,
        onBack: (Int) -> Unit,
    ): Scene<T>? {
        val remainingEntries = entries.toMutableList()
        val overlayEntry =
            if (remainingEntries.lastOrNull()?.metadata?.containsKey(OVERLAY_KEY) == true) {
                remainingEntries.removeLastOrNull()
            } else {
                null
            }
        val baseEntry = remainingEntries.removeLastOrNull() ?: return null
        return StackScene(
            key = baseEntry.contentKey,
            baseEntry = baseEntry,
            overlayEntry = overlayEntry,
            previousEntries = remainingEntries,
        )
    }

    companion object {
        fun overlay(): Map<String, Any> = mapOf(OVERLAY_KEY to true)

        internal const val OVERLAY_KEY = "overlay"
    }
}
