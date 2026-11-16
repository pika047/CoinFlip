package com.flipcoin.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipcoin.core.designsystem.theme.FlipCoinTheme

/** App root. Onboarding and flip screens are wired in here in later steps. */
@Composable
fun FlipCoinApp() {
    Scaffold { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "FlipCoin",
                style = MaterialTheme.typography.displayMedium,
            )
        }
    }
}

@Preview
@Composable
private fun FlipCoinAppPreview() {
    FlipCoinTheme { FlipCoinApp() }
}
