package com.akash.kontactplus.feature.ai.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.akash.kontactplus.R

@Composable
fun AiPrivacyDisclosureDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.ai_disclosure_title)) },
        text = {
            Text(
                stringResource(R.string.ai_disclosure_data_sent) + "\n\n" +
                stringResource(R.string.ai_disclosure_provider) + "\n\n" +
                stringResource(R.string.ai_disclosure_no_automatic_send)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.ai_disclosure_understand))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ai_cancel))
            }
        }
    )
}
