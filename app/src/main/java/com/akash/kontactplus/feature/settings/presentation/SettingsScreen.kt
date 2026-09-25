package com.akash.kontactplus.feature.settings.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.BuildConfig
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium
import com.akash.kontactplus.core.designsystem.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    isDemoModeEnabled: Boolean,
    onDemoModeToggle: (Boolean) -> Unit,
    onClearRelationshipData: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToInsights: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToAiPrivacy: () -> Unit,
    onReplayOnboarding: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val supportEmail = stringResource(R.string.support_email)
    val githubUrl = stringResource(R.string.github_url)

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
            // 1. Account & Profile
            SettingsCategory(title = "Account & Intelligence Profile")
            SettingsItem(
                title = "My Relationship Profile",
                icon = Icons.Default.AccountCircle,
                onClick = onNavigateToProfile
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 2. Appearance / Theme
            SettingsCategory(title = stringResource(R.string.settings_appearance))
            Text(
                text = stringResource(R.string.settings_theme),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = themeMode == ThemeMode.System,
                    onClick = { onThemeModeChange(ThemeMode.System) },
                    label = { Text(stringResource(R.string.settings_theme_system)) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = themeMode == ThemeMode.Light,
                    onClick = { onThemeModeChange(ThemeMode.Light) },
                    label = { Text(stringResource(R.string.settings_theme_light)) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = themeMode == ThemeMode.Dark,
                    onClick = { onThemeModeChange(ThemeMode.Dark) },
                    label = { Text(stringResource(R.string.settings_theme_dark)) },
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 3. Phone & Permissions
            SettingsCategory(title = stringResource(R.string.settings_phone_permissions))
            SettingsItem(
                title = stringResource(R.string.connection_insights_title),
                icon = Icons.Default.AutoAwesome,
                onClick = onNavigateToInsights
            )
            SettingsItem(
                title = stringResource(R.string.contacts_permission_open_settings),
                icon = Icons.Default.Settings,
                onClick = { openAppSettings(context) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 4. Privacy
            SettingsCategory(title = stringResource(R.string.settings_privacy_group))
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
            SettingsItem(
                title = stringResource(R.string.settings_clear_relationship_data),
                icon = Icons.Default.DeleteForever,
                onClick = { showClearDataDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 5. Education & Onboarding
            SettingsCategory(title = "Education")
            SettingsItem(
                title = "Replay Onboarding",
                icon = Icons.Default.Info,
                onClick = onReplayOnboarding
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 6. Contact & Support
            SettingsCategory(title = stringResource(R.string.settings_support_group))
            SettingsItem(
                title = stringResource(R.string.settings_contact_developer),
                icon = Icons.Default.Email,
                onClick = { sendEmail(context, supportEmail, "Kontact++ Support") }
            )
            SettingsItem(
                title = stringResource(R.string.settings_github_repo),
                icon = Icons.Default.Code,
                onClick = { openUrl(context, githubUrl) }
            )
            SettingsItem(
                title = stringResource(R.string.settings_share_app),
                icon = Icons.Default.Share,
                onClick = { shareApp(context) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 7. About
            SettingsCategory(title = "About")
            SettingsItem(
                title = "About Kontact++",
                icon = Icons.Default.Star,
                onClick = { showAboutDialog = true }
            )

            if (BuildConfig.DEBUG) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
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

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text(stringResource(R.string.settings_clear_relationship_data)) },
            text = { Text(stringResource(R.string.settings_clear_relationship_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearRelationshipData()
                        showClearDataDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Kontact++") },
            text = {
                Column {
                    Text(
                        text = "Kontact++ — Your AI Relationship Memory",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Built with Kotlin, Jetpack Compose, Material 3, Clean Architecture, Hilt, Room, and Ktor.\n\nPrivacy-First Design: Your contacts, call history, and relationship notes remain strictly local on your device.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun SettingsCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
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

private fun openAppSettings(context: Context) {
    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {}
}

private fun sendEmail(context: Context, email: String, subject: String) {
    if (email.isBlank()) return
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
        putExtra(Intent.EXTRA_SUBJECT, subject)
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {}
}

private fun openUrl(context: Context, url: String) {
    if (url.isBlank()) return
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        context.startActivity(intent)
    } catch (_: Exception) {}
}

private fun shareApp(context: Context) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Check out Kontact++ — Your AI Relationship Memory: https://github.com/akashray398/KontactPlus")
        type = "text/plain"
    }
    try {
        context.startActivity(Intent.createChooser(sendIntent, "Share Kontact++"))
    } catch (_: Exception) {}
}
