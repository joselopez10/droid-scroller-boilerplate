package com.jtonomous.droidscroller.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jtonomous.droidscroller.model.Card
import com.jtonomous.droidscroller.model.CardSequence
import com.jtonomous.droidscroller.model.DefaultInterestFixture
import com.jtonomous.droidscroller.model.InterestSequence
import com.jtonomous.droidscroller.model.NavigationSettings
import com.jtonomous.droidscroller.persistence.CardScrollerStateStore
import com.jtonomous.droidscroller.persistence.InMemoryCardScrollerStateStore
import com.jtonomous.droidscroller.persistence.PersistedCardScrollerState

class CardScrollerViewModel(
    private val stateStore: CardScrollerStateStore = InMemoryCardScrollerStateStore()
) : ViewModel() {
    private val _interestSequence = mutableStateOf(DefaultInterestFixture.create())
    private val _navigationSettings = mutableStateOf(NavigationSettings())
    private val _persistenceError = mutableStateOf<String?>(null)
    private var nextCardId = 1

    val interestSequence: State<InterestSequence> = _interestSequence
    val navigationSettings: State<NavigationSettings> = _navigationSettings
    val persistenceError: State<String?> = _persistenceError
    val cardSequence: CardSequence?
        get() = _interestSequence.value.activeInterest?.cards

    init {
        restore()
    }

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
        persist()
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
        persist()
    }

    fun moveToNextInterest() {
        _interestSequence.value = _interestSequence.value.moveForward()
        persist()
    }

    fun moveToPreviousInterest() {
        _interestSequence.value = _interestSequence.value.moveBackward()
        persist()
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
        persist()
    }

    fun deleteFocusedCard() {
        _interestSequence.value = _interestSequence.value.copy(
            interests = _interestSequence.value.interests.mapIndexed { index, interest ->
                if (index == _interestSequence.value.activeIndex) {
                    interest.copy(cards = interest.cards.deleteFocused())
                } else {
                    interest
                }
            }
        )
        persist()
    }

    fun openSettings() {
        _navigationSettings.value = _navigationSettings.value.openSettings()
        persist()
    }

    fun closeSettings() {
        _navigationSettings.value = _navigationSettings.value.closeSettings()
        persist()
    }

    fun persistNow() {
        persist()
    }

    fun resetToDefaultsForTesting() {
        stateStore.clear()
        _interestSequence.value = DefaultInterestFixture.create()
        _navigationSettings.value = NavigationSettings()
        nextCardId = 1
        _persistenceError.value = null
    }

    private fun restore() {
        try {
            stateStore.load()?.let { savedState ->
                _interestSequence.value = savedState.interestSequence
                _navigationSettings.value = savedState.navigationSettings
                nextCardId = savedState.nextCardId
            }
        } catch (exception: Exception) {
            reportPersistenceError(exception)
        }
    }

    private fun persist() {
        try {
            stateStore.save(
                PersistedCardScrollerState(
                    interestSequence = _interestSequence.value,
                    navigationSettings = _navigationSettings.value,
                    nextCardId = nextCardId
                )
            )
            _persistenceError.value = null
        } catch (exception: Exception) {
            reportPersistenceError(exception)
        }
    }

    private fun reportPersistenceError(exception: Exception) {
        _persistenceError.value = exception.message ?: "Unknown persistence error"
    }

    class Factory(
        private val stateStore: CardScrollerStateStore
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(CardScrollerViewModel::class.java))
            return CardScrollerViewModel(stateStore) as T
        }
    }
}
