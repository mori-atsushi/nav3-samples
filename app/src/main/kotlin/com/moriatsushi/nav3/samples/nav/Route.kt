package com.moriatsushi.nav3.samples.nav

import androidx.annotation.DrawableRes

sealed interface Route {
    data object Top : Route
    data class PhotoDetail(@DrawableRes val resId: Int) : Route
}
