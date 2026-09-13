package com.akash.kontactplus.feature.settings.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    uiState: PrivacySettingsUiState,
    snackbarHostState: SnackbarHostState,
    onAiEnabledChange: (Boolean) -> Unit,
    onInsightsEnabledChange: (Boolean) -> Unit,
    onCallHistoryEnabledChange: (Boolean) -> Unit,
    onDeleteRelationshipData: () -> Unit,
    onResetSuggestions: () -> Unit,
    onBackClick: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.delete_orphaned_relationship_data)) },
            text = { Text("This will permanently delete all your private notes, tags, dates, and reminders. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteRelationshipData()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.ai_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Privacy Center",
                        modifier = Modifier.semantics { heading() }
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Local Data", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            
            Text(
                text = "Contacts and call history are read from your device. Relationship notes and insights are stored only on this device and are excluded from cloud backups.",
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider()

            Text(text = "Feature Controls", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

            SwitchListItem(
                title = "Online AI Assistance",
                description = "Enable optional AI tools for drafting. Content is sent only after you review it.",
                checked = uiState.isAiEnabled,
                onCheckedChange = onAiEnabledChange
            )

            SwitchListItem(
                title = "On-Device Insights",
                description = "Calculate follow-up suggestions locally.",
                checked = uiState.areInsightsEnabled,
                onCheckedChange = onInsightsEnabledChange
            )

            SwitchListItem(
                title = "Call History Analysis",
                description = "Use call dates to improve local insights. Never sent to any server.",
                checked = uiState.isCallHistoryEnabled,
                onCheckedChange = onCallHistoryEnabledChange,
                enabled = uiState.areInsightsEnabled
            )

            HorizontalDivider()

            Text(text = "Data Management", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

            TextButton(
                onClick = onResetSuggestions,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Reset Snoozed Suggestions",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Delete Local Relationship Data")
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
