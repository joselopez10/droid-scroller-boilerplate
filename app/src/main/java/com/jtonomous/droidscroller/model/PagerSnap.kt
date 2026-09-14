package com.jtonomous.droidscroller.model

fun snapPageDelta(
    dragDistance: Float,
    pageExtent: Float,
    currentIndex: Int,
    pageCount: Int,
    thresholdFraction: Float = 0.2f
): Int {
    require(pageExtent > 0f) { "Page extent must be positive" }
    require(pageCount >= 0) { "Page count cannot be negative" }
    require(thresholdFraction in 0f..1f) { "Threshold fraction must be between 0 and 1" }

    if (pageCount == 0 || kotlin.math.abs(dragDistance) < pageExtent * thresholdFraction) {
        return 0
    }

    val requestedDelta = if (dragDistance < 0f) 1 else -1
    val targetIndex = (currentIndex + requestedDelta).coerceIn(0, pageCount - 1)
    return targetIndex - currentIndex
}
