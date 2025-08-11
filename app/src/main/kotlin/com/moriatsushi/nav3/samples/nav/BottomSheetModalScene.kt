package com.moriatsushi.nav3.samples.nav

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.OverlayScene
import androidx.navigation3.ui.Scene
import androidx.navigation3.ui.SceneStrategy
import com.moriatsushi.nav3.samples.system.StatusBarAppearance

@OptIn(ExperimentalMaterial3Api::class)
class BottomSheetModalScene<T : Any>(
    override val key: Any,
    private val entry: NavEntry<T>,
    override val overlaidEntries: List<NavEntry<T>>,
    override val previousEntries: List<NavEntry<T>>,
    private val properties: ModalBottomSheetProperties,
    private val onBack: (count: Int) -> Unit,
) : OverlayScene<T> {
    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        ModalBottomSheet(onDismissRequest = { onBack(1) }, properties = properties) {
            val window = (LocalView.current.parent as? DialogWindowProvider)?.window
            if (window != null) {
                StatusBarAppearance(isLight = false, window = window)
            }
            entry.Content()
        }
    }
}

class BottomSheetModalSceneStrategy<T : Any> : SceneStrategy<T> {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun calculateScene(
        entries: List<NavEntry<T>>,
        onBack: (Int) -> Unit,
    ): Scene<T>? {
        val lastEntry = entries.lastOrNull() ?: return null
        val properties = lastEntry.metadata[BOTTOM_SHEET_MODAL_KEY] as? ModalBottomSheetProperties
            ?: return null
        return BottomSheetModalScene(
            key = lastEntry.contentKey,
            previousEntries = entries.dropLast(1),
            overlaidEntries = entries.dropLast(1),
            entry = lastEntry,
            properties = properties,
            onBack = onBack,
        )
    }

    companion object {
        internal const val BOTTOM_SHEET_MODAL_KEY = "bottom_sheet_modal"

        @ExperimentalMaterial3Api
        fun bottomSheetModal(
            properties: ModalBottomSheetProperties = ModalBottomSheetProperties(),
        ): Map<String, Any> = mapOf(BOTTOM_SHEET_MODAL_KEY to properties)
    }
}
