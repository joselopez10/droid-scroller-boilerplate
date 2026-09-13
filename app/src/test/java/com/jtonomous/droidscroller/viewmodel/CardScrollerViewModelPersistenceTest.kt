package com.jtonomous.droidscroller.viewmodel

import com.jtonomous.droidscroller.persistence.InMemoryCardScrollerStateStore
import com.jtonomous.droidscroller.persistence.CardScrollerStateStore
import com.jtonomous.droidscroller.persistence.PersistenceException
import com.jtonomous.droidscroller.persistence.PersistedCardScrollerState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CardScrollerViewModelPersistenceTest {
    @Test
    fun restoredViewModelKeepsActiveInterestFocusedCardAndSettingsState() {
        val store = InMemoryCardScrollerStateStore()
        val firstViewModel = CardScrollerViewModel(store)

        firstViewModel.moveToNextInterest()
        firstViewModel.moveForward()
        firstViewModel.openSettings()

        val restoredViewModel = CardScrollerViewModel(store)

        assertEquals(2, restoredViewModel.interestSequence.value.activeIndex)
        assertEquals(
            2,
            restoredViewModel.interestSequence.value.activeInterest?.cards?.focusedIndex
        )
        assertEquals(true, restoredViewModel.navigationSettings.value.isSettingsOpen)
        assertNull(restoredViewModel.persistenceError.value)
    }

    @Test
    fun userCardIdSequenceContinuesAfterRestoration() {
        val store = InMemoryCardScrollerStateStore()
        CardScrollerViewModel(store).addCard("First")

        val restoredViewModel = CardScrollerViewModel(store)
        restoredViewModel.addCard("Second")

        assertEquals(
            listOf("user-card-2", "user-card-1"),
            restoredViewModel.cardSequence?.cards?.take(2)?.map { it.id }
        )
    }

    @Test
    fun deletingFocusedCardPersistsTheUpdatedSequence() {
        val store = InMemoryCardScrollerStateStore()
        val viewModel = CardScrollerViewModel(store)

        viewModel.deleteFocusedCard()
        val restoredViewModel = CardScrollerViewModel(store)

        assertEquals(2, restoredViewModel.cardSequence?.cards?.size)
        assertEquals(1, restoredViewModel.cardSequence?.focusedIndex)
    }

    @Test
    fun persistenceFailuresAreExposedToThePresentationLayer() {
        val viewModel = CardScrollerViewModel(
            object : CardScrollerStateStore {
                override fun load() = throw PersistenceException("storage unavailable")

                override fun save(state: PersistedCardScrollerState) {
                    throw PersistenceException("storage unavailable")
                }

                override fun clear() = Unit
            }
        )

        assertEquals("storage unavailable", viewModel.persistenceError.value)
    }
}
