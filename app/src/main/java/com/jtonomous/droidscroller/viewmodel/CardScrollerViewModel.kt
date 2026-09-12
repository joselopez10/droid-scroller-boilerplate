package com.jtonomous.droidscroller.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.jtonomous.droidscroller.model.Card
import com.jtonomous.droidscroller.model.CardSequence
import com.jtonomous.droidscroller.model.Interest
import com.jtonomous.droidscroller.model.InterestSequence

class CardScrollerViewModel : ViewModel() {
    private val _interestSequence = mutableStateOf(
        InterestSequence(
            interests = listOf(
                Interest(
                    id = "reading",
                    title = "Reading",
                    cards = CardSequence(
                        cards = listOf(
                            Card("reading-1", "First Book"),
                            Card("reading-2", "Second Book"),
                            Card("reading-3", "Third Book")
                        ),
                        focusedIndex = 0
                    )
                ),
                Interest(
                    id = "ideas",
                    title = "Ideas",
                    cards = CardSequence(
                        cards = listOf(
                            Card("ideas-1", "First Idea"),
                            Card("ideas-2", "Second Idea"),
                            Card("ideas-3", "Third Idea")
                        ),
                        focusedIndex = 0
                    )
                ),
                Interest(
                    id = "places",
                    title = "Places",
                    cards = CardSequence(
                        cards = listOf(
                            Card("places-1", "First Place"),
                            Card("places-2", "Second Place"),
                            Card("places-3", "Third Place")
                        ),
                        focusedIndex = 0
                    )
                )
            ),
            activeIndex = 0
        )
    )

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
