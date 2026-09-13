package com.akash.kontactplus.feature.settings.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PrivacySettingsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PrivacySettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                PrivacyUiEvent.DataDeleted -> {
                    snackbarHostState.showSnackbar("All local relationship data has been deleted.")
                }
                PrivacyUiEvent.SuggestionsReset -> {
                    snackbarHostState.showSnackbar("Dismissed suggestions have been reset.")
                }
                is PrivacyUiEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    PrivacySettingsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onAiEnabledChange = viewModel::setAiEnabled,
        onInsightsEnabledChange = viewModel::setInsightsEnabled,
        onCallHistoryEnabledChange = viewModel::setCallHistoryEnabled,
        onDeleteRelationshipData = viewModel::onDeleteRelationshipData,
        onResetSuggestions = viewModel::onResetSuggestions,
        onBackClick = onBackClick
    )
}
