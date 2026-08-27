package com.akash.kontactplus.feature.contacts.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.R
import com.akash.kontactplus.feature.contacts.domain.usecase.GetContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getContactsUseCase: GetContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    private var loadContactsJob: Job? = null

    private var hasRequestedPermission: Boolean
        get() = savedStateHandle.get<Boolean>(KEY_HAS_REQUESTED_PERMISSION) ?: false
        set(value) {
            savedStateHandle[KEY_HAS_REQUESTED_PERMISSION] = value
        }

    fun onPermissionStatusChecked(isGranted: Boolean, shouldShowRationale: Boolean) {
        val newState = when {
            isGranted -> ContactsPermissionState.Granted
            !hasRequestedPermission -> ContactsPermissionState.NotRequested
            shouldShowRationale -> ContactsPermissionState.Denied
            else -> ContactsPermissionState.PermanentlyDenied
        }
        _uiState.update { it.copy(permissionState = newState) }
        
        if (isGranted && !_uiState.value.hasLoadedContacts) {
            loadContacts()
        } else if (!isGranted) {
            clearContacts()
        }
    }

    fun onPermissionRequestStarted() {
        hasRequestedPermission = true
    }

    fun onPermissionResultReceived(isGranted: Boolean, shouldShowRationale: Boolean) {
        val newState = when {
            isGranted -> ContactsPermissionState.Granted
            shouldShowRationale -> ContactsPermissionState.Denied
            else -> ContactsPermissionState.PermanentlyDenied
        }
        _uiState.update { it.copy(permissionState = newState) }
        
        if (isGranted) {
            loadContacts()
        } else {
            clearContacts()
        }
    }

    fun retryLoadingContacts() {
        if (_uiState.value.permissionState == ContactsPermissionState.Granted) {
            loadContacts()
        }
    }

    private fun loadContacts() {
        if (_uiState.value.isLoading) return
        
        loadContactsJob?.cancel()
        loadContactsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
            
            getContactsUseCase().fold(
                onSuccess = { contacts ->
                    // Double check permission before publishing data
                    if (_uiState.value.permissionState == ContactsPermissionState.Granted) {
                        _uiState.update { 
                            it.copy(
                                contacts = contacts, 
                                isLoading = false, 
                                hasLoadedContacts = true
                            ) 
                        }
                    }
                },
                onFailure = {
                    if (_uiState.value.permissionState == ContactsPermissionState.Granted) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                errorMessageRes = R.string.contacts_load_error_description 
                            ) 
                        }
                    }
                }
            )
        }
    }

    private fun clearContacts() {
        loadContactsJob?.cancel()
        _uiState.update { 
            it.copy(
                contacts = emptyList(), 
                hasLoadedContacts = false,
                isLoading = false,
                errorMessageRes = null
            ) 
        }
    }

    companion object {
        private const val KEY_HAS_REQUESTED_PERMISSION = "has_requested_permission"
    }
}
