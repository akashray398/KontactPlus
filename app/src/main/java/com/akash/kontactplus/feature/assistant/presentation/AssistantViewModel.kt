package com.akash.kontactplus.feature.assistant.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.R
import com.akash.kontactplus.feature.relationship.domain.model.ConnectionInsight
import com.akash.kontactplus.feature.relationship.domain.model.ConnectionInsightSource
import com.akash.kontactplus.feature.relationship.domain.repository.ConnectionInsightsRepository
import com.akash.kontactplus.feature.relationship.domain.repository.InteractionInsightsRepository
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import com.akash.kontactplus.feature.relationship.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val observeConnectionInsightsUseCase: ObserveConnectionInsightsUseCase,
    private val connectionInsightsRepository: ConnectionInsightsRepository,
    private val acceptCallHistoryDisclosureUseCase: AcceptCallHistoryDisclosureUseCase,
    private val snoozeConnectionInsightUseCase: SnoozeConnectionInsightUseCase,
    private val dismissConnectionInsightUseCase: DismissConnectionInsightUseCase,
    private val completeRelationshipReminderUseCase: CompleteRelationshipReminderUseCase,
    private val interactionInsightsRepository: InteractionInsightsRepository,
    private val relationshipRepository: RelationshipRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    private val _events = Channel<AssistantUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        observeInsights()
        observeSettings()
    }

    fun refresh() {
        viewModelScope.launch {
            interactionInsightsRepository.refresh()
        }
    }

    fun onUndoAction(insight: ConnectionInsight) {
        viewModelScope.launch {
            relationshipRepository.deleteSuggestionAction(insight.id)
        }
    }

    private fun observeInsights() {
        observeConnectionInsightsUseCase()
            .onStart { _uiState.update { it.copy(isLoading = true) } }
            .onEach { insights ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        insights = insights
                    ) 
                }
            }
            .catch { 
                _uiState.update { it.copy(isLoading = false, errorMessageRes = R.string.recents_error_description) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeSettings() {
        connectionInsightsRepository.hasAcceptedCallHistoryDisclosure()
            .onEach { accepted -> _uiState.update { it.copy(hasAcceptedCallHistoryDisclosure = accepted) } }
            .launchIn(viewModelScope)
        
        connectionInsightsRepository.areCallHistoryInsightsEnabled()
            .onEach { enabled -> _uiState.update { it.copy(isCallHistoryInsightsEnabled = enabled) } }
            .launchIn(viewModelScope)
    }

    fun onAcceptCallHistoryDisclosure() {
        viewModelScope.launch {
            acceptCallHistoryDisclosureUseCase()
        }
    }

    fun onSnoozeInsight(insight: ConnectionInsight) {
        viewModelScope.launch {
            snoozeConnectionInsightUseCase(insight)
            _events.send(AssistantUiEvent.ShowUndo(insight))
        }
    }

    fun onDismissInsight(insight: ConnectionInsight) {
        viewModelScope.launch {
            dismissConnectionInsightUseCase(insight)
            _events.send(AssistantUiEvent.ShowUndo(insight))
        }
    }

    fun onMarkReminderDone(insight: ConnectionInsight) {
        if (insight.source == ConnectionInsightSource.Reminder) {
            val reminderId = insight.id.removePrefix("reminder_")
            viewModelScope.launch {
                completeRelationshipReminderUseCase(reminderId)
            }
        }
    }
}

sealed interface AssistantUiEvent {
    data class ShowUndo(val insight: ConnectionInsight) : AssistantUiEvent
}

data class AssistantUiState(
    val isLoading: Boolean = false,
    val insights: List<ConnectionInsight> = emptyList(),
    val hasAcceptedCallHistoryDisclosure: Boolean = false,
    val isCallHistoryInsightsEnabled: Boolean = false,
    val errorMessageRes: Int? = null
)
