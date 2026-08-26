package com.akash.kontactplus.feature.contacts.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

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
    }

    companion object {
        private const val KEY_HAS_REQUESTED_PERMISSION = "has_requested_permission"
    }
}
