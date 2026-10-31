package com.flipcoin.core.model

data class UserPreferences(
    val selectedTopicIds: Set<String>,
    val onboardingCompleted: Boolean,
)
