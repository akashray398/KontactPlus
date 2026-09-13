package com.akash.kontactplus.feature.settings.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsSettingsScreen(
    uiState: InsightsSettingsUiState,
    onInsightsEnabledChange: (Boolean) -> Unit,
    onCallHistoryEnabledChange: (Boolean) -> Unit,
    onShowMissedCallsChange: (Boolean) -> Unit,
    onShowImportantDatesChange: (Boolean) -> Unit,
    onShowRemindersChange: (Boolean) -> Unit,
    onClearDismissedSuggestions: () -> Unit,
    onPrivacyCenterClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.insights_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SwitchListItem(
                title = stringResource(R.string.connection_insights_enable),
                checked = uiState.areInsightsEnabled,
                onCheckedChange = onInsightsEnabledChange
            )
            
            if (uiState.areInsightsEnabled) {
                HorizontalDivider()
                
                Text(text = "Data Sources", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                
                SwitchListItem(
                    title = stringResource(R.string.enable_call_insights),
                    description = stringResource(R.string.connection_insights_privacy_description),
                    checked = uiState.isCallHistoryEnabled,
                    onCheckedChange = onCallHistoryEnabledChange
                )
                
                HorizontalDivider()
                
                Text(text = "Suggestions", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                
                SwitchListItem(
                    title = stringResource(R.string.insight_missed_call_title),
                    checked = uiState.showMissedCalls,
                    onCheckedChange = onShowMissedCallsChange,
                    enabled = uiState.isCallHistoryEnabled
                )
                
                SwitchListItem(
                    title = stringResource(R.string.relationship_important_dates),
                    checked = uiState.showImportantDates,
                    onCheckedChange = onShowImportantDatesChange
                )
                
                SwitchListItem(
                    title = stringResource(R.string.relationship_reminders),
                    checked = uiState.showReminders,
                    onCheckedChange = onShowRemindersChange
                )
                
                HorizontalDivider()
                
                TextButton(onClick = onClearDismissedSuggestions) {
                    Text(stringResource(R.string.insights_clear_dismissed))
                }

                HorizontalDivider()

                TextButton(
                    onClick = onPrivacyCenterClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text("Privacy Center")
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SwitchListItem(
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline)
            if (description != null) {
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}
