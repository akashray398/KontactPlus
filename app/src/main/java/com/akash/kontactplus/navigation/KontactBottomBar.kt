package com.akash.kontactplus.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme

@Composable
fun KontactBottomBar(
    currentDestination: KontactDestination,
    onDestinationSelected: (KontactDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        KontactDestination.topLevelDestinations.forEach { destination ->
            val isSelected = currentDestination == destination
            
            NavigationBarItem(
                selected = isSelected,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = stringResource(destination.labelRes)
                    )
                },
                label = {
                    Text(text = stringResource(destination.labelRes))
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Preview
@Composable
private fun KontactBottomBarPreview() {
    KontactPlusTheme {
        KontactBottomBar(
            currentDestination = KontactDestination.Contacts,
            onDestinationSelected = {}
        )
    }
}

@Preview
@Composable
private fun KontactBottomBarDarkPreview() {
    KontactPlusTheme(darkTheme = true) {
        KontactBottomBar(
            currentDestination = KontactDestination.Favourites,
            onDestinationSelected = {}
        )
    }
}
