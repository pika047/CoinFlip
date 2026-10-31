package com.flipcoin.feature.flip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipcoin.core.domain.FactDeck
import com.flipcoin.core.domain.FlipCoinUseCase
import com.flipcoin.core.domain.ObserveSelectedFactsUseCase
import com.flipcoin.core.domain.UpdateSelectedTopicsUseCase
import com.flipcoin.core.domain.repository.TopicRepository
import com.flipcoin.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FlipViewModel @Inject constructor(
    private val flipCoin: FlipCoinUseCase,
    observeSelectedFacts: ObserveSelectedFactsUseCase,
    private val topicRepository: TopicRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val updateSelectedTopics: UpdateSelectedTopicsUseCase,
    random: Random,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FlipUiState())
    val uiState: StateFlow<FlipUiState> = _uiState.asStateFlow()

    /** Rebuilt whenever the chosen topics change, so new facts apply from the next flip. */
    private var deck: FactDeck? = null

    init {
        viewModelScope.launch {
            observeSelectedFacts().collect { facts ->
                deck = FactDeck(facts, random)
                _uiState.update { it.copy(hasFacts = facts.isNotEmpty()) }
            }
        }
    }

    fun onFlip() {
        val deck = deck ?: return
        if (_uiState.value.isFlipping) return
        val side = flipCoin()
        val fact = deck.next()
        _uiState.update {
            it.copy(side = side, isFlipping = true, flipCount = it.flipCount + 1, fact = fact)
        }
    }

    /** Called by the UI when the coin animation has landed. */
    fun onFlipFinished() {
        _uiState.update { it.copy(isFlipping = false) }
    }

    fun onOpenTopics() {
        viewModelScope.launch {
            val topics = topicRepository.getTopics()
            val selected = userPreferencesRepository.userPreferences.first().selectedTopicIds
            _uiState.update { it.copy(topicPicker = TopicPickerState(topics, selected)) }
        }
    }

    fun onToggleTopic(topicId: String) {
        _uiState.update { state ->
            val picker = state.topicPicker ?: return@update state
            val selected = picker.selectedTopicIds
            val updated = if (topicId in selected) selected - topicId else selected + topicId
            state.copy(topicPicker = picker.copy(selectedTopicIds = updated))
        }
    }

    fun onSaveTopics() {
        val picker = _uiState.value.topicPicker ?: return
        if (!picker.canSave) return
        _uiState.update { it.copy(topicPicker = null) }
        viewModelScope.launch { updateSelectedTopics(picker.selectedTopicIds) }
    }

    fun onDismissTopics() {
        _uiState.update { it.copy(topicPicker = null) }
    }
}
