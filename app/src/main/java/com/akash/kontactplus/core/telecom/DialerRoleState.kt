package com.akash.kontactplus.core.telecom

/**
 * Represents the current state of the Dialer role.
 */
sealed interface DialerRoleState {
    data object Checking : DialerRoleState
    data object Unsupported : DialerRoleState
    data object NotHeld : DialerRoleState
    data object Held : DialerRoleState
}
