package com.akash.kontactplus.core.telecom

/**
 * Domain-level information about an active call.
 */
data class ActiveCallInfo(
    val callId: String = "",
    val phoneNumber: String = "",
    val displayName: String = "",
    val state: ActiveCallState = ActiveCallState.NoCall,
    val direction: CallDirection = CallDirection.Unknown,
    val startTimeMillis: Long = 0,
    val isMuted: Boolean = false,
    val currentEndpoint: CallAudioEndpoint = CallAudioEndpoint.Unknown,
    val availableEndpoints: List<CallAudioEndpoint> = emptyList(),
    val canHold: Boolean = false,
    val canUnhold: Boolean = false,
    val canMute: Boolean = false,
    val canDtmf: Boolean = false,
    val disconnectReason: CallDisconnectReason = CallDisconnectReason.Other
)
