package com.jtonomous.droidscroller.persistence

import com.jtonomous.droidscroller.model.Card
import com.jtonomous.droidscroller.model.CardSequence
import com.jtonomous.droidscroller.model.Interest
import com.jtonomous.droidscroller.model.InterestSequence
import com.jtonomous.droidscroller.model.NavigationMode
import com.jtonomous.droidscroller.model.NavigationSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class CardScrollerStateStoreTest {
    @Test
    fun inMemoryStoreRoundTripsCompleteUserFacingState() {
        val original = PersistedCardScrollerState(
            interestSequence = InterestSequence(
                interests = listOf(
                    Interest(
                        id = "music",
                        title = "Music",
                        cards = CardSequence(
                            cards = listOf(
                                Card("music-1", "Jazz"),
                                Card("music-2", "Classical")
                            ),
                            focusedIndex = 1,
                            navigationMode = NavigationMode.LOOP
                        )
                    ),
                    Interest(
                        id = "books",
                        title = "Books",
                        cards = CardSequence(
                            cards = listOf(Card("books-1", "History")),
                            focusedIndex = 0
                        )
                    )
                ),
                activeIndex = 0
            ),
            navigationSettings = NavigationSettings(isSettingsOpen = true),
            nextCardId = 42
        )
        val store = InMemoryCardScrollerStateStore()

        store.save(CardScrollerStateCodec.decode(CardScrollerStateCodec.encode(original)))

        assertEquals(original, store.load())
    }

    @Test
    fun legacyNavigationButtonPropertyIsIgnoredWhenDecoding() {
        val state = PersistedCardScrollerState(
            interestSequence = InterestSequence(emptyList(), 0),
            navigationSettings = NavigationSettings(),
            nextCardId = 1
        )
        val legacy = CardScrollerStateCodec.encode(state) + "showNavigationButtons=true\n"

        assertEquals(state, CardScrollerStateCodec.decode(legacy))
    }

    @Test
    fun emptyStoreLoadsAsEmptyState() {
        assertNull(InMemoryCardScrollerStateStore().load())
    }

    @Test
    fun malformedStateIsRejected() {
        val exception = assertThrows(PersistenceException::class.java) {
            CardScrollerStateCodec.decode("version=1\ninterestCount=1\nactiveIndex=0")
        }

        assertEquals("Persisted state is malformed", exception.message)
    }

    @Test
    fun emptyStateTextIsRejected() {
        assertThrows(PersistenceException::class.java) {
            CardScrollerStateCodec.decode("")
        }
    }
}
