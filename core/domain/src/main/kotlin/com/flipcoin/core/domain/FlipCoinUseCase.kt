package com.flipcoin.core.domain

import com.flipcoin.core.model.CoinSide
import javax.inject.Inject

class FlipCoinUseCase @Inject constructor(
    private val random: RandomSource,
) {
    operator fun invoke(): CoinSide = if (random.nextBoolean()) CoinSide.HEADS else CoinSide.TAILS
}
