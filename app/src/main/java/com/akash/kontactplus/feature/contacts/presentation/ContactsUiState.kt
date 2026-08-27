package com.akash.kontactplus.feature.contacts.presentation

import androidx.annotation.StringRes
import com.akash.kontactplus.feature.contacts.domain.model.Contact

/**
 * Immutable state for the Contacts screen.
 */
data class ContactsUiState(
    val permissionState: ContactsPermissionState = ContactsPermissionState.Checking,
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    val hasLoadedContacts: Boolean = false
)
