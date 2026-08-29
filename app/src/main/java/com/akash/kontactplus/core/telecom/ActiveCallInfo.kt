package com.akash.kontactplus.core.telecom

/**
 * Domain-level information about an active call.
 */
data class ActiveCallInfo(
    val phoneNumber: String = "",
    val displayName: String = "",
    val state: ActiveCallState = ActiveCallState.NoCall,
    val startTimeMillis: Long = 0,
    val isMuted: Boolean = false,
    val canHold: Boolean = false,
    val canUnhold: Boolean = false,
    val canMute: Boolean = false
)
