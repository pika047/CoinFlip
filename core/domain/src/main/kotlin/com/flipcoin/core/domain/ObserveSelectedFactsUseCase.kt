package com.flipcoin.core.domain

import com.flipcoin.core.domain.repository.TopicRepository
import com.flipcoin.core.domain.repository.UserPreferencesRepository
import com.flipcoin.core.model.Fact
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

/** Emits every fact from the chosen topics, again whenever the selection changes. */
class ObserveSelectedFactsUseCase @Inject constructor(
    private val topicRepository: TopicRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Fact>> = userPreferencesRepository.userPreferences
        .map { it.selectedTopicIds }
        .distinctUntilChanged()
        .mapLatest { topicRepository.getFacts(it) }
}
