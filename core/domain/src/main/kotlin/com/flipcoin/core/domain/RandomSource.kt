package com.flipcoin.core.domain

/**
 * Abstraction over randomness so that flip results can be made
 * deterministic in tests.
 */
fun interface RandomSource {
    fun nextBoolean(): Boolean
}
