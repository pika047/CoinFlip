package com.flipcoin.feature.flip

import com.flipcoin.core.domain.FlipCoinUseCase
import com.flipcoin.core.domain.ObserveSelectedFactsUseCase
import com.flipcoin.core.domain.UpdateSelectedTopicsUseCase
import com.flipcoin.core.domain.repository.TopicRepository
import com.flipcoin.core.domain.repository.UserPreferencesRepository
import com.flipcoin.core.model.CoinSide
import com.flipcoin.core.model.Fact
import com.flipcoin.core.model.Topic
import com.flipcoin.core.model.UserPreferences
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlipViewModelTest {
    private val topics = listOf(Topic("space", "Space", "🚀"), Topic("ocean", "Ocean", "🌊"))
    private val facts = listOf(
        Fact("space-0", "space", "A"),
        Fact("space-1", "space", "B"),
        Fact("ocean-0", "ocean", "C"),
    )
    private val preferences = FakeUserPreferencesRepository(setOf("space"))

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(heads: Boolean = true, facts: List<Fact> = this.facts): FlipViewModel {
        val topicRepository = FakeTopicRepository(topics, facts)
        return FlipViewModel(
            flipCoin = FlipCoinUseCase { heads },
            observeSelectedFacts = ObserveSelectedFactsUseCase(topicRepository, preferences),
            topicRepository = topicRepository,
            userPreferencesRepository = preferences,
            updateSelectedTopics = UpdateSelectedTopicsUseCase(preferences),
            random = Random(7),
        )
    }

    @Test
    fun `flip starts the animation with a side and a fact`() {
        val vm = viewModel(heads = false)

        vm.onFlip()

        val state = vm.uiState.value
        assertEquals(CoinSide.TAILS, state.side)
        assertTrue(state.isFlipping)
        assertEquals(1, state.flipCount)
        assertNotNull(state.fact)
        assertFalse(state.showResult)
    }

    @Test
    fun `result shows once the coin lands`() {
        val vm = viewModel()

        vm.onFlip()
        vm.onFlipFinished()

        assertTrue(vm.uiState.value.showResult)
    }

    @Test
    fun `flipping again while the coin is in the air is ignored`() {
        val vm = viewModel()

        vm.onFlip()
        vm.onFlip()

        assertEquals(1, vm.uiState.value.flipCount)
    }

    @Test
    fun `consecutive flips show different facts`() {
        val vm = viewModel()

        vm.onFlip()
        vm.onFlipFinished()
        val first = vm.uiState.value.fact
        vm.onFlip()
        vm.onFlipFinished()

        assertTrue(first != vm.uiState.value.fact)
    }

    @Test
    fun `reports when there are no facts`() {
        val vm = viewModel(facts = emptyList())

        assertFalse(vm.uiState.value.hasFacts)
    }

    @Test
    fun `opening topics shows every topic with the current selection`() {
        val vm = viewModel()

        vm.onOpenTopics()

        assertEquals(TopicPickerState(topics, setOf("space")), vm.uiState.value.topicPicker)
    }

    @Test
    fun `saving a new selection updates preferences and later facts`() {
        val vm = viewModel()

        vm.onOpenTopics()
        vm.onToggleTopic("space")
        vm.onToggleTopic("ocean")
        vm.onSaveTopics()
        vm.onFlip()

        assertEquals(setOf("ocean"), preferences.state.value.selectedTopicIds)
        assertNull(vm.uiState.value.topicPicker)
        assertEquals("ocean", vm.uiState.value.fact?.topicId)
    }

    @Test
    fun `an empty selection cannot be saved`() {
        val vm = viewModel()

        vm.onOpenTopics()
        vm.onToggleTopic("space")
        vm.onSaveTopics()

        assertFalse(vm.uiState.value.topicPicker!!.canSave)
        assertEquals(setOf("space"), preferences.state.value.selectedTopicIds)
    }

    @Test
    fun `dismissing the picker keeps the old selection`() {
        val vm = viewModel()

        vm.onOpenTopics()
        vm.onToggleTopic("ocean")
        vm.onDismissTopics()

        assertNull(vm.uiState.value.topicPicker)
        assertEquals(setOf("space"), preferences.state.value.selectedTopicIds)
    }
}

private class FakeTopicRepository(
    private val topics: List<Topic>,
    private val facts: List<Fact>,
) : TopicRepository {
    override suspend fun getTopics(): List<Topic> = topics

    override suspend fun getFacts(topicIds: Set<String>): List<Fact> = facts.filter { it.topicId in topicIds }
}

private class FakeUserPreferencesRepository(
    selected: Set<String>,
) : UserPreferencesRepository {
    val state = MutableStateFlow(UserPreferences(selected, onboardingCompleted = true))
    override val userPreferences = state

    override suspend fun completeOnboarding(selectedTopicIds: Set<String>) = Unit

    override suspend fun setSelectedTopics(selectedTopicIds: Set<String>) {
        state.value = state.value.copy(selectedTopicIds = selectedTopicIds)
    }
}
