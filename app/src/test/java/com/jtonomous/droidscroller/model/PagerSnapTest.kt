package com.jtonomous.droidscroller.model

import org.junit.Assert.assertEquals
import org.junit.Test

class PagerSnapTest {
    @Test
    fun shortDragReturnsToCurrentPage() {
        assertEquals(0, snapPageDelta(10f, 100f, currentIndex = 1, pageCount = 3))
    }

    @Test
    fun upwardDragAdvancesOnePage() {
        assertEquals(1, snapPageDelta(-60f, 100f, currentIndex = 1, pageCount = 3))
    }

    @Test
    fun downwardDragMovesBackOnePage() {
        assertEquals(-1, snapPageDelta(60f, 100f, currentIndex = 1, pageCount = 3))
    }

    @Test
    fun boundariesClampWithoutWrapping() {
        assertEquals(0, snapPageDelta(-60f, 100f, currentIndex = 2, pageCount = 3))
        assertEquals(0, snapPageDelta(60f, 100f, currentIndex = 0, pageCount = 3))
    }

    @Test
    fun emptyPagesNeverMove() {
        assertEquals(0, snapPageDelta(-100f, 100f, currentIndex = 0, pageCount = 0))
    }

    @Test
    fun defaultThresholdAllowsShorterPagerGestures() {
        assertEquals(1, snapPageDelta(-25f, 100f, currentIndex = 0, pageCount = 3))
    }
}
