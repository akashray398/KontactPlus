package com.akash.kontactplus.feature.contacts.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun ContactsScreen(
    uiState: ContactsUiState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(SpaceMedium)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (uiState.permissionState) {
            ContactsPermissionState.Checking -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.padding(SpaceSmall))
                Text(text = stringResource(R.string.contacts_permission_checking))
            }

            ContactsPermissionState.NotRequested -> {
                PermissionContent(
                    title = stringResource(R.string.contacts_permission_title),
                    description = stringResource(R.string.contacts_permission_description),
                    privacyNote = stringResource(R.string.contacts_permission_privacy_note),
                    buttonLabel = stringResource(R.string.contacts_permission_allow),
                    onButtonClick = onRequestPermission
                )
            }

            ContactsPermissionState.Denied -> {
                PermissionContent(
                    title = stringResource(R.string.contacts_permission_denied_title),
                    description = stringResource(R.string.contacts_permission_denied_description),
                    buttonLabel = stringResource(R.string.contacts_permission_retry),
                    onButtonClick = onRequestPermission
                )
            }

            ContactsPermissionState.PermanentlyDenied -> {
                PermissionContent(
                    title = stringResource(R.string.contacts_permission_permanently_denied_title),
                    description = stringResource(R.string.contacts_permission_permanently_denied_description),
                    buttonLabel = stringResource(R.string.contacts_permission_open_settings),
                    onButtonClick = onOpenSettings
                )
            }

            ContactsPermissionState.Granted -> {
                GrantedContent()
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
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
        Spacer(modifier = Modifier.padding(SpaceLarge))
        KontactPrimaryButton(
            onClick = onButtonClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = buttonLabel)
        }
    }
}

@Composable
private fun GrantedContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.title_contacts),
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = stringResource(R.string.tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.padding(SpaceLarge))

        KontactCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /* TODO */ }
        ) {
            Row(
                modifier = Modifier
                    .padding(SpaceMedium)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContactAvatar(displayName = stringResource(R.string.preview_contact_name))
                Spacer(modifier = Modifier.width(SpaceMedium))
                Text(
                    text = stringResource(R.string.preview_contact_name),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.padding(SpaceMedium))

        Text(
            text = stringResource(R.string.contacts_permission_granted),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = stringResource(R.string.contacts_placeholder_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.weight(1f))

        KontactPrimaryButton(
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.add_contact))
        }
        Spacer(modifier = Modifier.padding(SpaceSmall))
    }
}

class ContactsPermissionPreviewParameterProvider : PreviewParameterProvider<ContactsPermissionState> {
    override val values = sequenceOf(
        ContactsPermissionState.Checking,
        ContactsPermissionState.NotRequested,
        ContactsPermissionState.Denied,
        ContactsPermissionState.PermanentlyDenied,
        ContactsPermissionState.Granted
    )
}

@Preview(showBackground = true)
@Composable
private fun ContactsScreenPreview(
    @PreviewParameter(ContactsPermissionPreviewParameterProvider::class) state: ContactsPermissionState
) {
    KontactPlusTheme {
        ContactsScreen(
            uiState = ContactsUiState(permissionState = state),
            onRequestPermission = {},
            onOpenSettings = {}
        )
    }
}
