package com.jtonomous.droidscroller.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.jtonomous.droidscroller.model.Card
import com.jtonomous.droidscroller.model.CardSequence

class CardScrollerViewModel : ViewModel() {
    private val _cardSequence = mutableStateOf(
        CardSequence(
            cards = listOf(
                Card("1", "First Card"),
                Card("2", "Second Card"),
                Card("3", "Third Card"),
                Card("4", "Fourth Card"),
                Card("5", "Fifth Card")
            ),
            focusedIndex = 0
        )
    )

    val cardSequence: State<CardSequence> = _cardSequence

    fun moveForward() {
        _cardSequence.value = _cardSequence.value.moveForward()
    }

    fun moveBackward() {
        _cardSequence.value = _cardSequence.value.moveBackward()
    }
}
