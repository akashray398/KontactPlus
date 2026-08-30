package com.akash.kontactplus.feature.contacts.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.ContactAvatar
import com.akash.kontactplus.core.designsystem.component.KontactCard
import com.akash.kontactplus.core.designsystem.component.KontactPrimaryButton
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme
import com.akash.kontactplus.core.designsystem.theme.SpaceLarge
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
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.padding(SpaceSmall))
                    Text(text = stringResource(R.string.contacts_permission_checking))
                }
            }

            ContactsPermissionState.NotRequested -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PermissionContent(
                        title = stringResource(R.string.contacts_permission_title),
                        description = stringResource(R.string.contacts_permission_description),
                        privacyNote = stringResource(R.string.contacts_permission_privacy_note),
                        buttonLabel = stringResource(R.string.contacts_permission_allow),
                        onButtonClick = onRequestPermission
                    )
                }
            }

            ContactsPermissionState.Denied -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PermissionContent(
                        title = stringResource(R.string.contacts_permission_denied_title),
                        description = stringResource(R.string.contacts_permission_denied_description),
                        buttonLabel = stringResource(R.string.contacts_permission_retry),
                        onButtonClick = onRequestPermission
                    )
                }
            }

            ContactsPermissionState.PermanentlyDenied -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PermissionContent(
                        title = stringResource(R.string.contacts_permission_permanently_denied_title),
                        description = stringResource(R.string.contacts_permission_permanently_denied_description),
                        buttonLabel = stringResource(R.string.contacts_permission_open_settings),
                        onButtonClick = onOpenSettings
                    )
                }
            }

            ContactsPermissionState.Granted -> {
                GrantedContent(
                    uiState = uiState,
                    onRetryLoading = onRetryLoading,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onClearSearch = onClearSearch,
                    onSortOrderChanged = onSortOrderChanged,
                    onContactClick = onContactClick
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
    onContactClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.padding(SpaceSmall))
        Text(
            text = stringResource(R.string.title_contacts),
            style = MaterialTheme.typography.headlineLarge
        )
        
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
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.padding(SpaceSmall))
                    Text(text = stringResource(R.string.contacts_loading))
                }
            }
            uiState.errorMessageRes != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    InfoContent(
                        icon = Icons.Default.ErrorOutline,
                        title = stringResource(R.string.contacts_load_error_title),
                        description = stringResource(uiState.errorMessageRes),
                        buttonLabel = stringResource(R.string.contacts_retry),
                        onButtonClick = onRetryLoading
                    )
                }
            }
            uiState.visibleContacts.isEmpty() && uiState.searchQuery.isNotBlank() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    InfoContent(
                        icon = Icons.Default.SearchOff,
                        title = stringResource(R.string.contacts_no_results_title),
                        description = stringResource(R.string.contacts_no_results_description),
                        buttonLabel = stringResource(R.string.contacts_clear_search_action),
                        onButtonClick = onClearSearch
                    )
                }
            }
            uiState.visibleContacts.isEmpty() && uiState.hasLoadedContacts -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    InfoContent(
                        icon = Icons.Default.ContactPage,
                        title = stringResource(R.string.contacts_empty_title),
                        description = stringResource(R.string.contacts_empty_description)
                    )
                }
            }
            else -> {
                Text(
                    text = pluralStringResource(
                        R.plurals.contacts_count,
                        uiState.visibleContacts.size,
                        uiState.visibleContacts.size
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = SpaceMedium),
                    verticalArrangement = Arrangement.spacedBy(SpaceSmall)
                ) {
                    items(
                        items = uiState.visibleContacts,
                        key = { "${it.lookupKey}_${it.id}" }
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

@Composable
private fun PermissionContent(
    title: String,
    description: String,
    buttonLabel: String,
    onButtonClick: () -> Unit,
    privacyNote: String? = null
) {
    InfoContent(
        icon = Icons.Default.Lock,
        title = title,
        description = description,
        privacyNote = privacyNote,
        buttonLabel = buttonLabel,
        onButtonClick = onButtonClick
    )
}

@Composable
private fun InfoContent(
    icon: ImageVector,
    title: String,
    description: String,
    buttonLabel: String? = null,
    onButtonClick: (() -> Unit)? = null,
    privacyNote: String? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.padding(SpaceMedium))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(SpaceSmall))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (privacyNote != null) {
            Spacer(modifier = Modifier.padding(SpaceSmall))
            Text(
                text = privacyNote,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline
            )
        }
        if (buttonLabel != null && (onButtonClick != null)) {
            Spacer(modifier = Modifier.padding(SpaceLarge))
            KontactPrimaryButton(
                onClick = onButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = buttonLabel)
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
            onContactClick = {}
        )
    }
}
