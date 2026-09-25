package com.akash.kontactplus.feature.contacts.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.*
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium
import com.akash.kontactplus.core.designsystem.theme.SpaceSmall
import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.model.ContactSortOrder

@Composable
fun ContactsScreen(
    uiState: ContactsUiState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onRetryLoading: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onClearSearch: () -> Unit,
    onSortOrderChanged: (ContactSortOrder) -> Unit,
    onContactClick: (String) -> Unit,
    onDismissSetupCard: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToDialpad: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = SpaceMedium)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        when (uiState.permissionState) {
            ContactsPermissionState.Checking -> {
                KontactLoadingState()
            }

            ContactsPermissionState.NotRequested -> {
                KontactEmptyState(
                    title = stringResource(R.string.contacts_permission_title),
                    description = stringResource(R.string.contacts_permission_description),
                    icon = Icons.Default.Lock,
                    action = {
                        KontactPrimaryButton(
                            onClick = onRequestPermission,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = stringResource(R.string.contacts_permission_allow))
                        }
                    }
                )
            }

            ContactsPermissionState.Denied -> {
                KontactEmptyState(
                    title = stringResource(R.string.contacts_permission_denied_title),
                    description = stringResource(R.string.contacts_permission_denied_description),
                    icon = Icons.Default.Lock,
                    action = {
                        KontactPrimaryButton(
                            onClick = onRequestPermission,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = stringResource(R.string.contacts_permission_retry))
                        }
                    }
                )
            }

            ContactsPermissionState.PermanentlyDenied -> {
                KontactEmptyState(
                    title = stringResource(R.string.contacts_permission_permanently_denied_title),
                    description = stringResource(R.string.contacts_permission_permanently_denied_description),
                    icon = Icons.Default.Lock,
                    action = {
                        KontactPrimaryButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = stringResource(R.string.contacts_permission_open_settings))
                        }
                    }
                )
            }

            ContactsPermissionState.Granted -> {
                GrantedContent(
                    uiState = uiState,
                    onRetryLoading = onRetryLoading,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onClearSearch = onClearSearch,
                    onSortOrderChanged = onSortOrderChanged,
                    onContactClick = onContactClick,
                    onDismissSetupCard = onDismissSetupCard,
                    onNavigateToProfile = onNavigateToProfile,
                    onNavigateToDialpad = onNavigateToDialpad,
                    onNavigateToRecents = onNavigateToRecents,
                    onNavigateToAi = onNavigateToAi,
                    onNavigateToSettings = onNavigateToSettings
                )
            }
        }
    }
}

@Composable
private fun GrantedContent(
    uiState: ContactsUiState,
    onRetryLoading: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onClearSearch: () -> Unit,
    onSortOrderChanged: (ContactSortOrder) -> Unit,
    onContactClick: (String) -> Unit,
    onDismissSetupCard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDialpad: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        KontactTopAppBar(
            title = stringResource(R.string.title_contacts),
            subtitle = "Connect Smarter • Remember Better",
            onProfileClick = onNavigateToProfile,
            onAiToolsClick = onNavigateToAi,
            onSettingsClick = onNavigateToSettings
        )
        
        if (uiState.showSetupCard) {
            Spacer(modifier = Modifier.height(SpaceMedium))
            SetupProgressCard(
                items = uiState.setupItems.map { item ->
                    item.copy(onClick = {
                        when (item.title) {
                            "Contacts access" -> onRetryLoading()
                            "Default Phone role" -> onNavigateToDialpad()
                            "Call history access" -> onNavigateToRecents()
                            "Notification permission" -> onNavigateToSettings()
                            "Optional AI choice" -> onNavigateToAi()
                        }
                    })
                },
                onDismiss = onDismissSetupCard
            )
        }

        if (uiState.duplicatePairs.isNotEmpty()) {
            Spacer(modifier = Modifier.height(SpaceSmall))
            KontactCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(SpaceMedium)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ContactPage,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                        Text(
                            text = "Contact Fusion (${uiState.duplicatePairs.size} duplicates detected)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(SpaceSmall))
                    val pair = uiState.duplicatePairs.first()
                    Text(
                        text = "Potential duplicate: '${pair.primaryContact.displayName}' & '${pair.duplicateContact.displayName}' (${pair.matchReason})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactsSearchBar(
                query = uiState.searchQuery,
                onQueryChanged = onSearchQueryChanged,
                onClearQuery = onClearSearch,
                modifier = Modifier.weight(1f)
            )
            ContactsSortMenu(
                selectedSortOrder = uiState.sortOrder,
                onSortOrderChanged = onSortOrderChanged
            )
        }

        when {
            uiState.isLoading -> {
                KontactLoadingState()
            }
            uiState.errorMessageRes != null -> {
                KontactErrorState(
                    title = stringResource(R.string.contacts_load_error_title),
                    description = stringResource(uiState.errorMessageRes),
                    onRetry = onRetryLoading
                )
            }
            uiState.visibleContacts.isEmpty() && uiState.searchQuery.isNotBlank() -> {
                KontactEmptyState(
                    title = stringResource(R.string.contacts_no_results_title),
                    description = stringResource(R.string.contacts_no_results_description),
                    icon = Icons.Default.SearchOff,
                    action = {
                        TextButton(onClick = onClearSearch) {
                            Text(stringResource(R.string.contacts_clear_search_action))
                        }
                    }
                )
            }
            uiState.visibleContacts.isEmpty() && uiState.hasLoadedContacts -> {
                KontactEmptyState(
                    title = stringResource(R.string.contacts_empty_title),
                    description = stringResource(R.string.contacts_empty_description),
                    icon = Icons.Default.ContactPage
                )
            }
            else -> {
                Text(
                    text = pluralStringResource(
                        R.plurals.contacts_count,
                        uiState.visibleContacts.size,
                        uiState.visibleContacts.size
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
                        items = uiState.visibleContacts,
                        key = { it.lookupKey }
                    ) { contact ->
                        ContactListItem(
                            contact = contact,
                            onClick = { onContactClick(contact.lookupKey) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactListItem(
    contact: Contact,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    KontactCard(
        modifier = modifier.fillMaxWidth(),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = contact.phoneNumbers.first(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (contact.phoneNumbers.size > 1) {
                            Text(
                                text = " " + stringResource(
                                    R.string.contacts_more_numbers,
                                    contact.phoneNumbers.size - 1
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

class ContactsPreviewParameterProvider : PreviewParameterProvider<ContactsUiState> {
    override val values = sequenceOf(
        ContactsUiState(permissionState = ContactsPermissionState.NotRequested),
        ContactsUiState(permissionState = ContactsPermissionState.Granted, isLoading = true),
        ContactsUiState(
            permissionState = ContactsPermissionState.Granted, 
            hasLoadedContacts = true,
            visibleContacts = listOf(
                Contact(1, "k1", "Akash Patel", listOf("1234567890", "0987654321")),
                Contact(2, "k2", "Jane Doe", listOf("9876543210")),
                Contact(3, "k3", "", listOf("1112223333"))
            )
        ),
        ContactsUiState(
            permissionState = ContactsPermissionState.Granted, 
            hasLoadedContacts = true,
            visibleContacts = emptyList()
        ),
        ContactsUiState(
            permissionState = ContactsPermissionState.Granted, 
            hasLoadedContacts = true,
            visibleContacts = emptyList(),
            searchQuery = "Unknown"
        ),
        ContactsUiState(
            permissionState = ContactsPermissionState.Granted, 
            errorMessageRes = R.string.contacts_load_error_description
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ContactsScreenPreview(
    @PreviewParameter(ContactsPreviewParameterProvider::class) state: ContactsUiState
) {
    KontactPlusTheme {
        ContactsScreen(
            uiState = state,
            onRequestPermission = {},
            onOpenSettings = {},
            onRetryLoading = {},
            onSearchQueryChanged = {},
            onClearSearch = {},
            onSortOrderChanged = {},
            onContactClick = {},
            onDismissSetupCard = {},
            onNavigateToDialpad = {},
            onNavigateToRecents = {},
            onNavigateToAi = {},
            onNavigateToSettings = {}
        )
    }
}
