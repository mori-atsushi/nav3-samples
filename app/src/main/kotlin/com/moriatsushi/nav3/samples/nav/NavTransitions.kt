package com.moriatsushi.nav3.samples.nav

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

object NavTransitions {
    val fadeInOut: ContentTransform = ContentTransform(
        fadeIn(animationSpec = animationSpec()),
        fadeOut(animationSpec = animationSpec()),
    )

    fun <T> animationSpec(visibilityThreshold: T? = null): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.8f,
        stiffness = 500f,
        visibilityThreshold = visibilityThreshold,
    )
}
