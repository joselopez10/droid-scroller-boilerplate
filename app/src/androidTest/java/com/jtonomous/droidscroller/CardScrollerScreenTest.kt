package com.jtonomous.droidscroller

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
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

    @Test
    fun longPressDeleteCanBeCancelledAndConfirmed() {
        composeRule.onNodeWithText("Blackjack").performTouchInput {
            down(center)
            advanceEventTime(700)
            up()
        }
        composeRule.onNodeWithText("Card actions").assertIsDisplayed()
        composeRule.onNodeWithText("Delete").performClick()
        composeRule.onNodeWithText("Delete card?").assertIsDisplayed()
        composeRule.onNodeWithText("No").performClick()
        composeRule.onNodeWithText("Card actions").assertIsDisplayed()
        composeRule.onNodeWithText("Cancel").performClick()

        composeRule.onNodeWithText("Blackjack").performTouchInput {
            down(center)
            advanceEventTime(700)
            up()
        }
        composeRule.onNodeWithText("Delete").performClick()
        composeRule.onNodeWithText("Yes").performClick()

        composeRule.onNodeWithText("Card 1 of 2 · Finite").assertIsDisplayed()
    }

}
