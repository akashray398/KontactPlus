package com.akash.kontactplus.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.akash.kontactplus.core.telecom.DialIntentHandler
import com.akash.kontactplus.core.telecom.TelecomRoleManager
import com.akash.kontactplus.navigation.KontactBottomBar
import com.akash.kontactplus.navigation.KontactDestination
import com.akash.kontactplus.navigation.KontactNavHost
import kotlinx.coroutines.flow.collectLatest

@Composable
fun KontactPlusApp(
    telecomRoleManager: TelecomRoleManager,
    dialIntentHandler: DialIntentHandler,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

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
            modifier = Modifier.padding(innerPadding)
        )
    }
}
