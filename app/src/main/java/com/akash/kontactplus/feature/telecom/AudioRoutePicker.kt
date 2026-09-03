package com.akash.kontactplus.feature.telecom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium
import com.akash.kontactplus.core.telecom.CallAudioEndpoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRoutePicker(
    currentEndpoint: CallAudioEndpoint,
    availableEndpoints: List<CallAudioEndpoint>,
    onEndpointSelected: (CallAudioEndpoint) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = SpaceMedium)
        ) {
            Text(
                text = stringResource(R.string.active_call_audio),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(SpaceMedium)
            )

            availableEndpoints.forEach { endpoint ->
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(mapEndpointToLabel(endpoint)))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = mapEndpointToIcon(endpoint),
                            contentDescription = null
                        )
                    },
                    trailingContent = {
                        if (endpoint == currentEndpoint) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier.clickable {
                        onEndpointSelected(endpoint)
                    }
                )
            }
        }
    }
}

private fun mapEndpointToIcon(endpoint: CallAudioEndpoint): ImageVector {
    return when (endpoint) {
        CallAudioEndpoint.Earpiece -> Icons.Default.Hearing
        CallAudioEndpoint.Speaker -> Icons.Default.VolumeUp
        CallAudioEndpoint.WiredHeadset -> Icons.Default.Headset
        CallAudioEndpoint.Bluetooth -> Icons.Default.Bluetooth
        else -> Icons.Default.Info
    }
}

private fun mapEndpointToLabel(endpoint: CallAudioEndpoint): Int {
    return when (endpoint) {
        CallAudioEndpoint.Earpiece -> R.string.active_call_earpiece
        CallAudioEndpoint.Speaker -> R.string.active_call_speaker
        CallAudioEndpoint.WiredHeadset -> R.string.active_call_wired_headset
        CallAudioEndpoint.Bluetooth -> R.string.active_call_bluetooth
        CallAudioEndpoint.Streaming -> R.string.active_call_streaming
        else -> R.string.active_call_unknown_audio_device
    }
}
