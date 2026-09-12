package com.jtonomous.droidscroller.model

object DefaultInterestFixture {
    fun create(): InterestSequence {
        return InterestSequence(
            interests = listOf(
                interest(
                    id = "board-games",
                    title = "Board games",
                    cards = listOf("Texas Hold'em", "Blackjack", "Exploding Kittens")
                ),
                interest(
                    id = "running",
                    title = "Running",
                    cards = listOf("Park", "Trail", "Marathon")
                ),
                interest(
                    id = "photography",
                    title = "Photography",
                    cards = listOf("City", "National Park", "Gallery")
                )
            ),
            activeIndex = 1
        )
    }

    private fun interest(id: String, title: String, cards: List<String>): Interest {
        return Interest(
            id = id,
            title = title,
            cards = CardSequence(
                cards = cards.mapIndexed { index, cardTitle ->
                    Card("$id-${index + 1}", cardTitle)
                },
                focusedIndex = 1
            )
        )
    }
}
