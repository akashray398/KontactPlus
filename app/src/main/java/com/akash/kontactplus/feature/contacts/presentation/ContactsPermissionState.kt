package com.akash.kontactplus.feature.contacts.presentation

sealed interface ContactsPermissionState {
    data object Checking : ContactsPermissionState
    data object NotRequested : ContactsPermissionState
    data object Granted : ContactsPermissionState
    data object Denied : ContactsPermissionState
    data object PermanentlyDenied : ContactsPermissionState
}
