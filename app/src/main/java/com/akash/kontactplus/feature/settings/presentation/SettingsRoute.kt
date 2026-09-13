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
    val isDemoModeEnabled by viewModel.isDemoModeEnabled.collectAsStateWithLifecycle()

    SettingsScreen(
        isDemoModeEnabled = isDemoModeEnabled,
        onDemoModeToggle = viewModel::toggleDemoMode,
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
