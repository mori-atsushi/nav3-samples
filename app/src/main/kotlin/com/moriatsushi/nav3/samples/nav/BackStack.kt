package com.moriatsushi.nav3.samples.nav

import androidx.annotation.DrawableRes
import androidx.compose.runtime.mutableStateListOf

class BackStack {
    private val _list = mutableStateListOf<Route>(Route.Top)
    val list: List<Route> get() = _list

    val selectedPhoto: Int?
        @DrawableRes
        get() = when (val entry = _list.getOrNull(1)) {
            is Route.PhotoPreview -> entry.resId
            is Route.PhotoDetail -> entry.resId
            else -> null
        }

    fun back(count: Int = 1) {
        repeat(count) {
            if (_list.size > 1) {
                _list.removeLastOrNull()
            }
        }
    }

    fun navigateTo(route: Route) {
        _list.add(route)
    }
}
