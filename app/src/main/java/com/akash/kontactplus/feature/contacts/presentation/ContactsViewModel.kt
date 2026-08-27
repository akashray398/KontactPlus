package com.akash.kontactplus.feature.contacts.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.R
import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.model.ContactSortOrder
import com.akash.kontactplus.feature.contacts.domain.usecase.FilterContactsUseCase
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
    private val getContactsUseCase: GetContactsUseCase,
    private val filterContactsUseCase: FilterContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    private var allContacts: List<Contact> = emptyList()
    private var loadContactsJob: Job? = null

    init {
        // Restore search query and sort order from saved state if needed
        val savedQuery = savedStateHandle.get<String>(KEY_SEARCH_QUERY) ?: ""
        val savedSortOrder = savedStateHandle.get<ContactSortOrder>(KEY_SORT_ORDER) ?: ContactSortOrder.NameAscending
        _uiState.update { it.copy(searchQuery = savedQuery, sortOrder = savedSortOrder) }
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

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        savedStateHandle[KEY_SEARCH_QUERY] = query
        applyFilter()
    }

    fun onClearSearch() {
        onSearchQueryChanged("")
    }

    fun onSortOrderChanged(sortOrder: ContactSortOrder) {
        _uiState.update { it.copy(sortOrder = sortOrder) }
        savedStateHandle[KEY_SORT_ORDER] = sortOrder
        applyFilter()
    }

    private fun loadContacts() {
        if (_uiState.value.isLoading) return
        
        loadContactsJob?.cancel()
        loadContactsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
            
            getContactsUseCase().fold(
                onSuccess = { contacts ->
                    if (_uiState.value.permissionState == ContactsPermissionState.Granted) {
                        allContacts = contacts
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                hasLoadedContacts = true
                            ) 
                        }
                        applyFilter()
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

    private fun applyFilter() {
        val filtered = filterContactsUseCase(
            contacts = allContacts,
            query = _uiState.value.searchQuery,
            sortOrder = _uiState.value.sortOrder
        )
        _uiState.update { it.copy(visibleContacts = filtered) }
    }

    private fun clearContacts() {
        loadContactsJob?.cancel()
        allContacts = emptyList()
        _uiState.update { 
            it.copy(
                visibleContacts = emptyList(), 
                hasLoadedContacts = false,
                isLoading = false,
                errorMessageRes = null
            ) 
        }
    }

    private var hasRequestedPermission: Boolean
        get() = savedStateHandle.get<Boolean>(KEY_HAS_REQUESTED_PERMISSION) ?: false
        set(value) {
            savedStateHandle[KEY_HAS_REQUESTED_PERMISSION] = value
        }

    companion object {
        private const val KEY_HAS_REQUESTED_PERMISSION = "has_requested_permission"
        private const val KEY_SEARCH_QUERY = "search_query"
        private const val KEY_SORT_ORDER = "sort_order"
    }
}
