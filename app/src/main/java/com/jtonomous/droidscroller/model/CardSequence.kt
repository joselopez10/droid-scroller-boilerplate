package com.jtonomous.droidscroller.model

data class CardSequence(
    val cards: List<Card>,
    val focusedIndex: Int,
    val navigationMode: NavigationMode = NavigationMode.FINITE
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

    fun insertAtTop(card: Card): CardSequence {
        return copy(
            cards = listOf(card) + cards,
            focusedIndex = 0
        )
    }

    fun deleteFocused(): CardSequence {
        if (focusedIndex !in cards.indices) {
            return this
        }

        val remainingCards = cards.toMutableList().also { it.removeAt(focusedIndex) }
        return copy(
            cards = remainingCards,
            focusedIndex = if (remainingCards.isEmpty()) {
                0
            } else {
                focusedIndex.coerceAtMost(remainingCards.lastIndex)
            }
        )
    }

    fun moveForward(): CardSequence {
        if (cards.isEmpty()) {
            return copy(focusedIndex = 0)
        }
        val nextIndex = when (navigationMode) {
            NavigationMode.FINITE -> (focusedIndex + 1).coerceAtMost(cards.lastIndex)
            NavigationMode.LOOP -> (focusedIndex + 1).mod(cards.size)
        }
        return copy(focusedIndex = nextIndex)
    }

    fun moveBackward(): CardSequence {
        if (cards.isEmpty()) {
            return copy(focusedIndex = 0)
        }
        val prevIndex = when (navigationMode) {
            NavigationMode.FINITE -> (focusedIndex - 1).coerceAtLeast(0)
            NavigationMode.LOOP -> (focusedIndex - 1 + cards.size).mod(cards.size)
        }
        return copy(focusedIndex = prevIndex)
    }
}
