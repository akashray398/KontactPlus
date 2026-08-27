package com.akash.kontactplus.feature.contacts.presentation

import androidx.annotation.StringRes
import com.akash.kontactplus.feature.contacts.domain.model.Contact

/**
 * Immutable state for the Contact Details screen.
 */
sealed interface ContactDetailsUiState {
    data object Loading : ContactDetailsUiState
    data class Success(val contact: Contact) : ContactDetailsUiState
    data object NotFound : ContactDetailsUiState
    data class Error(@StringRes val messageRes: Int) : ContactDetailsUiState
}
