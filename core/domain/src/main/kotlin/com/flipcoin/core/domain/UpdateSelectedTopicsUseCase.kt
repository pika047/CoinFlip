package com.flipcoin.core.domain

import com.flipcoin.core.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UpdateSelectedTopicsUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(selectedTopicIds: Set<String>) {
        require(selectedTopicIds.size >= CompleteOnboardingUseCase.MIN_TOPICS) {
            "Pick at least ${CompleteOnboardingUseCase.MIN_TOPICS} topic"
        }
        userPreferencesRepository.setSelectedTopics(selectedTopicIds)
    }
}
