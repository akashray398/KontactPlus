package com.akash.kontactplus.feature.recents.presentation

/**
 * Represents the access state for the Recents screen.
 */
sealed interface RecentsAccessState {
    data object CheckingRole : RecentsAccessState
    data object RoleUnsupported : RecentsAccessState
    data object RoleRequired : RecentsAccessState
    data object CheckingPermission : RecentsAccessState
    data object PermissionNotRequested : RecentsAccessState
    data object PermissionDenied : RecentsAccessState
    data object PermissionPermanentlyDenied : RecentsAccessState
    data object Ready : RecentsAccessState
}
