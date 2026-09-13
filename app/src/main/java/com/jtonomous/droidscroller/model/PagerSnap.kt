package com.jtonomous.droidscroller.model

fun snapPageDelta(
    dragDistance: Float,
    pageExtent: Float,
    currentIndex: Int,
    pageCount: Int
): Int {
    require(pageExtent > 0f) { "Page extent must be positive" }
    require(pageCount >= 0) { "Page count cannot be negative" }

    if (pageCount == 0 || kotlin.math.abs(dragDistance) < pageExtent / 2f) {
        return 0
    }

    val requestedDelta = if (dragDistance < 0f) 1 else -1
    val targetIndex = (currentIndex + requestedDelta).coerceIn(0, pageCount - 1)
    return targetIndex - currentIndex
}
