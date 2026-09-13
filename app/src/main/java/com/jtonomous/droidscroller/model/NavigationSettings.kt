package com.jtonomous.droidscroller.model

data class NavigationSettings(
    val isSettingsOpen: Boolean = false
) {

    fun openSettings(): NavigationSettings = copy(isSettingsOpen = true)

    fun closeSettings(): NavigationSettings = copy(isSettingsOpen = false)
}
