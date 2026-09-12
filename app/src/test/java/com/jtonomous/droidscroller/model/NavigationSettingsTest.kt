package com.jtonomous.droidscroller.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationSettingsTest {
    @Test
    fun navigationButtonsAreHiddenByDefault() {
        val settings = NavigationSettings()

        assertFalse(settings.isSettingsOpen)
        assertFalse(settings.showNavigationButtons)
    }

    @Test
    fun settingsCanOpenAndClose() {
        val settings = NavigationSettings()

        val opened = settings.openSettings()
        val closed = opened.closeSettings()

        assertTrue(opened.isSettingsOpen)
        assertFalse(closed.isSettingsOpen)
    }

    @Test
    fun navigationButtonsCanBeToggled() {
        val settings = NavigationSettings()

        val shown = settings.toggleNavigationButtons()
        val hidden = shown.toggleNavigationButtons()

        assertTrue(shown.showNavigationButtons)
        assertFalse(hidden.showNavigationButtons)
    }
}
