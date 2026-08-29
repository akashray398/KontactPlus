package com.akash.kontactplus.feature.favourites.presentation

import androidx.annotation.StringRes
import com.akash.kontactplus.feature.contacts.domain.model.Contact

/**
 * Immutable UI state for the Favourites screen.
 */
data class FavouritesUiState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    val hasContactsPermission: Boolean = true
) {
    val isEmpty: Boolean = contacts.isEmpty() && !isLoading && errorMessageRes == null && hasContactsPermission
}
