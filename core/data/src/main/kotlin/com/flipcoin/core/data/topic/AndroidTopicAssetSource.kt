package com.flipcoin.core.data.topic

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** Reads every `assets/topics/*.json` file, so a new topic only needs a new file. */
internal class AndroidTopicAssetSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : TopicAssetSource {
    override fun readTopicFiles(): List<String> {
        val files = context.assets.list(TOPICS_DIR).orEmpty().filter { it.endsWith(".json") }.sorted()
        return files.map { name ->
            context.assets.open("$TOPICS_DIR/$name").bufferedReader().use { it.readText() }
        }
    }

    private companion object {
        const val TOPICS_DIR = "topics"
    }
}
