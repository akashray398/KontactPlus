package com.akash.kontactplus.feature.onboarding.presentation

data class OnboardingUiState(
    val currentPage: Int = 0,
    val isCompleted: Boolean = false,
    val isLoading: Boolean = true
)
