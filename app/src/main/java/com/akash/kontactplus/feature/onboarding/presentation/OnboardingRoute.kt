package com.akash.kontactplus.feature.onboarding.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OnboardingRoute(
    onFinish: () -> Unit,
    onPrivacyClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OnboardingScreen(
        onFinish = {
            viewModel.onOnboardingFinished()
            onFinish()
        },
        onPrivacyClick = onPrivacyClick,
        modifier = modifier
    )
}
