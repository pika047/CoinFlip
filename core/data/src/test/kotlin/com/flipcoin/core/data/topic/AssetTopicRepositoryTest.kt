package com.flipcoin.core.data.topic

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AssetTopicRepositoryTest {
    private val files = listOf(
        """{"id":"space","name":"Space","emoji":"🚀","facts":["Fact A","Fact B"]}""",
        """{"id":"ocean","name":"Ocean","emoji":"🌊","facts":["Fact C"]}""",
    )

    @Test
    fun `parses topics from asset files`() = runTest {
        val repository = AssetTopicRepository({ files }, StandardTestDispatcher(testScheduler))

        assertEquals(listOf("space", "ocean"), repository.getTopics().map { it.id })
    }

    @Test
    fun `returns only facts for the requested topics with stable ids`() = runTest {
        val repository = AssetTopicRepository({ files }, StandardTestDispatcher(testScheduler))

        val facts = repository.getFacts(setOf("space"))

        assertEquals(listOf("space-0", "space-1"), facts.map { it.id })
        assertEquals(listOf("Fact A", "Fact B"), facts.map { it.text })
    }

    @Test
    fun `reads asset files only once`() = runTest {
        var reads = 0
        val repository = AssetTopicRepository(
            {
                reads++
                files
            },
            StandardTestDispatcher(testScheduler),
        )

        repository.getTopics()
        repository.getFacts(setOf("ocean"))

        assertEquals(1, reads)
    }
}
