package com.akash.kontactplus.feature.contacts.presentation

import androidx.annotation.StringRes
import com.akash.kontactplus.core.designsystem.component.SetupItem
import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.model.ContactSortOrder
import com.akash.kontactplus.feature.contacts.domain.usecase.DuplicateContactPair

/**
 * Immutable state for the Contacts screen.
 */
data class ContactsUiState(
    val permissionState: ContactsPermissionState = ContactsPermissionState.Checking,
    val visibleContacts: List<Contact> = emptyList(),
    val duplicatePairs: List<DuplicateContactPair> = emptyList(),
    val searchQuery: String = "",
    val sortOrder: ContactSortOrder = ContactSortOrder.NameAscending,
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    val hasLoadedContacts: Boolean = false,
    val showSetupCard: Boolean = false,
    val setupItems: List<SetupItem> = emptyList()
)
