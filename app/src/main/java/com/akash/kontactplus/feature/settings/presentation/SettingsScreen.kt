package com.akash.kontactplus.feature.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.BuildConfig
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium
import com.akash.kontactplus.core.designsystem.theme.SpaceSmall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDemoModeEnabled: Boolean,
    onDemoModeToggle: (Boolean) -> Unit,
    onNavigateToInsights: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToAiPrivacy: () -> Unit,
    onReplayOnboarding: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Settings",
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
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SettingsCategory(title = "Phone & Permissions")
            SettingsItem(
                title = "Connection Insights",
                icon = Icons.Default.AutoAwesome,
                onClick = onNavigateToInsights
            )
            
            SettingsCategory(title = "Privacy")
            SettingsItem(
                title = "Privacy Center",
                icon = Icons.Default.PrivacyTip,
                onClick = onNavigateToPrivacy
            )
            SettingsItem(
                title = "AI Privacy",
                icon = Icons.Default.Psychology,
                onClick = onNavigateToAiPrivacy
            )

            SettingsCategory(title = "Education")
            SettingsItem(
                title = "Replay Onboarding",
                icon = Icons.Default.Info,
                onClick = onReplayOnboarding
            )

            SettingsCategory(title = "About")
            SettingsItem(
                title = "About Kontact++",
                icon = Icons.Default.Star,
                onClick = { /* Show about dialog */ }
            )

            if (BuildConfig.DEBUG) {
                SettingsCategory(title = "Developer (Debug Only)")
                ListItem(
                    headlineContent = { Text("Screenshot / Demo Mode") },
                    supportingContent = { Text("Uses synthetic data for store screenshots.") },
                    leadingContent = {
                        Icon(Icons.Default.BugReport, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    },
                    trailingContent = {
                        Switch(checked = isDemoModeEnabled, onCheckedChange = onDemoModeToggle)
                    }
                )
                if (isDemoModeEnabled) {
                    Text(
                        text = "Demo data will be shown instead of real contacts and call logs.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceMedium),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun SettingsCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        trailingContent = {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
