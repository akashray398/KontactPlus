package com.akash.kontactplus.feature.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun InsightsSettingsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InsightsSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InsightsSettingsScreen(
        uiState = uiState,
        onInsightsEnabledChange = viewModel::setInsightsEnabled,
        onCallHistoryEnabledChange = viewModel::setCallHistoryEnabled,
        onShowMissedCallsChange = viewModel::setShowMissedCalls,
        onShowImportantDatesChange = viewModel::setShowImportantDates,
        onShowRemindersChange = viewModel::setShowReminders,
        onClearDismissedSuggestions = viewModel::onClearDismissedSuggestions,
        onBackClick = onBackClick
    )
}
