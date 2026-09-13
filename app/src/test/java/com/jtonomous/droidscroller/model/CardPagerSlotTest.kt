package com.jtonomous.droidscroller.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CardPagerSlotTest {
    @Test
    fun fixedSlotsKeepFocusedCardInCenter() {
        val sequence = CardSequence(
            cards = (1..5).map { Card("$it", "Card $it") },
            focusedIndex = 2
        )

        val slots = sequence.fixedPagerSlots()

        assertEquals(listOf(-2, -1, 0, 1, 2), slots.map { it.relativePosition })
        assertEquals("3", slots[2].card?.id)
    }

    @Test
    fun fixedSlotsLeaveMissingNeighborsEmpty() {
        val sequence = CardSequence(
            cards = listOf(Card("1", "Card 1"), Card("2", "Card 2")),
            focusedIndex = 1
        )

        val slots = sequence.fixedPagerSlots()

        assertNull(slots[0].card)
        assertEquals("1", slots[1].card?.id)
        assertEquals("2", slots[2].card?.id)
        assertNull(slots[3].card)
        assertNull(slots[4].card)
    }

    @Test
    fun emptySequenceStillHasFiveEmptySlots() {
        val slots = CardSequence(emptyList(), 0).fixedPagerSlots()

        assertEquals(5, slots.size)
        assertEquals(5, slots.count { it.card == null })
    }

    @Test
    fun largerSequenceShowsOnlyNearestTwoNeighborsAroundFocus() {
        val sequence = CardSequence(
            cards = (1..7).map { Card("$it", "Card $it") },
            focusedIndex = 3
        )

        val slots = sequence.fixedPagerSlots()

        assertEquals(listOf("2", "3", "4", "5", "6"), slots.map { it.card?.id })
        assertEquals("4", slots[2].card?.id)
    }

    @Test
    fun focusRemainsInCenterWhenSequenceHasFewerThanFiveCards() {
        val sequence = CardSequence(
            cards = listOf(
                Card("1", "Card 1"),
                Card("2", "Card 2"),
                Card("3", "Card 3")
            ),
            focusedIndex = 1
        )

        val slots = sequence.fixedPagerSlots()

        assertEquals(listOf(null, "1", "2", "3", null), slots.map { it.card?.id })
        assertEquals("2", slots[2].card?.id)
    }
}
