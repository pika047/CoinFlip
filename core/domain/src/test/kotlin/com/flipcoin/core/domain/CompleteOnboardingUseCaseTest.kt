package com.flipcoin.core.domain

import com.flipcoin.core.domain.repository.UserPreferencesRepository
import com.flipcoin.core.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CompleteOnboardingUseCaseTest {
    private val repository = FakeUserPreferencesRepository()
    private val completeOnboarding = CompleteOnboardingUseCase(repository)

    @Test
    fun `saves selected topics and completes onboarding`() = runTest {
        completeOnboarding(setOf("space", "ocean"))

        val prefs = repository.userPreferences.value
        assertEquals(setOf("space", "ocean"), prefs.selectedTopicIds)
        assertTrue(prefs.onboardingCompleted)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects an empty selection`() = runTest {
        completeOnboarding(emptySet())
    }
}

private class FakeUserPreferencesRepository : UserPreferencesRepository {
    override val userPreferences: StateFlow<UserPreferences>
        get() = state

    private val state = MutableStateFlow(UserPreferences(emptySet(), onboardingCompleted = false))

    override suspend fun completeOnboarding(selectedTopicIds: Set<String>) {
        state.value = UserPreferences(selectedTopicIds, onboardingCompleted = true)
    }

    override suspend fun setSelectedTopics(selectedTopicIds: Set<String>) {
        state.value = state.value.copy(selectedTopicIds = selectedTopicIds)
    }
}
