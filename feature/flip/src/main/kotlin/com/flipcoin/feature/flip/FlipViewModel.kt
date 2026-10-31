package com.flipcoin.feature.flip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipcoin.core.domain.FactDeck
import com.flipcoin.core.domain.FlipCoinUseCase
import com.flipcoin.core.domain.GetSelectedFactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FlipViewModel @Inject constructor(
    private val flipCoin: FlipCoinUseCase,
    getSelectedFacts: GetSelectedFactsUseCase,
    random: Random,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FlipUiState())
    val uiState: StateFlow<FlipUiState> = _uiState.asStateFlow()

    private val deck: Deferred<FactDeck> = viewModelScope.async { FactDeck(getSelectedFacts(), random) }

    fun onFlip() {
        if (_uiState.value.isFlipping) return
        viewModelScope.launch {
            val deck = deck.await()
            val side = flipCoin()
            val fact = deck.next()
            _uiState.update {
                it.copy(
                    side = side,
                    isFlipping = true,
                    flipCount = it.flipCount + 1,
                    fact = fact,
                    hasFacts = !deck.isEmpty,
                )
            }
        }
    }

    /** Called by the UI when the coin animation has landed. */
    fun onFlipFinished() {
        _uiState.update { it.copy(isFlipping = false) }
    }
}
