package com.akash.kontactplus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.akash.kontactplus.feature.assistant.presentation.AssistantScreen
import com.akash.kontactplus.feature.contacts.presentation.ContactsRoute
import com.akash.kontactplus.feature.dialpad.presentation.DialpadScreen
import com.akash.kontactplus.feature.favourites.presentation.FavouritesScreen
import com.akash.kontactplus.feature.recents.presentation.RecentsScreen

@Composable
fun KontactNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = KontactDestination.Contacts.route,
        modifier = modifier,
    ) {
        composable(route = KontactDestination.Favourites.route) {
            FavouritesScreen()
        }
        
        composable(route = KontactDestination.Recents.route) {
            RecentsScreen()
        }
        
        composable(route = KontactDestination.Contacts.route) {
            ContactsRoute()
        }
        
        composable(route = KontactDestination.Dialpad.route) {
            DialpadScreen()
        }
        
        composable(route = KontactDestination.Assistant.route) {
            AssistantScreen()
        }
    }
}
