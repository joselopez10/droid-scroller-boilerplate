package com.jtonomous.droidscroller.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.jtonomous.droidscroller.model.CardSequence
import com.jtonomous.droidscroller.model.DefaultInterestFixture
import com.jtonomous.droidscroller.model.InterestSequence

class CardScrollerViewModel : ViewModel() {
    private val _interestSequence = mutableStateOf(DefaultInterestFixture.create())

    val interestSequence: State<InterestSequence> = _interestSequence
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
}
