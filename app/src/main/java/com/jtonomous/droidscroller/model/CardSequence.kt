package com.jtonomous.droidscroller.model

data class CardSequence(
    val cards: List<Card>,
    val focusedIndex: Int
) {
    val focusedCard: Card?
        get() = if (focusedIndex in cards.indices) cards[focusedIndex] else null

    val neighborBefore: Card?
        get() {
            val beforeIndex = focusedIndex - 1
            return if (beforeIndex in cards.indices) cards[beforeIndex] else null
        }

    val neighborAfter: Card?
        get() {
            val afterIndex = focusedIndex + 1
            return if (afterIndex in cards.indices) cards[afterIndex] else null
        }

    fun moveForward(): CardSequence {
        val nextIndex = (focusedIndex + 1).coerceAtMost(cards.size - 1)
        return copy(focusedIndex = nextIndex)
    }

    fun moveBackward(): CardSequence {
        val prevIndex = (focusedIndex - 1).coerceAtLeast(0)
        return copy(focusedIndex = prevIndex)
    }
}
