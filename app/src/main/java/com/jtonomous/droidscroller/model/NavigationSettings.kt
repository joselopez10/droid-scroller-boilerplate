package com.jtonomous.droidscroller.model

data class NavigationSettings(
    val isSettingsOpen: Boolean = false,
    val showNavigationButtons: Boolean = false
) {
    fun openSettings(): NavigationSettings = copy(isSettingsOpen = true)

    fun closeSettings(): NavigationSettings = copy(isSettingsOpen = false)

    fun toggleNavigationButtons(): NavigationSettings {
        return copy(showNavigationButtons = !showNavigationButtons)
    }
}
