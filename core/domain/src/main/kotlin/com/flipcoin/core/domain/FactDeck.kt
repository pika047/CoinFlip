package com.flipcoin.core.domain

import com.flipcoin.core.model.Fact
import kotlin.random.Random

/**
 * Deals facts in a shuffled order without repeats until every fact has been
 * shown once, then reshuffles. The first fact of a new round is never the
 * same as the last fact of the previous round.
 */
class FactDeck(
    private val facts: List<Fact>,
    private val random: Random,
) {
    private val remaining = ArrayDeque<Fact>()
    private var last: Fact? = null

    val isEmpty: Boolean get() = facts.isEmpty()

    fun next(): Fact? {
        if (facts.isEmpty()) return null
        if (remaining.isEmpty()) refill()
        return remaining.removeFirst().also { last = it }
    }

    private fun refill() {
        val shuffled = facts.shuffled(random).toMutableList()
        if (shuffled.size > 1 && shuffled.first() == last) {
            shuffled.add(shuffled.removeAt(0))
        }
        remaining.addAll(shuffled)
    }
}
