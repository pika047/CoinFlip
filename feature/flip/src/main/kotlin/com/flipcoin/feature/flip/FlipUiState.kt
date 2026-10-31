package com.flipcoin.feature.flip

import com.flipcoin.core.model.CoinSide
import com.flipcoin.core.model.Fact

data class FlipUiState(
    /** The side the coin shows, or will land on while [isFlipping]. */
    val side: CoinSide = CoinSide.HEADS,
    val isFlipping: Boolean = false,
    /** Increases on every flip so the UI can start a new animation. */
    val flipCount: Int = 0,
    /** The fact for the latest flip, shown once the coin lands. */
    val fact: Fact? = null,
    val hasFacts: Boolean = true,
) {
    val showResult: Boolean get() = flipCount > 0 && !isFlipping
}
