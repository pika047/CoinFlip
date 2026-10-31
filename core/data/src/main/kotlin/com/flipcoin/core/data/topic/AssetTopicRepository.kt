package com.flipcoin.core.data.topic

import com.flipcoin.core.domain.repository.TopicRepository
import com.flipcoin.core.model.Fact
import com.flipcoin.core.model.Topic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AssetTopicRepository @Inject constructor(
    private val assetSource: TopicAssetSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : TopicRepository {
    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()
    private var cache: List<TopicFile>? = null

    override suspend fun getTopics(): List<Topic> = loadTopicFiles().map { Topic(it.id, it.name, it.emoji) }

    override suspend fun getFacts(topicIds: Set<String>): List<Fact> = loadTopicFiles()
        .filter { it.id in topicIds }
        .flatMap { topic ->
            topic.facts.mapIndexed { index, text -> Fact(id = "${topic.id}-$index", topicId = topic.id, text = text) }
        }

    private suspend fun loadTopicFiles(): List<TopicFile> = mutex.withLock {
        cache ?: withContext(ioDispatcher) {
            assetSource.readTopicFiles().map { json.decodeFromString<TopicFile>(it) }
        }.also { cache = it }
    }
}
