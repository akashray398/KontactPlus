package com.akash.kontactplus.core.telecom

/**
 * Domain-level representation of an active call state.
 */
enum class ActiveCallState {
    NoCall,
    Incoming,
    Dialling,
    Connecting,
    Active,
    OnHold,
    Disconnecting,
    Disconnected
}
