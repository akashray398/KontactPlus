package com.akash.kontactplus.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.akash.kontactplus.core.telecom.DialIntentHandler
import com.akash.kontactplus.core.telecom.TelecomRoleManager
import com.akash.kontactplus.feature.ai.domain.repository.AiRepository
import com.akash.kontactplus.feature.onboarding.presentation.OnboardingRoute
import com.akash.kontactplus.feature.onboarding.presentation.OnboardingViewModel
import com.akash.kontactplus.navigation.KontactBottomBar
import com.akash.kontactplus.navigation.KontactDestination
import com.akash.kontactplus.navigation.KontactNavHost
import kotlinx.coroutines.flow.collectLatest

@Composable
fun KontactPlusApp(
    telecomRoleManager: TelecomRoleManager,
    dialIntentHandler: DialIntentHandler,
    aiRepository: AiRepository,
    modifier: Modifier = Modifier,
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    val onboardingState by onboardingViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    if (onboardingState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (!onboardingState.isCompleted) {
        OnboardingRoute(
            onFinish = { onboardingViewModel.onOnboardingFinished() },
            onPrivacyClick = { 
                // We could navigate to a detailed privacy explanation or just a dialog
            }
        )
        return
    }

    LaunchedEffect(Unit) {
        dialIntentHandler.dialNumber.collectLatest { number ->
            val route = if (number != null) {
                "dialpad?number=$number"
            } else {
                KontactDestination.Dialpad.route
            }
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentTopLevelDestination = KontactDestination.topLevelDestinations.find { destination ->
        currentDestination?.hierarchy?.any { it.route == destination.route } == true
    } ?: KontactDestination.Contacts

    val shouldShowBottomBar = remember(currentDestination) {
        KontactDestination.topLevelDestinations.any { destination ->
            currentDestination?.hierarchy?.any { it.route == destination.route } == true
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomBar) {
                KontactBottomBar(
                    currentDestination = currentTopLevelDestination,
                    onDestinationSelected = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        KontactNavHost(
            navController = navController,
            telecomRoleManager = telecomRoleManager,
            aiRepository = aiRepository,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
