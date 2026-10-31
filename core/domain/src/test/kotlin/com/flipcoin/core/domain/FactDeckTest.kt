package com.flipcoin.core.domain

import com.flipcoin.core.model.Fact
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.random.Random

class FactDeckTest {
    private val facts = (1..5).map { Fact(id = "f$it", topicId = "t", text = "Fact $it") }

    @Test
    fun `shows every fact once before repeating`() {
        val deck = FactDeck(facts, Random(42))

        val firstRound = List(facts.size) { deck.next() }

        assertEquals(facts.toSet(), firstRound.toSet())
    }

    @Test
    fun `never repeats the same fact twice in a row across rounds`() {
        repeat(50) { seed ->
            val deck = FactDeck(facts, Random(seed))
            var previous = deck.next()
            repeat(facts.size * 4) {
                val current = deck.next()
                assertNotEquals(previous, current)
                previous = current
            }
        }
    }

    @Test
    fun `returns null when there are no facts`() {
        assertNull(FactDeck(emptyList(), Random(1)).next())
    }

    @Test
    fun `a single fact is returned every time`() {
        val deck = FactDeck(facts.take(1), Random(1))
        assertEquals(facts[0], deck.next())
        assertEquals(facts[0], deck.next())
    }
}
