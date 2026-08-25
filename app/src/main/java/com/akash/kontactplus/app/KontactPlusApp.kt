package com.akash.kontactplus.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.akash.kontactplus.navigation.KontactBottomBar
import com.akash.kontactplus.navigation.KontactDestination
import com.akash.kontactplus.navigation.KontactNavHost

@Composable
fun KontactPlusApp(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentTopLevelDestination = KontactDestination.topLevelDestinations.find { destination ->
        currentDestination?.hierarchy?.any { it.route == destination.route } == true
    } ?: KontactDestination.Contacts

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            KontactBottomBar(
                currentDestination = currentTopLevelDestination,
                onDestinationSelected = { destination ->
                    navController.navigate(destination.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        KontactNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
