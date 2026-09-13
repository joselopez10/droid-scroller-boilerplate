package com.jtonomous.droidscroller.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationSettingsTest {
    @Test
    fun settingsAreClosedByDefault() {
        val settings = NavigationSettings()

        assertFalse(settings.isSettingsOpen)
    }

    @Test
    fun settingsCanOpenAndClose() {
        val settings = NavigationSettings()

        val opened = settings.openSettings()
        val closed = opened.closeSettings()

        assertTrue(opened.isSettingsOpen)
        assertFalse(closed.isSettingsOpen)
    }

}
