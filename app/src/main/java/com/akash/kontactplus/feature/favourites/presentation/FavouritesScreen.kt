package com.akash.kontactplus.feature.favourites.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.*
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium
import com.akash.kontactplus.core.designsystem.theme.SpaceSmall
import com.akash.kontactplus.feature.contacts.domain.model.Contact

@Composable
fun FavouritesScreen(
    uiState: FavouritesUiState,
    onContactClick: (String) -> Unit,
    onOpenContacts: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = SpaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(SpaceSmall))
        
        Text(
            text = stringResource(R.string.favourites_title),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.fillMaxWidth().semantics { heading() }
        )

        when {
            !uiState.hasContactsPermission -> {
                KontactEmptyState(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.favourites_permission_title),
                    description = stringResource(R.string.favourites_permission_description),
                    action = {
                        KontactPrimaryButton(onClick = onOpenContacts) {
                            Text(text = stringResource(R.string.favourites_open_contacts))
                        }
                    }
                )
            }
            uiState.isLoading -> {
                KontactLoadingState()
            }
            uiState.errorMessageRes != null -> {
                KontactErrorState(
                    title = stringResource(R.string.favourites_error_title),
                    description = stringResource(uiState.errorMessageRes),
                    onRetry = onRetry
                )
            }
            uiState.contacts.isEmpty() -> {
                KontactEmptyState(
                    icon = Icons.Outlined.StarOutline,
                    title = stringResource(R.string.favourites_empty_title),
                    description = stringResource(R.string.favourites_empty_description),
                    action = {
                        KontactPrimaryButton(onClick = onOpenContacts) {
                            Text(text = stringResource(R.string.favourites_browse_contacts))
                        }
                    }
                )
            }
            else -> {
                FavouritesList(
                    contacts = uiState.contacts,
                    onContactClick = onContactClick
                )
            }
        }
    }
}

@Composable
private fun FavouritesList(
    contacts: List<Contact>,
    onContactClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = pluralStringResource(
                R.plurals.favourites_count,
                contacts.size,
                contacts.size
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = SpaceSmall)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {
            items(
                items = contacts,
                key = { it.lookupKey }
            ) { contact ->
                FavouriteListItem(
                    contact = contact,
                    onClick = { onContactClick(contact.lookupKey) }
                )
            }
        }
    }
}

@Composable
private fun FavouriteListItem(
    contact: Contact,
    onClick: () -> Unit
) {
    KontactCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMedium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(
                displayName = contact.displayName.ifBlank { stringResource(R.string.contacts_unnamed) }
            )
            Spacer(modifier = Modifier.width(SpaceMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.displayName.ifBlank { stringResource(R.string.contacts_unnamed) },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (contact.phoneNumbers.isNotEmpty()) {
                    Text(
                        text = contact.phoneNumbers.first(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavouritesPopulatedPreview() {
    KontactPlusTheme {
        FavouritesScreen(
            uiState = FavouritesUiState(
                contacts = listOf(
                    Contact(1, "k1", "Akash Patel", listOf("123456789")),
                    Contact(2, "k2", "Jane Doe", listOf("987654321"))
                )
            ),
            onContactClick = {},
            onOpenContacts = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavouritesEmptyPreview() {
    KontactPlusTheme {
        FavouritesScreen(
            uiState = FavouritesUiState(),
            onContactClick = {},
            onOpenContacts = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavouritesNoPermissionPreview() {
    KontactPlusTheme(darkTheme = true) {
        FavouritesScreen(
            uiState = FavouritesUiState(hasContactsPermission = false),
            onContactClick = {},
            onOpenContacts = {},
            onRetry = {}
        )
    }
}
