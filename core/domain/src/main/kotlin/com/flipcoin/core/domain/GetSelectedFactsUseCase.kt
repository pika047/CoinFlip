package com.flipcoin.core.domain

import com.flipcoin.core.domain.repository.TopicRepository
import com.flipcoin.core.domain.repository.UserPreferencesRepository
import com.flipcoin.core.model.Fact
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/** Returns every fact from the topics the user picked during onboarding. */
class GetSelectedFactsUseCase @Inject constructor(
    private val topicRepository: TopicRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(): List<Fact> {
        val selected = userPreferencesRepository.userPreferences.first().selectedTopicIds
        return topicRepository.getFacts(selected)
    }
}
