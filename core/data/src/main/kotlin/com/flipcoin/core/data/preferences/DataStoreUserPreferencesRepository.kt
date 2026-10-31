package com.flipcoin.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.flipcoin.core.domain.repository.UserPreferencesRepository
import com.flipcoin.core.model.UserPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DataStoreUserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {
    override val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            selectedTopicIds = prefs[SELECTED_TOPICS].orEmpty(),
            onboardingCompleted = prefs[ONBOARDING_COMPLETED] ?: false,
        )
    }

    override suspend fun completeOnboarding(selectedTopicIds: Set<String>) {
        dataStore.edit { prefs ->
            prefs[SELECTED_TOPICS] = selectedTopicIds
            prefs[ONBOARDING_COMPLETED] = true
        }
    }

    override suspend fun setSelectedTopics(selectedTopicIds: Set<String>) {
        dataStore.edit { prefs -> prefs[SELECTED_TOPICS] = selectedTopicIds }
    }

    private companion object {
        val SELECTED_TOPICS = stringSetPreferencesKey("selected_topic_ids")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}
