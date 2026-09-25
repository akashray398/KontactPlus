package com.akash.kontactplus.feature.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun UserProfileRoute(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigateToAiTools: () -> Unit,
    onNavigateToInsights: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UserProfileScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onEditProfileClick = {},
        onSettingsClick = onSettingsClick,
        onNavigateToAiTools = onNavigateToAiTools,
        onNavigateToInsights = onNavigateToInsights,
        onNavigateToPrivacy = onNavigateToPrivacy,
        onUpdateNameAndPhone = { name, phone ->
            viewModel.updateNameAndPhone(name, phone)
        },
        modifier = modifier
    )
}
