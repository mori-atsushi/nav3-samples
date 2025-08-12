package com.moriatsushi.nav3.samples.nav

import androidx.annotation.DrawableRes

sealed interface Route {
    data object Top : Route
    data class PhotoPreview(@param:DrawableRes val resId: Int) : Route
    data class PhotoDetail(@param:DrawableRes val resId: Int) : Route
    data object PhotoInfo: Route
}
