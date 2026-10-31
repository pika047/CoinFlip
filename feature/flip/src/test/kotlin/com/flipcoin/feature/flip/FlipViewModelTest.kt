package com.flipcoin.feature.flip

import com.flipcoin.core.domain.FlipCoinUseCase
import com.flipcoin.core.domain.GetSelectedFactsUseCase
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlipViewModelTest {
    private val facts = listOf(Fact("space-0", "space", "A"), Fact("space-1", "space", "B"))

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(heads: Boolean = true, facts: List<Fact> = this.facts) = FlipViewModel(
        flipCoin = FlipCoinUseCase { heads },
        getSelectedFacts = GetSelectedFactsUseCase(FakeTopicRepository(facts), FakeUserPreferencesRepository()),
        random = Random(7),
    )

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

        vm.onFlip()

        assertFalse(vm.uiState.value.hasFacts)
    }
}

private class FakeTopicRepository(
    private val facts: List<Fact>,
) : TopicRepository {
    override suspend fun getTopics(): List<Topic> = emptyList()

    override suspend fun getFacts(topicIds: Set<String>): List<Fact> = facts.filter { it.topicId in topicIds }
}

private class FakeUserPreferencesRepository : UserPreferencesRepository {
    override val userPreferences = MutableStateFlow(UserPreferences(setOf("space"), onboardingCompleted = true))

    override suspend fun completeOnboarding(selectedTopicIds: Set<String>) = Unit
}
