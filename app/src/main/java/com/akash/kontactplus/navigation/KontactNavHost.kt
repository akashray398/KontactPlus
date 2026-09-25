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
import com.akash.kontactplus.feature.settings.presentation.InsightsSettingsRoute
import com.akash.kontactplus.feature.settings.presentation.PrivacySettingsRoute
import com.akash.kontactplus.feature.settings.presentation.SettingsRoute
import com.akash.kontactplus.feature.onboarding.presentation.OnboardingRoute

@Composable
fun KontactNavHost(
    navController: NavHostController,
    telecomRoleManager: TelecomRoleManager,
    aiRepository: com.akash.kontactplus.feature.ai.domain.repository.AiRepository,
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
                telecomRoleManager = telecomRoleManager,
                aiRepository = aiRepository,
                onContactClick = { lookupKey ->
                    val encodedKey = Uri.encode(lookupKey)
                    navController.navigate("contact/$encodedKey")
                },
                onNavigateToDialpad = {
                    navController.navigate(KontactDestination.Dialpad.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToRecents = {
                    navController.navigate(KontactDestination.Recents.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToAi = {
                    navController.navigate("ai_tools")
                },
                onNavigateToSettings = {
                    navController.navigate("settings/insights")
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
                },
                onSettingsClick = {
                    navController.navigate("settings")
                },
                onDraftMessage = { action, instruction ->
                    val encodedInstruction = Uri.encode(instruction)
                    navController.navigate("ai_tools?action=$action&instruction=$encodedInstruction")
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
                },
                onNavigateToDialpad = { number ->
                    val encodedNumber = Uri.encode(number)
                    navController.navigate("dialpad?number=$encodedNumber")
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

        composable(
            route = "ai_tools?action={action}&instruction={instruction}",
            arguments = listOf(
                navArgument("action") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("instruction") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) {
            AiFlowRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = "settings") {
            SettingsRoute(
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToInsights = { navController.navigate("settings/insights") },
                onNavigateToPrivacy = { navController.navigate("settings/privacy") },
                onNavigateToAiPrivacy = { navController.navigate("settings/privacy") },
                onOnboardingReplayed = { navController.navigate("onboarding") },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = "profile") {
            com.akash.kontactplus.feature.profile.presentation.UserProfileRoute(
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate("settings") },
                onNavigateToAiTools = { navController.navigate("ai_tools") },
                onNavigateToInsights = { navController.navigate("settings/insights") },
                onNavigateToPrivacy = { navController.navigate("settings/privacy") }
            )
        }

        composable(route = "settings/insights") {
            InsightsSettingsRoute(
                onPrivacyCenterClick = {
                    navController.navigate("settings/privacy")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = "settings/privacy") {
            PrivacySettingsRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = "onboarding") {
            OnboardingRoute(
                onFinish = { navController.popBackStack() },
                onPrivacyClick = { /* Show privacy dialog */ }
            )
        }
    }
}
