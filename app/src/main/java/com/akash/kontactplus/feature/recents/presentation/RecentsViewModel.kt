package com.akash.kontactplus.feature.recents.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.R
import com.akash.kontactplus.feature.recents.domain.model.RecentCall
import com.akash.kontactplus.feature.recents.domain.usecase.FilterRecentCallsUseCase
import com.akash.kontactplus.feature.recents.domain.usecase.GetRecentCallsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRecentCallsUseCase: GetRecentCallsUseCase,
    private val filterRecentCallsUseCase: FilterRecentCallsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecentsUiState())
    val uiState: StateFlow<RecentsUiState> = _uiState.asStateFlow()

    private var allCalls: List<RecentCall> = emptyList()
    private var loadJob: Job? = null

    private var hasRequestedPermission: Boolean
        get() = savedStateHandle.get<Boolean>(KEY_HAS_REQUESTED_PERMISSION) ?: false
        set(value) {
            savedStateHandle[KEY_HAS_REQUESTED_PERMISSION] = value
        }

    fun onAccessStatusChecked(
        isRoleHeld: Boolean,
        isPermissionGranted: Boolean,
        shouldShowRationale: Boolean,
        isTelecomSupported: Boolean
    ) {
        val newState = when {
            !isTelecomSupported -> RecentsAccessState.RoleUnsupported
            !isRoleHeld -> RecentsAccessState.RoleRequired
            isPermissionGranted -> RecentsAccessState.Ready
            !hasRequestedPermission -> RecentsAccessState.PermissionNotRequested
            shouldShowRationale -> RecentsAccessState.PermissionDenied
            else -> RecentsAccessState.PermissionPermanentlyDenied
        }
        
        _uiState.update { it.copy(accessState = newState) }
        
        if (newState == RecentsAccessState.Ready && !_uiState.value.hasLoadedCalls) {
            loadRecents()
        } else if (newState != RecentsAccessState.Ready) {
            clearRecents()
        }
    }

    fun onPermissionRequestStarted() {
        hasRequestedPermission = true
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    fun onClearSearch() {
        onSearchQueryChanged("")
    }

    fun retry() {
        if (_uiState.value.accessState == RecentsAccessState.Ready) {
            loadRecents()
        }
    }

    fun refresh() {
        if (_uiState.value.accessState == RecentsAccessState.Ready) {
            loadRecents()
        }
    }

    private fun loadRecents() {
        if (_uiState.value.isLoading) return
        
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
            
            getRecentCallsUseCase().fold(
                onSuccess = { calls ->
                    if (_uiState.value.accessState == RecentsAccessState.Ready) {
                        allCalls = calls
                        _uiState.update { it.copy(isLoading = false, hasLoadedCalls = true) }
                        applyFilter()
                    }
                },
                onFailure = {
                    if (_uiState.value.accessState == RecentsAccessState.Ready) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                errorMessageRes = R.string.recents_error_description 
                            ) 
                        }
                    }
                }
            )
        }
    }

    private fun applyFilter() {
        val filtered = filterRecentCallsUseCase(allCalls, _uiState.value.searchQuery)
        _uiState.update { it.copy(visibleCalls = filtered) }
    }

    private fun clearRecents() {
        loadJob?.cancel()
        allCalls = emptyList()
        _uiState.update { 
            it.copy(
                visibleCalls = emptyList(), 
                hasLoadedCalls = false,
                isLoading = false,
                errorMessageRes = null
            ) 
        }
    }

    companion object {
        private const val KEY_HAS_REQUESTED_PERMISSION = "has_requested_call_log_permission"
    }
}
