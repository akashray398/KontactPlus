package com.akash.kontactplus.feature.settings.presentation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(
    onNavigateToInsights: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToAiPrivacy: () -> Unit,
    onOnboardingReplayed: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val isDemoModeEnabled by viewModel.isDemoModeEnabled.collectAsStateWithLifecycle()

    SettingsScreen(
        themeMode = themeMode,
        onThemeModeChange = viewModel::setThemeMode,
        isDemoModeEnabled = isDemoModeEnabled,
        onDemoModeToggle = viewModel::toggleDemoMode,
        onClearRelationshipData = viewModel::clearLocalRelationshipData,
        onNavigateToInsights = onNavigateToInsights,
        onNavigateToPrivacy = onNavigateToPrivacy,
        onNavigateToAiPrivacy = onNavigateToAiPrivacy,
        onReplayOnboarding = {
            viewModel.replayOnboarding()
            onOnboardingReplayed()
        },
        onBackClick = onBackClick,
        modifier = modifier
    )
}
