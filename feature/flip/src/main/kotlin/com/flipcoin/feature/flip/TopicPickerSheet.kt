package com.flipcoin.feature.flip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipcoin.core.designsystem.component.SelectableChip
import com.flipcoin.core.designsystem.component.SelectableChipGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopicPickerSheet(
    state: TopicPickerState,
    onTopicClick: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
            Text(stringResource(R.string.flip_topics_title), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            SelectableChipGrid(
                chips = state.topics.map { topic ->
                    SelectableChip(topic.id, "${topic.emoji}  ${topic.name}", topic.id in state.selectedTopicIds)
                },
                onChipClick = onTopicClick,
                modifier = Modifier.heightIn(max = 360.dp),
            )
            Button(onClick = onSave, enabled = state.canSave, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.flip_topics_save))
            }
        }
    }
}
