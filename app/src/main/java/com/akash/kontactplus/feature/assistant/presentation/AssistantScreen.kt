package com.akash.kontactplus.feature.assistant.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium
import com.akash.kontactplus.core.designsystem.theme.SpaceSmall
import com.akash.kontactplus.feature.relationship.domain.usecase.DashboardItem

@Composable
fun AssistantScreen(
    uiState: AssistantUiState,
    onContactClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = SpaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(SpaceSmall))
        
        Text(
            text = stringResource(R.string.assistant_title),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.isLoading) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.dashboard.dueToday.isEmpty() && 
                   uiState.dashboard.overdue.isEmpty() && 
                   uiState.dashboard.upcomingDates.isEmpty() && 
                   uiState.dashboard.upcomingReminders.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = SpaceMedium),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall)
            ) {
                if (uiState.dashboard.overdue.isNotEmpty()) {
                    item { SectionHeader(stringResource(R.string.assistant_overdue), MaterialTheme.colorScheme.error) }
                    items(uiState.dashboard.overdue) { item ->
                        DashboardCard(item, onClick = { onContactClick(item.lookupKey) })
                    }
                }

                if (uiState.dashboard.dueToday.isNotEmpty()) {
                    item { SectionHeader(stringResource(R.string.assistant_due_today)) }
                    items(uiState.dashboard.dueToday) { item ->
                        DashboardCard(item, onClick = { onContactClick(item.lookupKey) })
                    }
                }

                if (uiState.dashboard.upcomingDates.isNotEmpty()) {
                    item { SectionHeader(stringResource(R.string.assistant_coming_up)) }
                    items(uiState.dashboard.upcomingDates) { item ->
                        DashboardCard(item, onClick = { onContactClick(item.lookupKey) })
                    }
                }

                if (uiState.dashboard.upcomingReminders.isNotEmpty()) {
                    item { SectionHeader(stringResource(R.string.assistant_upcoming)) }
                    items(uiState.dashboard.upcomingReminders) { item ->
                        DashboardCard(item, onClick = { onContactClick(item.lookupKey) })
                    }
                }
                
                item { Spacer(modifier = Modifier.height(SpaceMedium)) }
            }
        }
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

@Composable
private fun DashboardCard(
    item: DashboardItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(SpaceMedium)) {
            Text(
                text = item.contactName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(text = item.title, style = MaterialTheme.typography.titleSmall)
            if (!item.subtitle.isNullOrBlank()) {
                Text(
                    text = item.subtitle, 
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
