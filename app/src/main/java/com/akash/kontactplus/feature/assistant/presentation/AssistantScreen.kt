package com.akash.kontactplus.feature.assistant.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium
import com.akash.kontactplus.core.designsystem.theme.SpaceSmall
import com.akash.kontactplus.feature.relationship.domain.model.ConnectionInsight
import com.akash.kontactplus.feature.relationship.domain.model.ConnectionInsightAction
import com.akash.kontactplus.feature.relationship.domain.model.ConnectionInsightSource
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    uiState: AssistantUiState,
    onContactClick: (String) -> Unit,
    onAiToolsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAcceptCallHistoryDisclosure: () -> Unit,
    onSnoozeInsight: (ConnectionInsight) -> Unit,
    onDismissInsight: (ConnectionInsight) -> Unit,
    onMarkReminderDone: (ConnectionInsight) -> Unit,
    onDraftGreeting: (ConnectionInsight) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    var selectedInsightForExplanation by remember { mutableStateOf<ConnectionInsight?>(null) }

    if (selectedInsightForExplanation != null) {
        InsightExplanationSheet(
            insight = selectedInsightForExplanation!!,
            onDismiss = { selectedInsightForExplanation = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.assistant_title)) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.insights_settings))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = SpaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = SpaceMedium),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall)
            ) {
                // Call History Disclosure
                if (!uiState.hasAcceptedCallHistoryDisclosure) {
                    item {
                        CallHistoryDisclosureCard(onAccept = onAcceptCallHistoryDisclosure)
                        Spacer(Modifier.height(SpaceMedium))
                    }
                }

                // AI Tools Entry
                item {
                    Card(
                        onClick = onAiToolsClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(SpaceMedium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null)
                            Spacer(Modifier.width(SpaceMedium))
                            Column {
                                Text(text = stringResource(R.string.ai_tools_title), style = MaterialTheme.typography.titleMedium)
                                Text(text = stringResource(R.string.ai_tools_description), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Spacer(Modifier.height(SpaceMedium))
                }

                if (uiState.isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(SpaceMedium), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (uiState.insights.isEmpty()) {
                    item {
                        AllCaughtUpState()
                    }
                } else {
                    // Insights Categorized
                    val needsAttention = uiState.insights.filter { 
                        (it.source == ConnectionInsightSource.Reminder) || (it.source == ConnectionInsightSource.MissedCall) 
                    }
                    val followUps = uiState.insights.filter { it.source == ConnectionInsightSource.UserCadence }
                    val comingUp = uiState.insights.filter { it.source == ConnectionInsightSource.ImportantDate }

                    if (needsAttention.isNotEmpty()) {
                        item { SectionHeader(stringResource(R.string.insights_needs_attention), MaterialTheme.colorScheme.error) }
                        items(needsAttention) { insight ->
                            InsightCard(
                                insight = insight,
                                onContactClick = onContactClick,
                                onSnooze = onSnoozeInsight,
                                onDismiss = onDismissInsight,
                                onMarkDone = onMarkReminderDone,
                                onDraftMessage = onDraftGreeting,
                                onInfoClick = { selectedInsightForExplanation = it }
                            )
                        }
                    }

                    if (followUps.isNotEmpty()) {
                        item { SectionHeader(stringResource(R.string.insights_follow_ups)) }
                        items(followUps) { insight ->
                            InsightCard(
                                insight = insight,
                                onContactClick = onContactClick,
                                onSnooze = onSnoozeInsight,
                                onDismiss = onDismissInsight,
                                onMarkDone = onMarkReminderDone,
                                onDraftMessage = onDraftGreeting,
                                onInfoClick = { selectedInsightForExplanation = it }
                            )
                        }
                    }

                    if (comingUp.isNotEmpty()) {
                        item { SectionHeader(stringResource(R.string.insights_coming_up)) }
                        items(comingUp) { insight ->
                            InsightCard(
                                insight = insight,
                                onContactClick = onContactClick,
                                onSnooze = onSnoozeInsight,
                                onDismiss = onDismissInsight,
                                onMarkDone = onMarkReminderDone,
                                onDraftMessage = onDraftGreeting,
                                onInfoClick = { selectedInsightForExplanation = it }
                            )
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(SpaceMedium)) }
            }
        }
    }
}

@Composable
private fun CallHistoryDisclosureCard(onAccept: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(SpaceMedium)) {
            Text(text = stringResource(R.string.connection_insights_privacy_title), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(SpaceSmall))
            Text(text = stringResource(R.string.connection_insights_privacy_description), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(SpaceMedium))
            Button(onClick = onAccept, modifier = Modifier.align(Alignment.End)) {
                Text(stringResource(R.string.connection_insights_accept))
            }
        }
    }
}

@Composable
private fun InsightCard(
    insight: ConnectionInsight,
    onContactClick: (String) -> Unit,
    onSnooze: (ConnectionInsight) -> Unit,
    onDismiss: (ConnectionInsight) -> Unit,
    onMarkDone: (ConnectionInsight) -> Unit,
    onDraftMessage: (ConnectionInsight) -> Unit,
    onInfoClick: (ConnectionInsight) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onContactClick(insight.lookupKey) }
    ) {
        Column(modifier = Modifier.padding(SpaceMedium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(insight.titleRes),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { onInfoClick(insight) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
            Text(text = formatInsightExplanation(insight), style = MaterialTheme.typography.titleSmall)
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (insight.availableActions.contains(ConnectionInsightAction.DraftMessage)) {
                    TextButton(onClick = { onDraftMessage(insight) }) {
                        Text("Draft")
                    }
                }
                if (insight.availableActions.contains(ConnectionInsightAction.Snooze)) {
                    TextButton(onClick = { onSnooze(insight) }) {
                        Text(stringResource(R.string.insight_snooze))
                    }
                }
                if (insight.availableActions.contains(ConnectionInsightAction.Dismiss)) {
                    TextButton(onClick = { onDismiss(insight) }) {
                        Text(stringResource(R.string.insight_dismiss))
                    }
                }
                if (insight.availableActions.contains(ConnectionInsightAction.MarkDone)) {
                    TextButton(onClick = { onMarkDone(insight) }) {
                        Text(stringResource(R.string.insight_mark_done))
                    }
                }
            }
        }
    }
}

@Composable
private fun formatInsightExplanation(insight: ConnectionInsight): String {
    return when (insight.explanationRes) {
        R.string.insight_missed_call_explanation -> {
            val timestamp = insight.explanationArgs.first().toLong()
            val time = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
            val now = LocalDate.now()
            val days = ChronoUnit.DAYS.between(time, now).toInt()
            val timeAgo = pluralStringResource(R.plurals.insight_days_ago, days, days)
            stringResource(R.string.insight_missed_call_explanation, timeAgo)
        }
        else -> stringResource(insight.explanationRes, *insight.explanationArgs.toTypedArray())
    }
}

@Composable
private fun AllCaughtUpState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(SpaceMedium))
        Text(
            text = stringResource(R.string.assistant_all_caught_up),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(R.string.assistant_all_caught_up_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun SectionHeader(title: String, color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = color,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightExplanationSheet(
    insight: ConnectionInsight,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth().padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(R.string.insight_why_shown), style = MaterialTheme.typography.titleLarge)
            
            Text(text = formatInsightExplanation(insight), style = MaterialTheme.typography.bodyLarge)
            
            HorizontalDivider()
            
            Column {
                Text(text = stringResource(R.string.insight_data_used), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(text = mapInsightSourceToData(insight.source), style = MaterialTheme.typography.bodyMedium)
            }
            
            Column {
                Text(text = stringResource(R.string.insight_online_ai), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(text = stringResource(R.string.insight_online_ai_not_used), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

private fun mapInsightSourceToData(source: ConnectionInsightSource): String {
    return when (source) {
        ConnectionInsightSource.UserCadence -> "Follow-up schedule and matched call history"
        ConnectionInsightSource.MissedCall -> "Call log (unreturned missed call)"
        ConnectionInsightSource.ImportantDate -> "Saved important dates"
        ConnectionInsightSource.Reminder -> "Saved reminders"
    }
}
