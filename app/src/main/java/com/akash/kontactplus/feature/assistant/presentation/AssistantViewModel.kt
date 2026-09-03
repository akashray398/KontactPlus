package com.akash.kontactplus.feature.assistant.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.feature.relationship.domain.usecase.AssistantDashboard
import com.akash.kontactplus.feature.relationship.domain.usecase.ObserveAssistantDashboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val observeAssistantDashboardUseCase: ObserveAssistantDashboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    init {
        observeDashboard()
    }

    private fun observeDashboard() {
        _uiState.update { it.copy(isLoading = true) }
        observeAssistantDashboardUseCase()
            .onEach { dashboard ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        dashboard = dashboard
                    ) 
                }
            }
            .catch { 
                _uiState.update { it.copy(isLoading = false, errorMessageRes = com.akash.kontactplus.R.string.recents_error_description) }
            }
            .launchIn(viewModelScope)
    }
}

data class AssistantUiState(
    val isLoading: Boolean = false,
    val dashboard: AssistantDashboard = AssistantDashboard(),
    val errorMessageRes: Int? = null
)
