package com.akash.kontactplus.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Dialpad
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.akash.kontactplus.R

sealed class KontactDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    data object Favourites : KontactDestination(
        route = "favourites",
        labelRes = R.string.bottom_favourites,
        selectedIcon = Icons.Filled.Star,
        unselectedIcon = Icons.Outlined.StarOutline,
    )

    data object Recents : KontactDestination(
        route = "recents",
        labelRes = R.string.bottom_recents,
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History,
    )

    data object Contacts : KontactDestination(
        route = "contacts",
        labelRes = R.string.bottom_contacts,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.PersonOutline,
    )

    data object Dialpad : KontactDestination(
        route = "dialpad",
        labelRes = R.string.bottom_keypad,
        selectedIcon = Icons.Filled.Dialpad,
        unselectedIcon = Icons.Outlined.Dialpad,
    )

    data object Assistant : KontactDestination(
        route = "assistant",
        labelRes = R.string.bottom_assistant,
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome,
    )

    companion object {
        val topLevelDestinations = listOf(
            Favourites,
            Recents,
            Contacts,
            Dialpad,
            Assistant,
        )
    }
}
