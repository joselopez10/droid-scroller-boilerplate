package com.jtonomous.droidscroller

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CardScrollerScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun resetPersistedState() {
        composeRule.activity.resetPersistedStateForTesting()
        composeRule.waitForIdle()
    }

    @Test
    fun interestsScreenHasNoNavigationButtonControls() {
        composeRule.onNodeWithText("⚙").assertIsDisplayed()
        composeRule.onAllNodesWithText("Next card").assertCountEquals(0)
        composeRule.onAllNodesWithText("Previous card").assertCountEquals(0)
        composeRule.onAllNodesWithText("← Interest").assertCountEquals(0)
        composeRule.onAllNodesWithText("Interest →").assertCountEquals(0)
    }

    @Test
    fun settingsShowsVersionAndNoNavigationButtonControls() {
        composeRule.onNodeWithText("⚙").performClick()
        composeRule.onNodeWithText("Version").assertIsDisplayed()
        composeRule.onNodeWithText("0.1").assertIsDisplayed()
        composeRule.onAllNodesWithText("Navigation buttons").assertCountEquals(0)
        composeRule.onNodeWithText("Back").performClick()
    }

    @Test
    fun addCardDialogInsertsNewCardAtTopAndFocusesIt() {
        composeRule.onNodeWithText("+").performClick()
        composeRule.onNodeWithText("Add card").assertIsDisplayed()
        composeRule.onNodeWithText("Card name").performTextInput("New favorite")
        composeRule.onNodeWithText("OK").performClick()

        composeRule.onNodeWithText("New favorite").assertIsDisplayed()
        composeRule.onNodeWithText("Card 1 of 4 · Finite").assertIsDisplayed()
    }

}
