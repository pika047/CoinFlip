package com.flipcoin.feature.onboarding

import com.flipcoin.core.domain.CompleteOnboardingUseCase
import com.flipcoin.core.domain.repository.TopicRepository
import com.flipcoin.core.domain.repository.UserPreferencesRepository
import com.flipcoin.core.model.Fact
import com.flipcoin.core.model.Topic
import com.flipcoin.core.model.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private val topics = listOf(Topic("space", "Space", "🚀"), Topic("ocean", "Ocean", "🌊"))
    private val preferences = FakeUserPreferencesRepository()
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = OnboardingViewModel(FakeTopicRepository(topics), CompleteOnboardingUseCase(preferences))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads topics with nothing selected`() {
        val state = viewModel.uiState.value as OnboardingUiState.Ready
        assertEquals(topics, state.topics)
        assertTrue(state.selectedTopicIds.isEmpty())
        assertFalse(state.canContinue)
    }

    @Test
    fun `toggling a topic selects and then deselects it`() {
        viewModel.toggleTopic("space")
        assertEquals(setOf("space"), (viewModel.uiState.value as OnboardingUiState.Ready).selectedTopicIds)

        viewModel.toggleTopic("space")
        assertTrue((viewModel.uiState.value as OnboardingUiState.Ready).selectedTopicIds.isEmpty())
    }

    @Test
    fun `continue saves the selection`() = runTest {
        viewModel.toggleTopic("ocean")
        viewModel.onContinue()

        assertEquals(UserPreferences(setOf("ocean"), onboardingCompleted = true), preferences.state.value)
    }

    @Test
    fun `continue does nothing without a selection`() = runTest {
        viewModel.onContinue()

        assertFalse(preferences.state.value.onboardingCompleted)
    }
}

private class FakeTopicRepository(
    private val topics: List<Topic>,
) : TopicRepository {
    override suspend fun getTopics(): List<Topic> = topics

    override suspend fun getFacts(topicIds: Set<String>): List<Fact> = emptyList()
}

private class FakeUserPreferencesRepository : UserPreferencesRepository {
    val state = MutableStateFlow(UserPreferences(emptySet(), onboardingCompleted = false))
    override val userPreferences = state

    override suspend fun completeOnboarding(selectedTopicIds: Set<String>) {
        state.value = UserPreferences(selectedTopicIds, onboardingCompleted = true)
    }

    override suspend fun setSelectedTopics(selectedTopicIds: Set<String>) {
        state.value = state.value.copy(selectedTopicIds = selectedTopicIds)
    }
}
