package com.jtonomous.droidscroller.persistence

import com.jtonomous.droidscroller.model.Card
import com.jtonomous.droidscroller.model.CardSequence
import com.jtonomous.droidscroller.model.Interest
import com.jtonomous.droidscroller.model.InterestSequence
import com.jtonomous.droidscroller.model.NavigationMode
import com.jtonomous.droidscroller.model.NavigationSettings
import java.io.StringReader
import java.io.StringWriter
import java.util.Base64
import java.util.Properties

data class PersistedCardScrollerState(
    val interestSequence: InterestSequence,
    val navigationSettings: NavigationSettings,
    val nextCardId: Int
)

interface CardScrollerStateStore {
    fun load(): PersistedCardScrollerState?

    fun save(state: PersistedCardScrollerState)

    fun clear()
}

class PersistenceException(message: String, cause: Throwable? = null) : Exception(message, cause)

object CardScrollerStateCodec {
    private const val CURRENT_VERSION = "1"

    fun encode(state: PersistedCardScrollerState): String {
        val properties = Properties()
        properties["version"] = CURRENT_VERSION
        properties["activeIndex"] = state.interestSequence.activeIndex.toString()
        properties["interestCount"] = state.interestSequence.interests.size.toString()
        properties["settingsOpen"] = state.navigationSettings.isSettingsOpen.toString()
        properties["showNavigationButtons"] = state.navigationSettings.showNavigationButtons.toString()
        properties["nextCardId"] = state.nextCardId.toString()

        state.interestSequence.interests.forEachIndexed { interestIndex, interest ->
            properties["interest.$interestIndex.id"] = encodeText(interest.id)
            properties["interest.$interestIndex.title"] = encodeText(interest.title)
            properties["interest.$interestIndex.focusedIndex"] = interest.cards.focusedIndex.toString()
            properties["interest.$interestIndex.navigationMode"] = interest.cards.navigationMode.name
            properties["interest.$interestIndex.cardCount"] = interest.cards.cards.size.toString()
            interest.cards.cards.forEachIndexed { cardIndex, card ->
                properties["interest.$interestIndex.card.$cardIndex.id"] = encodeText(card.id)
                properties["interest.$interestIndex.card.$cardIndex.title"] = encodeText(card.title)
            }
        }

        return StringWriter().also { writer ->
            properties.store(writer, null)
        }.toString()
    }

    fun decode(serialized: String): PersistedCardScrollerState {
        if (serialized.isBlank()) {
            throw PersistenceException("Persisted state is empty")
        }

        try {
            val properties = Properties()
            properties.load(StringReader(serialized))
            require(properties.getProperty("version") == CURRENT_VERSION) {
                "Unsupported persistence version"
            }

            val interestCount = properties.requiredInt("interestCount")
            require(interestCount >= 0) { "Interest count cannot be negative" }
            val activeIndex = properties.requiredInt("activeIndex")
            require(activeIndex in 0 until interestCount || (interestCount == 0 && activeIndex == 0)) {
                "Active interest index is out of bounds"
            }
            val nextCardId = properties.requiredInt("nextCardId")
            require(nextCardId > 0) { "Next card ID must be positive" }

            val interests = (0 until interestCount).map { interestIndex ->
                val cardCount = properties.requiredInt("interest.$interestIndex.cardCount")
                require(cardCount >= 0) { "Card count cannot be negative" }
                val focusedIndex = properties.requiredInt("interest.$interestIndex.focusedIndex")
                require(focusedIndex in 0 until cardCount || (cardCount == 0 && focusedIndex == 0)) {
                    "Focused card index is out of bounds"
                }
                val navigationMode = NavigationMode.valueOf(
                    properties.required("interest.$interestIndex.navigationMode")
                )
                val cards = (0 until cardCount).map { cardIndex ->
                    Card(
                        id = decodeText(properties.required("interest.$interestIndex.card.$cardIndex.id")),
                        title = decodeText(properties.required("interest.$interestIndex.card.$cardIndex.title"))
                    )
                }
                Interest(
                    id = decodeText(properties.required("interest.$interestIndex.id")),
                    title = decodeText(properties.required("interest.$interestIndex.title")),
                    cards = CardSequence(cards, focusedIndex, navigationMode)
                )
            }

            return PersistedCardScrollerState(
                interestSequence = InterestSequence(interests, activeIndex),
                navigationSettings = NavigationSettings(
                    isSettingsOpen = properties.requiredBoolean("settingsOpen"),
                    showNavigationButtons = properties.requiredBoolean("showNavigationButtons")
                ),
                nextCardId = nextCardId
            )
        } catch (exception: PersistenceException) {
            throw exception
        } catch (exception: Exception) {
            throw PersistenceException("Persisted state is malformed", exception)
        }
    }

    private fun encodeText(value: String): String {
        return Base64.getEncoder().encodeToString(value.toByteArray(Charsets.UTF_8))
    }

    private fun decodeText(value: String): String {
        return try {
            String(Base64.getDecoder().decode(value), Charsets.UTF_8)
        } catch (exception: IllegalArgumentException) {
            throw PersistenceException("Persisted text is not valid", exception)
        }
    }

    private fun Properties.required(key: String): String {
        return getProperty(key) ?: error("Missing property: $key")
    }

    private fun Properties.requiredInt(key: String): Int {
        return required(key).toInt()
    }

    private fun Properties.requiredBoolean(key: String): Boolean {
        return required(key).toBooleanStrict()
    }
}

class InMemoryCardScrollerStateStore(
    initialState: PersistedCardScrollerState? = null
) : CardScrollerStateStore {
    private var state = initialState

    override fun load(): PersistedCardScrollerState? = state

    override fun save(state: PersistedCardScrollerState) {
        this.state = state
    }

    override fun clear() {
        state = null
    }
}
