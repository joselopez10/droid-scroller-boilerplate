package com.jtonomous.droidscroller.model

import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultInterestFixtureTest {
    @Test
    fun startsOnRunningWithEachInterestFocusedOnItsSecondCard() {
        val fixture = DefaultInterestFixture.create()

        assertEquals(
            listOf("Board games", "Running", "Photography"),
            fixture.interests.map { it.title }
        )
        assertEquals(1, fixture.activeIndex)
        assertEquals("Running", fixture.activeInterest?.title)
        assertEquals(listOf(1, 1, 1), fixture.interests.map { it.cards.focusedIndex })
    }

    @Test
    fun containsRequestedCardsInOrder() {
        val fixture = DefaultInterestFixture.create()

        assertEquals(
            listOf("Texas Hold'em", "Blackjack", "Exploding Kittens"),
            fixture.interests[0].cards.cards.map { it.title }
        )
        assertEquals(
            listOf("Park", "Trail", "Marathon"),
            fixture.interests[1].cards.cards.map { it.title }
        )
        assertEquals(
            listOf("City", "National Park", "Gallery"),
            fixture.interests[2].cards.cards.map { it.title }
        )
    }
}
