package com.jtonomous.droidscroller.model

data class InterestSequence(
    val interests: List<Interest>,
    val activeIndex: Int
) {
    val activeInterest: Interest?
        get() = if (activeIndex in interests.indices) interests[activeIndex] else null

    fun moveForward(): InterestSequence {
        val nextIndex = (activeIndex + 1).coerceAtMost(interests.size - 1)
        return copy(activeIndex = nextIndex)
    }

    fun moveBackward(): InterestSequence {
        val previousIndex = (activeIndex - 1).coerceAtLeast(0)
        return copy(activeIndex = previousIndex)
    }
}
