package com.moriatsushi.nav3.samples.nav

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

object NavTransitions {
    val fadeIn: EnterTransition = fadeIn(animationSpec = animationSpec())
    val fadeOut: ExitTransition = fadeOut(animationSpec = animationSpec())

    val fadeInOut: ContentTransform = ContentTransform(fadeIn, fadeOut)

    fun <T> animationSpec(visibilityThreshold: T? = null): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.8f,
        stiffness = 500f,
        visibilityThreshold = visibilityThreshold,
    )
}
