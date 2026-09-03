package com.akash.kontactplus.feature.contacts.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.ContactAvatar
import com.akash.kontactplus.core.designsystem.component.KontactCard
import com.akash.kontactplus.core.designsystem.component.KontactPrimaryButton
import com.akash.kontactplus.core.designsystem.theme.ContactAvatarLarge
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme
import com.akash.kontactplus.core.designsystem.theme.SpaceLarge
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium
import com.akash.kontactplus.core.designsystem.theme.SpaceSmall
import com.akash.kontactplus.feature.contacts.domain.model.Contact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(
    uiState: ContactDetailsUiState,
    onBackClick: () -> Unit,
    onPhoneNumberClick: (String) -> Unit,
    onFavouriteClick: () -> Unit,
    onManageRelationship: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.contact_details_title)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.contact_details_back)
                    )
                }
            },
            actions = {
                if (uiState is ContactDetailsUiState.Success) {
                    IconButton(
                        onClick = onFavouriteClick,
                        enabled = !uiState.isFavouriteActionInProgress
                    ) {
                        Icon(
                            imageVector = if (uiState.isFavourite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = stringResource(
                                if (uiState.isFavourite) R.string.favourite_remove else R.string.favourite_add
                            ),
                            tint = if (uiState.isFavourite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        )

        when (uiState) {
            ContactDetailsUiState.Loading -> {
                LoadingState()
            }
            is ContactDetailsUiState.Success -> {
                SuccessState(
                    contact = uiState.contact,
                    relationship = uiState.relationship,
                    onPhoneNumberClick = onPhoneNumberClick,
                    onManageRelationship = onManageRelationship
                )
            }
            ContactDetailsUiState.NotFound -> {
                InfoState(
                    title = stringResource(R.string.contact_details_not_found_title),
                    description = stringResource(R.string.contact_details_not_found_description),
                    buttonLabel = stringResource(R.string.contact_details_back),
                    onButtonClick = onBackClick
                )
            }
            is ContactDetailsUiState.Error -> {
                InfoState(
                    title = stringResource(R.string.contact_details_error_title),
                    description = stringResource(uiState.messageRes),
                    buttonLabel = stringResource(R.string.contact_details_retry),
                    onButtonClick = onRetry
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(SpaceMedium))
        Text(text = stringResource(R.string.contact_details_loading))
    }
}

@Composable
private fun SuccessState(
    contact: Contact,
    relationship: com.akash.kontactplus.feature.relationship.domain.model.ContactRelationship?,
    onPhoneNumberClick: (String) -> Unit,
    onManageRelationship: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(SpaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ContactAvatar(
            displayName = contact.displayName.ifBlank { stringResource(R.string.contacts_unnamed) },
            size = ContactAvatarLarge
        )
        
        Spacer(modifier = Modifier.height(SpaceLarge))
        
        Text(
            text = contact.displayName.ifBlank { stringResource(R.string.contacts_unnamed) },
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(SpaceLarge))

        // Relationship Summary
        KontactCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onManageRelationship
        ) {
            Column(modifier = Modifier.padding(SpaceMedium)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.relationship_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                }
                
                if (relationship?.privateNote?.isNotBlank() == true) {
                    Spacer(modifier = Modifier.height(SpaceSmall))
                    Text(
                        text = relationship.privateNote,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                if (relationship?.tags?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(SpaceSmall))
                    // Simple tags list
                    Text(
                        text = relationship.tags.joinToString { it.name },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(SpaceLarge))
        
        Text(
            text = stringResource(R.string.contact_details_phone_numbers),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(SpaceSmall))
        
        if (contact.phoneNumbers.isEmpty()) {
            Text(
                text = stringResource(R.string.contact_details_no_phone_numbers),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            contact.phoneNumbers.forEach { number ->
                PhoneNumberItem(
                    number = number,
                    onDialClick = { onPhoneNumberClick(number) }
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}

@Composable
private fun PhoneNumberItem(
    number: String,
    onDialClick: () -> Unit
) {
    KontactCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onDialClick
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMedium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.contact_details_open_dialer),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = stringResource(R.string.contact_details_open_dialer),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun InfoState(
    title: String,
    description: String,
    buttonLabel: String,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SpaceMedium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(SpaceLarge))
        KontactPrimaryButton(onClick = onButtonClick) {
            Text(text = buttonLabel)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactDetailsSuccessPreview() {
    KontactPlusTheme {
        ContactDetailsScreen(
            uiState = ContactDetailsUiState.Success(
                Contact(1, "k1", "Akash Patel", listOf("1234567890", "0987654321"))
            ),
            onBackClick = {},
            onPhoneNumberClick = {},
            onFavouriteClick = {},
            onManageRelationship = {},
            onRetry = {}
        )
    }
}
