package com.akash.kontactplus.feature.telecom

import com.akash.kontactplus.core.telecom.ActiveCallInfo

/**
 * Immutable state for the Active Call screen.
 */
data class ActiveCallUiState(
    val callInfo: ActiveCallInfo = ActiveCallInfo(),
    val durationText: String = "00:00",
    val isDtmfVisible: Boolean = false,
    val dtmfDigits: String = "",
    val isAudioRoutePickerVisible: Boolean = false
)
