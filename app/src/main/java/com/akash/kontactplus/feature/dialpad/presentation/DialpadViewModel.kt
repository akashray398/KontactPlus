package com.akash.kontactplus.feature.dialpad.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.R
import com.akash.kontactplus.feature.dialpad.domain.model.DialpadKey
import com.akash.kontactplus.feature.dialpad.domain.usecase.FindDialpadSuggestionsUseCase
import com.akash.kontactplus.feature.dialpad.domain.usecase.NormalizeDialableNumberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DialpadViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val normalizeDialableNumberUseCase: NormalizeDialableNumberUseCase,
    private val findDialpadSuggestionsUseCase: FindDialpadSuggestionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DialpadUiState())
    val uiState: StateFlow<DialpadUiState> = _uiState.asStateFlow()

    private var suggestionsJob: Job? = null

    private var hasRequestedCallPermission: Boolean
        get() = savedStateHandle.get<Boolean>(KEY_HAS_REQUESTED_PERMISSION) ?: false
        set(value) {
            savedStateHandle[KEY_HAS_REQUESTED_PERMISSION] = value
        }

    fun onKeyPressed(key: DialpadKey) {
        if (_uiState.value.dialableNumber.length >= MAX_NUMBER_LENGTH) {
            _uiState.update { it.copy(inputErrorRes = R.string.dialpad_number_too_long) }
            return
        }
        updateNumber(_uiState.value.dialableNumber + key.value)
    }

    fun onZeroLongPressed() {
        if (_uiState.value.dialableNumber.isEmpty()) {
            updateNumber("+")
        }
    }

    fun onDelete() {
        if (_uiState.value.dialableNumber.isNotEmpty()) {
            updateNumber(_uiState.value.dialableNumber.dropLast(1))
        }
    }

    fun onClear() {
        updateNumber("")
    }

    fun onPastedText(text: String) {
        val sanitized = normalizeDialableNumberUseCase(text)
        if (sanitized.isNotEmpty()) {
            updateNumber(sanitized)
        } else {
            _uiState.update { it.copy(inputErrorRes = R.string.dialpad_clipboard_invalid) }
        }
    }

    fun onExternalNumberReceived(number: String?) {
        if (number != null) {
            updateNumber(normalizeDialableNumberUseCase(number))
        }
    }

    fun onAccessStatusChanged(
        isRoleHeld: Boolean,
        isPermissionGranted: Boolean,
        shouldShowRationale: Boolean,
        isTelecomSupported: Boolean
    ) {
        val newState = when {
            !isTelecomSupported -> DialpadAccessState.RoleUnsupported
            !isRoleHeld -> DialpadAccessState.RoleRequired
            isPermissionGranted -> DialpadAccessState.Ready
            !hasRequestedCallPermission -> DialpadAccessState.CallPermissionNotRequested
            shouldShowRationale -> DialpadAccessState.CallPermissionDenied
            else -> DialpadAccessState.CallPermissionPermanentlyDenied
        }
        _uiState.update { it.copy(accessState = newState) }
    }

    fun onPermissionRequestStarted() {
        hasRequestedCallPermission = true
    }

    fun onCallStarted() {
        _uiState.update { it.copy(isPlacingCall = true, callErrorRes = null) }
    }

    fun onCallFinished() {
        _uiState.update { it.copy(isPlacingCall = false) }
        onClear()
    }

    fun onCallFailed(errorRes: Int) {
        _uiState.update { it.copy(isPlacingCall = false, callErrorRes = errorRes) }
    }

    private fun updateNumber(number: String) {
        _uiState.update { 
            it.copy(
                dialableNumber = number,
                formattedDisplayNumber = number,
                inputErrorRes = null,
                callErrorRes = null
            ) 
        }
        loadSuggestions(number)
    }

    private fun loadSuggestions(query: String) {
        suggestionsJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { it.copy(suggestions = emptyList(), isLoadingSuggestions = false) }
            return
        }

        suggestionsJob = viewModelScope.launch {
            delay(150)
            _uiState.update { it.copy(isLoadingSuggestions = true) }
            val suggestions = findDialpadSuggestionsUseCase(query)
            _uiState.update { it.copy(suggestions = suggestions, isLoadingSuggestions = false) }
        }
    }

    companion object {
        private const val MAX_NUMBER_LENGTH = 31
        private const val KEY_HAS_REQUESTED_PERMISSION = "has_requested_call_permission"
    }
}
