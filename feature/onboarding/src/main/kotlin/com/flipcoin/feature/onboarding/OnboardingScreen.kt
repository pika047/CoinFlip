package com.flipcoin.feature.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flipcoin.core.designsystem.component.SelectableChip
import com.flipcoin.core.designsystem.component.SelectableChipGrid
import com.flipcoin.core.designsystem.theme.FlipCoinTheme
import com.flipcoin.core.model.Topic

@Composable
fun OnboardingRoute(modifier: Modifier = Modifier, viewModel: OnboardingViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    OnboardingScreen(
        uiState = uiState,
        onTopicClick = viewModel::toggleTopic,
        onContinue = viewModel::onContinue,
        modifier = modifier,
    )
}

@Composable
internal fun OnboardingScreen(
    uiState: OnboardingUiState,
    onTopicClick: (String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        OnboardingUiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        is OnboardingUiState.Ready -> Column(modifier.fillMaxSize().padding(24.dp)) {
            Text(stringResource(R.string.onboarding_title), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.onboarding_subtitle), style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))
            SelectableChipGrid(
                chips = uiState.topics.map { topic ->
                    SelectableChip(topic.id, "${topic.emoji}  ${topic.name}", topic.id in uiState.selectedTopicIds)
                },
                onChipClick = onTopicClick,
                modifier = Modifier.weight(1f),
            )
            Button(
                onClick = onContinue,
                enabled = uiState.canContinue,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.onboarding_continue))
            }
        }
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    FlipCoinTheme {
        OnboardingScreen(
            uiState = OnboardingUiState.Ready(
                topics = listOf(Topic("space", "Space", "🚀"), Topic("ocean", "Ocean", "🌊")),
                selectedTopicIds = setOf("space"),
            ),
            onTopicClick = {},
            onContinue = {},
        )
    }
}
