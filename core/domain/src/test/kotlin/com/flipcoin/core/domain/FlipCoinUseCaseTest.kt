package com.flipcoin.core.domain

import com.flipcoin.core.model.CoinSide
import org.junit.Assert.assertEquals
import org.junit.Test

class FlipCoinUseCaseTest {
    @Test
    fun `returns heads when random source is true`() {
        val flip = FlipCoinUseCase { true }
        assertEquals(CoinSide.HEADS, flip())
    }

    @Test
    fun `returns tails when random source is false`() {
        val flip = FlipCoinUseCase { false }
        assertEquals(CoinSide.TAILS, flip())
    }
}
