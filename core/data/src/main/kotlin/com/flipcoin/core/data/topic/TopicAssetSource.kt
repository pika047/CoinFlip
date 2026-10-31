package com.flipcoin.core.data.topic

/** Supplies the raw JSON of each bundled topic file. */
fun interface TopicAssetSource {
    fun readTopicFiles(): List<String>
}
