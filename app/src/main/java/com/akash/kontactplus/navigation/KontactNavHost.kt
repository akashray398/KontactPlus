package com.akash.kontactplus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.akash.kontactplus.feature.contacts.presentation.ContactsScreen

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
        composable(
            route = KontactDestination.Contacts.route,
        ) {
            ContactsScreen()
        }
    }
}
