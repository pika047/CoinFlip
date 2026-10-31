package com.flipcoin.feature.onboarding

import com.flipcoin.core.model.Topic

sealed interface OnboardingUiState {
    data object Loading : OnboardingUiState

    data class Ready(
        val topics: List<Topic>,
        val selectedTopicIds: Set<String>,
    ) : OnboardingUiState {
        val canContinue: Boolean get() = selectedTopicIds.isNotEmpty()
    }
}
