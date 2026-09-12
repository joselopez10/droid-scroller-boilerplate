package com.jtonomous.droidscroller.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CardPresentationTest {
    @Test
    fun focusedCardIsLargestAndOpaque() {
        assertEquals(CardPresentation(1f, 1f), cardPresentationFor(0))
    }

    @Test
    fun firstNeighborsAreSmallerAndTranslucent() {
        assertEquals(CardPresentation(0.9f, 0.5f), cardPresentationFor(1))
        assertEquals(CardPresentation(0.9f, 0.5f), cardPresentationFor(-1))
    }

    @Test
    fun secondNeighborsAreSmallerAndMoreTranslucent() {
        assertEquals(CardPresentation(0.8f, 0.25f), cardPresentationFor(2))
        assertEquals(CardPresentation(0.8f, 0.25f), cardPresentationFor(-2))
    }
}
