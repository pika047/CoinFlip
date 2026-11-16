package com.flipcoin.core.domain.repository

import com.flipcoin.core.model.Fact
import com.flipcoin.core.model.Topic

/** Read-only access to the topics and facts that ship with the app. */
interface TopicRepository {
    suspend fun getTopics(): List<Topic>

    suspend fun getFacts(topicIds: Set<String>): List<Fact>
}
