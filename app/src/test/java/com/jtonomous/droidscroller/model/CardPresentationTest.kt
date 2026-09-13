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

    @Test
    fun focusedCardRemainsCenteredAndDominantAtDifferentLayoutSizes() {
        val small = CardLayoutSize(width = 320f, height = 480f)
        val large = CardLayoutSize(width = 800f, height = 1280f)

        assertEquals(CardPresentation(1f, 1f), cardPresentationFor(0, small))
        assertEquals(CardPresentation(1f, 1f), cardPresentationFor(0, large))
        assertEquals(1f, cardPresentationFor(0, small).scale)
        assertEquals(1f, cardPresentationFor(0, large).alpha)
    }

    @Test
    fun largerLayoutsGiveNeighborsMoreResponsiveScaleAndTransparency() {
        val small = CardLayoutSize(width = 320f, height = 480f)
        val large = CardLayoutSize(width = 800f, height = 1280f)

        val smallFirst = cardPresentationFor(1, small)
        val largeFirst = cardPresentationFor(1, large)
        val smallSecond = cardPresentationFor(2, small)
        val largeSecond = cardPresentationFor(2, large)

        assertEquals(smallFirst, cardPresentationFor(-1, small))
        assertEquals(largeFirst, cardPresentationFor(-1, large))
        assertEquals(smallSecond, cardPresentationFor(-2, small))
        assertEquals(largeSecond, cardPresentationFor(-2, large))
        assertEquals(true, largeFirst.scale > smallFirst.scale)
        assertEquals(true, largeFirst.alpha > smallFirst.alpha)
        assertEquals(true, largeSecond.scale > smallSecond.scale)
        assertEquals(true, largeSecond.alpha > smallSecond.alpha)
        assertEquals(true, smallSecond.scale < smallFirst.scale)
        assertEquals(true, smallSecond.alpha < smallFirst.alpha)
    }

    @Test
    fun transformationsAreDeterministicForSameSlotAndLayout() {
        val layout = CardLayoutSize(width = 480f, height = 800f)

        assertEquals(cardPresentationFor(1, layout), cardPresentationFor(1, layout))
        assertEquals(cardPresentationFor(-2, layout), cardPresentationFor(-2, layout))
    }
}
