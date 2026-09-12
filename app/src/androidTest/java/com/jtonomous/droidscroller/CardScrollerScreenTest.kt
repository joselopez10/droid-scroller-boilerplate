package com.jtonomous.droidscroller

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class CardScrollerScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigationButtonsAreHiddenByDefault() {
        composeRule.onNodeWithText("⚙").assertIsDisplayed()
        composeRule.onAllNodesWithText("Next card").assertCountEquals(0)
        composeRule.onAllNodesWithText("Previous card").assertCountEquals(0)
        composeRule.onAllNodesWithText("← Interest").assertCountEquals(0)
        composeRule.onAllNodesWithText("Interest →").assertCountEquals(0)
    }

    @Test
    fun settingsCanShowAndHideNavigationButtons() {
        composeRule.onNodeWithText("⚙").performClick()
        composeRule.onNodeWithText("Navigation buttons").assertIsDisplayed()
        composeRule.onNodeWithText("Off").performClick()
        composeRule.onNodeWithText("On").assertIsDisplayed()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Next card").assertIsDisplayed()
        composeRule.onNodeWithText("Previous card").assertIsDisplayed()
        composeRule.onNodeWithText("← Interest").assertIsDisplayed()
        composeRule.onNodeWithText("Interest →").assertIsDisplayed()
    }

    @Test
    fun cardButtonLabelsUseRequestedActions() {
        composeRule.onNodeWithText("⚙").performClick()
        composeRule.onNodeWithText("Off").performClick()
        composeRule.onNodeWithText("Back").performClick()

        composeRule.onNodeWithText("Next card").performClick()
        composeRule.onNodeWithText("Card 1 of 3 · Finite").assertIsDisplayed()

        composeRule.onNodeWithText("Previous card").performClick()
        composeRule.onNodeWithText("Card 2 of 3 · Finite").assertIsDisplayed()
    }
}
