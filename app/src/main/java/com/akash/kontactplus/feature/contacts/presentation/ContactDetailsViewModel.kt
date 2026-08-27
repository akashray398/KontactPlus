package com.akash.kontactplus.feature.contacts.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.R
import com.akash.kontactplus.feature.contacts.domain.usecase.GetContactUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getContactUseCase: GetContactUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ContactDetailsUiState>(ContactDetailsUiState.Loading)
    val uiState: StateFlow<ContactDetailsUiState> = _uiState.asStateFlow()

    private val lookupKey: String? = savedStateHandle[KEY_LOOKUP_KEY]

    init {
        loadContact()
    }

    fun retry() {
        loadContact()
    }

    private fun loadContact() {
        val key = lookupKey
        if (key.isNullOrBlank()) {
            _uiState.value = ContactDetailsUiState.Error(R.string.contact_details_error_description)
            return
        }

        viewModelScope.launch {
            _uiState.value = ContactDetailsUiState.Loading
            getContactUseCase(key).fold(
                onSuccess = { contact ->
                    _uiState.value = if (contact != null) {
                        ContactDetailsUiState.Success(contact)
                    } else {
                        ContactDetailsUiState.NotFound
                    }
                },
                onFailure = {
                    _uiState.value = ContactDetailsUiState.Error(R.string.contact_details_error_description)
                }
            )
        }
    }

    companion object {
        const val KEY_LOOKUP_KEY = "lookupKey"
    }
}
