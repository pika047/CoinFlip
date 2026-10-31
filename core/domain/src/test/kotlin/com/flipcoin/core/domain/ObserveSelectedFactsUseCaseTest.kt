package com.flipcoin.core.domain

import com.flipcoin.core.domain.repository.TopicRepository
import com.flipcoin.core.domain.repository.UserPreferencesRepository
import com.flipcoin.core.model.Fact
import com.flipcoin.core.model.Topic
import com.flipcoin.core.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveSelectedFactsUseCaseTest {
    private val facts = listOf(Fact("space-0", "space", "A"), Fact("ocean-0", "ocean", "B"))
    private val preferences = MutableStateFlow(UserPreferences(setOf("space"), onboardingCompleted = true))

    private val useCase = ObserveSelectedFactsUseCase(
        topicRepository = object : TopicRepository {
            override suspend fun getTopics(): List<Topic> = emptyList()

            override suspend fun getFacts(topicIds: Set<String>): List<Fact> = facts.filter { it.topicId in topicIds }
        },
        userPreferencesRepository = object : UserPreferencesRepository {
            override val userPreferences = preferences

            override suspend fun completeOnboarding(selectedTopicIds: Set<String>) = Unit

            override suspend fun setSelectedTopics(selectedTopicIds: Set<String>) = Unit
        },
    )

    @Test
    fun `emits facts for the selected topics`() = runTest {
        assertEquals(listOf("space-0"), useCase().first().map { it.id })
    }

    @Test
    fun `emits new facts when the selection changes`() = runTest {
        preferences.value = preferences.value.copy(selectedTopicIds = setOf("ocean"))

        assertEquals(listOf("ocean-0"), useCase().first().map { it.id })
    }
}
