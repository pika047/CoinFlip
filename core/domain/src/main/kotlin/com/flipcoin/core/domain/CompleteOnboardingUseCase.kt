package com.flipcoin.core.domain

import com.flipcoin.core.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(selectedTopicIds: Set<String>) {
        require(selectedTopicIds.size >= MIN_TOPICS) { "Pick at least $MIN_TOPICS topic" }
        userPreferencesRepository.completeOnboarding(selectedTopicIds)
    }

    companion object {
        const val MIN_TOPICS = 1
    }
}
