package com.akash.kontactplus.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.akash.kontactplus.core.telecom.TelecomRoleManager
import com.akash.kontactplus.feature.ai.presentation.AiFlowRoute
import com.akash.kontactplus.feature.assistant.presentation.AssistantRoute
import com.akash.kontactplus.feature.contacts.presentation.ContactDetailsRoute
import com.akash.kontactplus.feature.contacts.presentation.ContactsRoute
import com.akash.kontactplus.feature.dialpad.presentation.DialpadRoute
import com.akash.kontactplus.feature.favourites.presentation.FavouritesRoute
import com.akash.kontactplus.feature.recents.presentation.RecentsRoute
import com.akash.kontactplus.feature.relationship.presentation.ContactRelationshipRoute

@Composable
fun KontactNavHost(
    navController: NavHostController,
    telecomRoleManager: TelecomRoleManager,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = KontactDestination.Contacts.route,
        modifier = modifier,
    ) {
        composable(route = KontactDestination.Favourites.route) {
            FavouritesRoute(
                onContactClick = { lookupKey ->
                    val encodedKey = Uri.encode(lookupKey)
                    navController.navigate("contact/$encodedKey")
                },
                onOpenContacts = {
                    navController.navigate(KontactDestination.Contacts.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        
        composable(route = KontactDestination.Recents.route) {
            RecentsRoute(
                telecomRoleManager = telecomRoleManager,
                onContactClick = { lookupKey ->
                    val encodedKey = Uri.encode(lookupKey)
                    navController.navigate("contact/$encodedKey")
                }
            )
        }
        
        composable(route = KontactDestination.Contacts.route) {
            ContactsRoute(
                onContactClick = { lookupKey ->
                    val encodedKey = Uri.encode(lookupKey)
                    navController.navigate("contact/$encodedKey")
                }
            )
        }
        
        composable(
            route = "dialpad?number={number}",
            arguments = listOf(
                navArgument("number") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            DialpadRoute(
                telecomRoleManager = telecomRoleManager
            )
        }
        
        composable(route = KontactDestination.Assistant.route) {
            AssistantRoute(
                onContactClick = { lookupKey ->
                    val encodedKey = Uri.encode(lookupKey)
                    navController.navigate("contact/$encodedKey")
                },
                onAiToolsClick = {
                    navController.navigate("ai_tools")
                }
            )
        }

        composable(
            route = "contact/{lookupKey}",
            arguments = listOf(
                navArgument("lookupKey") { type = NavType.StringType }
            )
        ) {
            ContactDetailsRoute(
                onBackClick = { navController.popBackStack() },
                onManageRelationship = { lookupKey ->
                    val encodedKey = Uri.encode(lookupKey)
                    navController.navigate("contact/$encodedKey/relationship")
                }
            )
        }

        composable(
            route = "contact/{lookupKey}/relationship",
            arguments = listOf(
                navArgument("lookupKey") { type = NavType.StringType }
            )
        ) {
            ContactRelationshipRoute(
                onBackClick = { navController.popBackStack() },
                onAiToolsClick = {
                    navController.navigate("ai_tools")
                }
            )
        }

        composable(route = "ai_tools") {
            AiFlowRoute(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
