package com.akash.kontactplus.feature.dialpad.presentation

import androidx.annotation.StringRes
import com.akash.kontactplus.feature.dialpad.domain.model.DialpadSuggestion

/**
 * Immutable state for the Dialpad screen.
 */
data class DialpadUiState(
    val dialableNumber: String = "",
    val formattedDisplayNumber: String = "",
    val suggestions: List<DialpadSuggestion> = emptyList(),
    val accessState: DialpadAccessState = DialpadAccessState.Checking,
    val isLoadingSuggestions: Boolean = false,
    @StringRes val inputErrorRes: Int? = null,
    @StringRes val callErrorRes: Int? = null,
    val isPlacingCall: Boolean = false
)
