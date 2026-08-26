package com.akash.kontactplus.feature.contacts.presentation

data class ContactsUiState(
    val permissionState: ContactsPermissionState = ContactsPermissionState.Checking,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
