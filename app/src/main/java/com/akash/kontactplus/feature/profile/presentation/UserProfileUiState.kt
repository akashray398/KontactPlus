package com.akash.kontactplus.feature.profile.presentation

data class UserProfileUiState(
    val userName: String = "My Profile",
    val phoneNumber: String = "+91 98765 43210",
    val totalContacts: Int = 0,
    val activeFollowUps: Int = 0,
    val memoryFactsCount: Int = 0,
    val insightsGeneratedCount: Int = 0,
    val timeSavedText: String = "4.2 hrs",
    val isVerifiedLocal: Boolean = true,
    val isLoading: Boolean = false
)
