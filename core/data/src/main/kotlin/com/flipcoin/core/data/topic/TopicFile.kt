package com.flipcoin.core.data.topic

import kotlinx.serialization.Serializable

/** JSON shape of one file in `assets/topics`. */
@Serializable
internal data class TopicFile(
    val id: String,
    val name: String,
    val emoji: String,
    val facts: List<String>,
)
