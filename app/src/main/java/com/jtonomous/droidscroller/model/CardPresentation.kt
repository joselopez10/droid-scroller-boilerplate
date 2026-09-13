package com.jtonomous.droidscroller.model

data class CardPresentation(
    val scale: Float,
    val alpha: Float
)

data class CardLayoutSize(
    val width: Float,
    val height: Float
) {
    init {
        require(width >= 0f) { "Layout width cannot be negative" }
        require(height >= 0f) { "Layout height cannot be negative" }
    }
}

fun cardPresentationFor(relativePosition: Int): CardPresentation {
    return when (kotlin.math.abs(relativePosition)) {
        0 -> CardPresentation(scale = 1f, alpha = 1f)
        1 -> CardPresentation(scale = 0.9f, alpha = 0.5f)
        else -> CardPresentation(scale = 0.8f, alpha = 0.25f)
    }
}

fun cardPresentationFor(
    relativePosition: Int,
    layoutSize: CardLayoutSize
): CardPresentation {
    val distance = kotlin.math.abs(relativePosition)
    if (distance == 0) {
        return CardPresentation(scale = 1f, alpha = 1f)
    }

    val sizeProgress = (
        (minOf(layoutSize.width, layoutSize.height) - 480f) / 320f
    ).coerceIn(0f, 1f)

    return when (distance) {
        1 -> CardPresentation(
            scale = 0.86f + (0.08f * sizeProgress),
            alpha = 0.4f + (0.2f * sizeProgress)
        )

        else -> CardPresentation(
            scale = 0.72f + (0.16f * sizeProgress),
            alpha = 0.15f + (0.2f * sizeProgress)
        )
    }
}
