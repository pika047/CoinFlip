package com.flipcoin.feature.flip

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flipcoin.core.designsystem.theme.FlipCoinTheme
import com.flipcoin.core.model.CoinSide
import com.flipcoin.core.model.Fact

@Composable
fun FlipRoute(modifier: Modifier = Modifier, viewModel: FlipViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FlipScreen(
        uiState = uiState,
        onFlip = viewModel::onFlip,
        onFlipFinished = viewModel::onFlipFinished,
        modifier = modifier,
    )
}

@Composable
internal fun FlipScreen(
    uiState: FlipUiState,
    onFlip: () -> Unit,
    onFlipFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.flip_count, uiState.flipCount),
            style = MaterialTheme.typography.labelLarge,
        )

        Coin(
            side = uiState.side,
            flipCount = uiState.flipCount,
            onFlipFinished = onFlipFinished,
            modifier = Modifier.padding(top = 96.dp),
        )

        AnimatedContent(
            targetState = uiState.showResult,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "result",
            modifier = Modifier.fillMaxWidth().heightIn(min = 180.dp),
        ) { showResult ->
            if (showResult) {
                ResultCard(uiState.side, uiState.fact, uiState.hasFacts)
            } else {
                Text(
                    text = if (uiState.flipCount == 0) stringResource(R.string.flip_prompt) else "",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Button(
            onClick = onFlip,
            enabled = !uiState.isFlipping,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            Text(stringResource(R.string.flip_button), style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun ResultCard(side: CoinSide, fact: Fact?, hasFacts: Boolean) {
    val title = if (side == CoinSide.HEADS) R.string.flip_result_heads else R.string.flip_result_tails
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth()) {
            Text(
                text = fact?.text ?: if (hasFacts) "" else stringResource(R.string.flip_no_facts),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(20.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FlipScreenPreview() {
    FlipCoinTheme {
        FlipScreen(
            uiState = FlipUiState(
                side = CoinSide.TAILS,
                flipCount = 3,
                fact = Fact("space-0", "space", "A day on Venus is longer than its year."),
            ),
            onFlip = {},
            onFlipFinished = {},
        )
    }
}
