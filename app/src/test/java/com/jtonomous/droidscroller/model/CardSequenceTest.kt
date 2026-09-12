package com.jtonomous.droidscroller.model

import org.junit.Test
import org.junit.Assert.*

class CardSequenceTest {
    @Test
    fun emptySequenceHasNoCards() {
        val sequence = CardSequence(emptyList(), 0)
        assertTrue(sequence.cards.isEmpty())
    }

    @Test
    fun focusedCardReturnsCurrentCard() {
        val cards = listOf(
            Card("1", "Card 1"),
            Card("2", "Card 2"),
            Card("3", "Card 3")
        )
        val sequence = CardSequence(cards, focusedIndex = 1)
        assertEquals("2", sequence.focusedCard?.id)
    }

    @Test
    fun focusedIndexOutOfBoundsReturnsNull() {
        val cards = listOf(Card("1", "Card 1"))
        val sequence = CardSequence(cards, focusedIndex = 5)
        assertNull(sequence.focusedCard)
    }

    @Test
    fun neighboringCardBeforeReturnsCorrectCard() {
        val cards = listOf(
            Card("1", "Card 1"),
            Card("2", "Card 2"),
            Card("3", "Card 3")
        )
        val sequence = CardSequence(cards, focusedIndex = 1)
        assertEquals("1", sequence.neighborBefore?.id)
    }

    @Test
    fun neighboringCardAfterReturnsCorrectCard() {
        val cards = listOf(
            Card("1", "Card 1"),
            Card("2", "Card 2"),
            Card("3", "Card 3")
        )
        val sequence = CardSequence(cards, focusedIndex = 1)
        assertEquals("3", sequence.neighborAfter?.id)
    }

    @Test
    fun firstCardHasNoNeighborBefore() {
        val cards = listOf(
            Card("1", "Card 1"),
            Card("2", "Card 2")
        )
        val sequence = CardSequence(cards, focusedIndex = 0)
        assertNull(sequence.neighborBefore)
        assertEquals("2", sequence.neighborAfter?.id)
    }

    @Test
    fun lastCardHasNoNeighborAfter() {
        val cards = listOf(
            Card("1", "Card 1"),
            Card("2", "Card 2")
        )
        val sequence = CardSequence(cards, focusedIndex = 1)
        assertEquals("1", sequence.neighborBefore?.id)
        assertNull(sequence.neighborAfter)
    }

    @Test
    fun singleCardHasNoNeighbors() {
        val cards = listOf(Card("1", "Card 1"))
        val sequence = CardSequence(cards, focusedIndex = 0)
        assertNull(sequence.neighborBefore)
        assertNull(sequence.neighborAfter)
    }

    @Test
    fun moveForwardAdvancesFocus() {
        val cards = listOf(
            Card("1", "Card 1"),
            Card("2", "Card 2"),
            Card("3", "Card 3")
        )
        val sequence = CardSequence(cards, focusedIndex = 0)
        val moved = sequence.moveForward()
        assertEquals(1, moved.focusedIndex)
        assertEquals("2", moved.focusedCard?.id)
    }

    @Test
    fun moveBackwardRetractsFocus() {
        val cards = listOf(
            Card("1", "Card 1"),
            Card("2", "Card 2"),
            Card("3", "Card 3")
        )
        val sequence = CardSequence(cards, focusedIndex = 2)
        val moved = sequence.moveBackward()
        assertEquals(1, moved.focusedIndex)
        assertEquals("2", moved.focusedCard?.id)
    }

    @Test
    fun moveForwardFromLastCardStaysAtLast() {
        val cards = listOf(
            Card("1", "Card 1"),
            Card("2", "Card 2")
        )
        val sequence = CardSequence(cards, focusedIndex = 1)
        val moved = sequence.moveForward()
        assertEquals(1, moved.focusedIndex)
        assertEquals("2", moved.focusedCard?.id)
    }

    @Test
    fun moveBackwardFromFirstCardStaysAtFirst() {
        val cards = listOf(
            Card("1", "Card 1"),
            Card("2", "Card 2")
        )
        val sequence = CardSequence(cards, focusedIndex = 0)
        val moved = sequence.moveBackward()
        assertEquals(0, moved.focusedIndex)
        assertEquals("1", moved.focusedCard?.id)
    }

    @Test
    fun movePreservesCardList() {
        val cards = listOf(
            Card("1", "Card 1"),
            Card("2", "Card 2"),
            Card("3", "Card 3")
        )
        val sequence = CardSequence(cards, focusedIndex = 0)
        val moved = sequence.moveForward()
        assertEquals(cards, moved.cards)
    }
}
