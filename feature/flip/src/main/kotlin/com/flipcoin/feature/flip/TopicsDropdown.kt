package com.flipcoin.feature.flip

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

/**
 * A "Topics ▾" button that opens a dropdown of checkable topics. Changes are kept as a draft
 * in [state] and only applied when the user taps Save.
 */
@Composable
internal fun TopicsDropdown(
    state: TopicPickerState?,
    enabled: Boolean,
    onOpen: () -> Unit,
    onTopicClick: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        OutlinedButton(onClick = onOpen, enabled = enabled) {
            Text(stringResource(R.string.flip_topics_button) + "  ▾")
        }
        DropdownMenu(expanded = state != null, onDismissRequest = onDismiss) {
            state?.topics?.forEach { topic ->
                val checked = topic.id in state.selectedTopicIds
                DropdownMenuItem(
                    text = { Text("${topic.emoji}  ${topic.name}") },
                    leadingIcon = { Checkbox(checked = checked, onCheckedChange = null) },
                    onClick = { onTopicClick(topic.id) },
                )
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.flip_topics_save),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                enabled = state?.canSave == true,
                onClick = onSave,
            )
        }
    }
}
