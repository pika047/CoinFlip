package com.flipcoin.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipcoin.MainActivityUiState
import com.flipcoin.core.designsystem.theme.FlipCoinTheme
import com.flipcoin.feature.flip.FlipRoute
import com.flipcoin.feature.onboarding.OnboardingRoute

/** App root: shows onboarding until the user has picked topics. */
@Composable
fun FlipCoinApp(uiState: MainActivityUiState) {
    Scaffold { padding ->
        val modifier = Modifier.fillMaxSize().padding(padding)
        when (uiState) {
            MainActivityUiState.Loading -> Box(modifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            MainActivityUiState.Onboarding -> OnboardingRoute(modifier)

            is MainActivityUiState.Home -> FlipRoute(modifier)
        }
    }
}

@Preview
@Composable
private fun FlipCoinAppPreview() {
    FlipCoinTheme { FlipCoinApp(MainActivityUiState.Loading) }
}
