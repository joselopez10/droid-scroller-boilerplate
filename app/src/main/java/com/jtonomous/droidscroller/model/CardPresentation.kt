package com.jtonomous.droidscroller.model

data class CardPresentation(
    val scale: Float,
    val alpha: Float
)

fun cardPresentationFor(relativePosition: Int): CardPresentation {
    return when (kotlin.math.abs(relativePosition)) {
        0 -> CardPresentation(scale = 1f, alpha = 1f)
        1 -> CardPresentation(scale = 0.9f, alpha = 0.5f)
        else -> CardPresentation(scale = 0.8f, alpha = 0.25f)
    }
}
