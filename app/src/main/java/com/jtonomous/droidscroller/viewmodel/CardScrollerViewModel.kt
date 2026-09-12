package com.jtonomous.droidscroller.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.jtonomous.droidscroller.model.Card
import com.jtonomous.droidscroller.model.CardSequence
import com.jtonomous.droidscroller.model.DefaultInterestFixture
import com.jtonomous.droidscroller.model.InterestSequence
import com.jtonomous.droidscroller.model.NavigationSettings

class CardScrollerViewModel : ViewModel() {
    private val _interestSequence = mutableStateOf(DefaultInterestFixture.create())
    private val _navigationSettings = mutableStateOf(NavigationSettings())
    private var nextCardId = 1

    val interestSequence: State<InterestSequence> = _interestSequence
    val navigationSettings: State<NavigationSettings> = _navigationSettings
    val cardSequence: CardSequence?
        get() = _interestSequence.value.activeInterest?.cards

    fun moveForward() {
        _interestSequence.value = _interestSequence.value.copy(
            interests = _interestSequence.value.interests.mapIndexed { index, interest ->
                if (index == _interestSequence.value.activeIndex) {
                    interest.copy(cards = interest.cards.moveForward())
                } else {
                    interest
                }
            }
        )
    }

    fun moveBackward() {
        _interestSequence.value = _interestSequence.value.copy(
            interests = _interestSequence.value.interests.mapIndexed { index, interest ->
                if (index == _interestSequence.value.activeIndex) {
                    interest.copy(cards = interest.cards.moveBackward())
                } else {
                    interest
                }
            }
        )
    }

    fun moveToNextInterest() {
        _interestSequence.value = _interestSequence.value.moveForward()
    }

    fun moveToPreviousInterest() {
        _interestSequence.value = _interestSequence.value.moveBackward()
    }

    fun addCard(title: String) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) return

        val card = Card(
            id = "user-card-${nextCardId++}",
            title = trimmedTitle
        )
        _interestSequence.value = _interestSequence.value.copy(
            interests = _interestSequence.value.interests.mapIndexed { index, interest ->
                if (index == _interestSequence.value.activeIndex) {
                    interest.copy(cards = interest.cards.insertAtTop(card))
                } else {
                    interest
                }
            }
        )
    }

    fun openSettings() {
        _navigationSettings.value = _navigationSettings.value.openSettings()
    }

    fun closeSettings() {
        _navigationSettings.value = _navigationSettings.value.closeSettings()
    }

    fun toggleNavigationButtons() {
        _navigationSettings.value = _navigationSettings.value.toggleNavigationButtons()
    }
}
