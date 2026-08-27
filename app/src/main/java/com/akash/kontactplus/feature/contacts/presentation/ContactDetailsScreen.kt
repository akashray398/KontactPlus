package com.akash.kontactplus.feature.contacts.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            }
        )

        when (uiState) {
            ContactDetailsUiState.Loading -> {
                LoadingState()
            }
            is ContactDetailsUiState.Success -> {
                SuccessState(
                    contact = uiState.contact,
                    onPhoneNumberClick = onPhoneNumberClick
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
    onPhoneNumberClick: (String) -> Unit
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
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactDetailsLoadingPreview() {
    KontactPlusTheme {
        ContactDetailsScreen(
            uiState = ContactDetailsUiState.Loading,
            onBackClick = {},
            onPhoneNumberClick = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactDetailsNotFoundPreview() {
    KontactPlusTheme(darkTheme = true) {
        ContactDetailsScreen(
            uiState = ContactDetailsUiState.NotFound,
            onBackClick = {},
            onPhoneNumberClick = {},
            onRetry = {}
        )
    }
}
