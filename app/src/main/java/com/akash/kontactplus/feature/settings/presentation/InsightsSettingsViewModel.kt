package com.akash.kontactplus.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.feature.relationship.domain.repository.ConnectionInsightsRepository
import com.akash.kontactplus.feature.relationship.domain.usecase.ClearDismissedSuggestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsightsSettingsViewModel @Inject constructor(
    private val repository: ConnectionInsightsRepository,
    private val clearDismissedSuggestionsUseCase: ClearDismissedSuggestionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsSettingsUiState())
    val uiState: StateFlow<InsightsSettingsUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        combine(
            repository.areConnectionInsightsEnabled(),
            repository.areCallHistoryInsightsEnabled(),
            repository.shouldShowMissedCallSuggestions(),
            repository.shouldShowImportantDateSuggestions(),
            repository.shouldShowReminderSuggestions()
        ) { enabled, callLogEnabled, missedCalls, dates, reminders ->
            InsightsSettingsUiState(
                areInsightsEnabled = enabled,
                isCallHistoryEnabled = callLogEnabled,
                showMissedCalls = missedCalls,
                showImportantDates = dates,
                showReminders = reminders
            )
        }.onEach { state -> _uiState.value = state }
        .launchIn(viewModelScope)
    }

    fun setInsightsEnabled(enabled: Boolean) = viewModelScope.launch { repository.setConnectionInsightsEnabled(enabled) }
    fun setCallHistoryEnabled(enabled: Boolean) = viewModelScope.launch { repository.setCallHistoryInsightsEnabled(enabled) }
    fun setShowMissedCalls(show: Boolean) = viewModelScope.launch { repository.setShowMissedCallSuggestions(show) }
    fun setShowImportantDates(show: Boolean) = viewModelScope.launch { repository.setShowImportantDateSuggestions(show) }
    fun setShowReminders(show: Boolean) = viewModelScope.launch { repository.setShowReminderSuggestions(show) }

    fun onClearDismissedSuggestions() = viewModelScope.launch {
        clearDismissedSuggestionsUseCase()
    }
}

data class InsightsSettingsUiState(
    val areInsightsEnabled: Boolean = true,
    val isCallHistoryEnabled: Boolean = false,
    val showMissedCalls: Boolean = false,
    val showImportantDates: Boolean = true,
    val showReminders: Boolean = true
)
