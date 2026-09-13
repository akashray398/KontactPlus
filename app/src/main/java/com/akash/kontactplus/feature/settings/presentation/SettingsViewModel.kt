package com.akash.kontactplus.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.core.demo.DemoModeManager
import com.akash.kontactplus.feature.onboarding.data.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val demoModeManager: DemoModeManager,
    private val onboardingPreferences: OnboardingPreferences
) : ViewModel() {

    val isDemoModeEnabled = demoModeManager.isDemoModeEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun toggleDemoMode(enabled: Boolean) {
        viewModelScope.launch {
            demoModeManager.setDemoModeEnabled(enabled)
        }
    }

    fun replayOnboarding() {
        viewModelScope.launch {
            onboardingPreferences.setOnboardingCompleted(false)
        }
    }
}
