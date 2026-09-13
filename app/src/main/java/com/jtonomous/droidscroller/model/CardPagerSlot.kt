package com.jtonomous.droidscroller.model

data class CardPagerSlot(
    val relativePosition: Int,
    val card: Card?
)

fun CardSequence.fixedPagerSlots(): List<CardPagerSlot> {
    return (-2..2).map { relativePosition ->
        CardPagerSlot(
            relativePosition = relativePosition,
            card = cards.getOrNull(focusedIndex + relativePosition)
        )
    }
}
