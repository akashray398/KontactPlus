package com.akash.kontactplus.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.feature.ai.domain.repository.AiRepository
import com.akash.kontactplus.feature.relationship.domain.repository.ConnectionInsightsRepository
import com.akash.kontactplus.feature.relationship.domain.usecase.ClearDismissedSuggestionsUseCase
import com.akash.kontactplus.feature.relationship.domain.usecase.DeleteAllRelationshipDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacySettingsViewModel @Inject constructor(
    private val aiRepository: AiRepository,
    private val insightsRepository: ConnectionInsightsRepository,
    private val deleteRelationshipDataUseCase: DeleteAllRelationshipDataUseCase,
    private val clearDismissedSuggestionsUseCase: ClearDismissedSuggestionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrivacySettingsUiState())
    val uiState: StateFlow<PrivacySettingsUiState> = _uiState.asStateFlow()

    private val _events = Channel<PrivacyUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        combine(
            aiRepository.isAiEnabled(),
            insightsRepository.areConnectionInsightsEnabled(),
            insightsRepository.areCallHistoryInsightsEnabled()
        ) { aiEnabled, insightsEnabled, callLogEnabled ->
            PrivacySettingsUiState(
                isAiEnabled = aiEnabled,
                areInsightsEnabled = insightsEnabled,
                isCallHistoryEnabled = callLogEnabled
            )
        }.onEach { _uiState.value = it }
        .launchIn(viewModelScope)
    }

    fun setAiEnabled(enabled: Boolean) = viewModelScope.launch {
        aiRepository.setAiEnabled(enabled)
    }

    fun setInsightsEnabled(enabled: Boolean) = viewModelScope.launch {
        insightsRepository.setConnectionInsightsEnabled(enabled)
    }

    fun setCallHistoryEnabled(enabled: Boolean) = viewModelScope.launch {
        insightsRepository.setCallHistoryInsightsEnabled(enabled)
    }

    fun onDeleteRelationshipData() = viewModelScope.launch {
        deleteRelationshipDataUseCase().fold(
            onSuccess = { _events.send(PrivacyUiEvent.DataDeleted) },
            onFailure = { _events.send(PrivacyUiEvent.Error("Failed to delete data")) }
        )
    }

    fun onResetSuggestions() = viewModelScope.launch {
        clearDismissedSuggestionsUseCase().fold(
            onSuccess = { _events.send(PrivacyUiEvent.SuggestionsReset) },
            onFailure = { _events.send(PrivacyUiEvent.Error("Failed to reset suggestions")) }
        )
    }
}

sealed interface PrivacyUiEvent {
    data object DataDeleted : PrivacyUiEvent
    data object SuggestionsReset : PrivacyUiEvent
    data class Error(val message: String) : PrivacyUiEvent
}

data class PrivacySettingsUiState(
    val isAiEnabled: Boolean = false,
    val areInsightsEnabled: Boolean = true,
    val isCallHistoryEnabled: Boolean = false
)
