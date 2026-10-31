package com.flipcoin.feature.flip

import com.flipcoin.core.model.CoinSide
import com.flipcoin.core.model.Fact
import com.flipcoin.core.model.Topic

data class FlipUiState(
    /** The side the coin shows, or will land on while [isFlipping]. */
    val side: CoinSide = CoinSide.HEADS,
    val isFlipping: Boolean = false,
    /** Increases on every flip so the UI can start a new animation. */
    val flipCount: Int = 0,
    /** The fact for the latest flip, shown once the coin lands. */
    val fact: Fact? = null,
    val hasFacts: Boolean = true,
    /** Non-null while the topic picker is open. */
    val topicPicker: TopicPickerState? = null,
) {
    val showResult: Boolean get() = flipCount > 0 && !isFlipping
}

/** Topics the user can toggle, and the selection they have not saved yet. */
data class TopicPickerState(
    val topics: List<Topic>,
    val selectedTopicIds: Set<String>,
) {
    val canSave: Boolean get() = selectedTopicIds.isNotEmpty()
}
