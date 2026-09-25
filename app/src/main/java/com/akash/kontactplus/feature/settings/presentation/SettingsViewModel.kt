package com.akash.kontactplus.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.core.demo.DemoModeManager
import com.akash.kontactplus.core.designsystem.theme.ThemeMode
import com.akash.kontactplus.core.designsystem.theme.ThemePreferences
import com.akash.kontactplus.feature.onboarding.data.OnboardingPreferences
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themePreferences: ThemePreferences,
    private val demoModeManager: DemoModeManager,
    private val onboardingPreferences: OnboardingPreferences,
    private val relationshipRepository: RelationshipRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeMode.System
    )

    val isDemoModeEnabled = demoModeManager.isDemoModeEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferences.setThemeMode(mode)
        }
    }

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

    fun clearLocalRelationshipData() {
        viewModelScope.launch {
            relationshipRepository.deleteAllRelationshipData()
        }
    }
}
