package com.flipcoin.core.domain.repository

import com.flipcoin.core.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>

    /** Saves the chosen topics and marks onboarding as done. */
    suspend fun completeOnboarding(selectedTopicIds: Set<String>)
}
