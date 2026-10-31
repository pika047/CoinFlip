package com.flipcoin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipcoin.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = userPreferencesRepository.userPreferences
        .map { prefs ->
            if (prefs.onboardingCompleted) {
                MainActivityUiState.Home(prefs.selectedTopicIds)
            } else {
                MainActivityUiState.Onboarding
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainActivityUiState.Loading)
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data object Onboarding : MainActivityUiState

    data class Home(
        val selectedTopicIds: Set<String>,
    ) : MainActivityUiState
}
