package com.akash.kontactplus.feature.contacts.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(SpaceMedium)
            .fillMaxSize(),
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
            onClick = { /* TODO: Navigate to details */ }
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
            text = stringResource(R.string.description_contacts),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.weight(1f))

        KontactPrimaryButton(
            onClick = { /* TODO: Add contact */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.add_contact))
        }
        
        Spacer(modifier = Modifier.padding(SpaceSmall))
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactsScreenLightPreview() {
    KontactPlusTheme(darkTheme = false) {
        ContactsScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactsScreenDarkPreview() {
    KontactPlusTheme(darkTheme = true) {
        ContactsScreen()
    }
}
