package com.akash.kontactplus.feature.contacts.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.SetupItem
import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.model.ContactSortOrder
import com.akash.kontactplus.feature.contacts.domain.usecase.FilterContactsUseCase
import com.akash.kontactplus.feature.contacts.domain.usecase.GetContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getContactsUseCase: GetContactsUseCase,
    private val filterContactsUseCase: FilterContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    private val _allContacts = MutableStateFlow<List<Contact>>(emptyList())
    private val _searchQuery = savedStateHandle.getStateFlow(KEY_SEARCH_QUERY, "")
    private val _sortOrder = savedStateHandle.getStateFlow(KEY_SORT_ORDER, ContactSortOrder.NameAscending)
    
    private val _isSetupCardDismissed = savedStateHandle.getStateFlow(KEY_SETUP_CARD_DISMISSED, false)

    private var loadContactsJob: Job? = null

    init {
        combine(
            _allContacts,
            _searchQuery.debounce(200),
            _sortOrder,
            _isSetupCardDismissed
        ) { contacts, query, sortOrder, dismissed ->
            val filtered = filterContactsUseCase(contacts, query, sortOrder)
            filtered to dismissed
        }.onEach { (filtered, dismissed) ->
            _uiState.update { 
                it.copy(
                    visibleContacts = filtered, 
                    searchQuery = _searchQuery.value, 
                    sortOrder = _sortOrder.value,
                    showSetupCard = !dismissed && _uiState.value.setupItems.any { !it.isCompleted }
                ) 
            }
        }.launchIn(viewModelScope)
    }

    fun updateSetupItems(
        isContactsGranted: Boolean,
        isDialerHeld: Boolean,
        isCallLogGranted: Boolean,
        isNotificationsGranted: Boolean,
        isAiDisclosureAccepted: Boolean
    ) {
        val items = listOf(
            SetupItem("Contacts access", isContactsGranted) {},
            SetupItem("Default Phone role", isDialerHeld) {},
            SetupItem("Call history access", isCallLogGranted) {},
            SetupItem("Notification permission", isNotificationsGranted) {},
            SetupItem("Optional AI help (Opt-in)", isAiDisclosureAccepted) {}
        )
        _uiState.update { 
            it.copy(
                setupItems = items,
                showSetupCard = !_isSetupCardDismissed.value && items.any { !it.isCompleted && it.title != "Optional AI help (Opt-in)" }
            ) 
        }
    }

    fun onDismissSetupCard() {
        savedStateHandle[KEY_SETUP_CARD_DISMISSED] = true
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
        savedStateHandle[KEY_SEARCH_QUERY] = query
    }

    fun onClearSearch() {
        onSearchQueryChanged("")
    }

    fun onSortOrderChanged(sortOrder: ContactSortOrder) {
        savedStateHandle[KEY_SORT_ORDER] = sortOrder
    }

    private fun loadContacts() {
        if (_uiState.value.isLoading) return
        
        loadContactsJob?.cancel()
        loadContactsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
            
            getContactsUseCase().fold(
                onSuccess = { contacts ->
                    if (_uiState.value.permissionState == ContactsPermissionState.Granted) {
                        _allContacts.value = contacts
                        _uiState.update { 
                            it.copy(
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
        _allContacts.value = emptyList()
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
        private const val KEY_SETUP_CARD_DISMISSED = "setup_card_dismissed"
    }
}
