package com.akash.kontactplus.feature.contacts.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.R
import com.akash.kontactplus.feature.contacts.domain.usecase.GetContactUseCase
import com.akash.kontactplus.feature.favourites.domain.usecase.IsContactFavouriteUseCase
import com.akash.kontactplus.feature.favourites.domain.usecase.ToggleFavouriteContactUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getContactUseCase: GetContactUseCase,
    private val isContactFavouriteUseCase: IsContactFavouriteUseCase,
    private val toggleFavouriteContactUseCase: ToggleFavouriteContactUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ContactDetailsUiState>(ContactDetailsUiState.Loading)
    val uiState: StateFlow<ContactDetailsUiState> = _uiState.asStateFlow()

    private val lookupKey: String? = savedStateHandle[KEY_LOOKUP_KEY]

    init {
        loadContact()
        observeFavouriteStatus()
    }

    fun retry() {
        loadContact()
    }

    fun onFavouriteClick() {
        val currentState = _uiState.value
        if (currentState is ContactDetailsUiState.Success && !currentState.isFavouriteActionInProgress) {
            val key = currentState.contact.lookupKey
            val isFavourite = currentState.isFavourite
            
            viewModelScope.launch {
                _uiState.update { 
                    if (it is ContactDetailsUiState.Success) it.copy(isFavouriteActionInProgress = true) else it 
                }
                
                toggleFavouriteContactUseCase(key, isFavourite).fold(
                    onSuccess = {
                        // Room observation will update the state
                    },
                    onFailure = {
                        _uiState.update { 
                            if (it is ContactDetailsUiState.Success) {
                                it.copy(
                                    isFavouriteActionInProgress = false,
                                    favouriteActionErrorRes = R.string.favourite_update_error
                                )
                            } else it 
                        }
                    }
                )
            }
        }
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
                        ContactDetailsUiState.Success(contact = contact)
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

    private fun observeFavouriteStatus() {
        val key = lookupKey ?: return
        viewModelScope.launch {
            isContactFavouriteUseCase(key).collectLatest { isFavourite ->
                _uiState.update { state ->
                    if (state is ContactDetailsUiState.Success) {
                        state.copy(
                            isFavourite = isFavourite,
                            isFavouriteActionInProgress = false
                        )
                    } else state
                }
            }
        }
    }

    companion object {
        const val KEY_LOOKUP_KEY = "lookupKey"
    }
}
