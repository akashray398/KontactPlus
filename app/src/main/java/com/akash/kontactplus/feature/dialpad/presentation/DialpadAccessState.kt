package com.akash.kontactplus.feature.dialpad.presentation

/**
 * Represents the access state for the Dialpad.
 */
sealed interface DialpadAccessState {
    data object Checking : DialpadAccessState
    data object RoleUnsupported : DialpadAccessState
    data object RoleRequired : DialpadAccessState
    data object CallPermissionNotRequested : DialpadAccessState
    data object CallPermissionDenied : DialpadAccessState
    data object CallPermissionPermanentlyDenied : DialpadAccessState
    data object Ready : DialpadAccessState
}
