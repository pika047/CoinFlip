package com.flipcoin.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipcoin.core.domain.CompleteOnboardingUseCase
import com.flipcoin.core.domain.repository.TopicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val completeOnboarding: CompleteOnboardingUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Loading)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = OnboardingUiState.Ready(topicRepository.getTopics(), emptySet())
        }
    }

    fun toggleTopic(topicId: String) {
        _uiState.update { state ->
            if (state !is OnboardingUiState.Ready) return@update state
            val selected = state.selectedTopicIds
            state.copy(selectedTopicIds = if (topicId in selected) selected - topicId else selected + topicId)
        }
    }

    fun onContinue() {
        val state = _uiState.value as? OnboardingUiState.Ready ?: return
        if (!state.canContinue) return
        viewModelScope.launch { completeOnboarding(state.selectedTopicIds) }
    }
}
