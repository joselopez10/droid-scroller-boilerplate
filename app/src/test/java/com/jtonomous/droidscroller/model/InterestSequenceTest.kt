package com.jtonomous.droidscroller.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InterestSequenceTest {
    @Test
    fun emptySequenceHasNoActiveInterest() {
        val sequence = InterestSequence(emptyList(), activeIndex = 0)

        assertTrue(sequence.interests.isEmpty())
        assertNull(sequence.activeInterest)
    }

    @Test
    fun singleInterestStaysActiveAtBothBoundaries() {
        val sequence = InterestSequence(listOf(interest("one")), activeIndex = 0)

        assertEquals(0, sequence.moveForward().activeIndex)
        assertEquals(0, sequence.moveBackward().activeIndex)
    }

    @Test
    fun movingForwardSelectsNextInterest() {
        val sequence = InterestSequence(
            interests = listOf(interest("one"), interest("two"), interest("three")),
            activeIndex = 0
        )

        val moved = sequence.moveForward()

        assertEquals(1, moved.activeIndex)
        assertEquals("two", moved.activeInterest?.id)
    }

    @Test
    fun movingBackwardSelectsPreviousInterest() {
        val sequence = InterestSequence(
            interests = listOf(interest("one"), interest("two"), interest("three")),
            activeIndex = 2
        )

        val moved = sequence.moveBackward()

        assertEquals(1, moved.activeIndex)
        assertEquals("two", moved.activeInterest?.id)
    }

    @Test
    fun repeatedBoundaryMovesDoNotCreateInvalidActiveIndex() {
        val sequence = InterestSequence(
            interests = listOf(interest("one"), interest("two")),
            activeIndex = 0
        )

        val atFirst = sequence.moveBackward().moveBackward()
        val atLast = sequence.moveForward().moveForward().moveForward()

        assertEquals(0, atFirst.activeIndex)
        assertEquals(1, atLast.activeIndex)
        assertEquals("two", atLast.activeInterest?.id)
    }

    @Test
    fun switchingInterestsPreservesEachInterestFocusedCard() {
        val first = interest(
            id = "one",
            focusedIndex = 1
        )
        val second = interest(
            id = "two",
            focusedIndex = 0
        )
        val sequence = InterestSequence(listOf(first, second), activeIndex = 0)

        val switchedAway = sequence.moveForward()
        val switchedBack = switchedAway.moveBackward()

        assertEquals(0, switchedBack.activeIndex)
        assertEquals(1, switchedBack.activeInterest?.cards?.focusedIndex)
        assertEquals(0, switchedAway.activeInterest?.cards?.focusedIndex)
    }

    private fun interest(
        id: String,
        focusedIndex: Int = 0
    ): Interest {
        return Interest(
            id = id,
            title = id.replaceFirstChar { it.uppercase() },
            cards = CardSequence(
                cards = listOf(
                    Card("$id-1", "$id card 1"),
                    Card("$id-2", "$id card 2")
                ),
                focusedIndex = focusedIndex
            )
        )
    }
}
