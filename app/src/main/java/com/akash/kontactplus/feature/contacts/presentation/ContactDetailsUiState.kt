package com.akash.kontactplus.feature.contacts.presentation

import androidx.annotation.StringRes
import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.relationship.domain.model.ContactRelationship

/**
 * Immutable state for the Contact Details screen.
 */
sealed interface ContactDetailsUiState {
    data object Loading : ContactDetailsUiState
    data class Success(
        val contact: Contact,
        val isFavourite: Boolean = false,
        val relationship: ContactRelationship? = null,
        val isFavouriteActionInProgress: Boolean = false,
        @StringRes val favouriteActionErrorRes: Int? = null
    ) : ContactDetailsUiState
    data object NotFound : ContactDetailsUiState
    data class Error(@StringRes val messageRes: Int) : ContactDetailsUiState
}
