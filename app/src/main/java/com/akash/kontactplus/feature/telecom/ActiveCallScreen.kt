package com.akash.kontactplus.feature.telecom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.ContactAvatar
import com.akash.kontactplus.core.designsystem.theme.ContactAvatarLarge
import com.akash.kontactplus.core.designsystem.theme.SpaceLarge
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium
import com.akash.kontactplus.core.telecom.ActiveCallState
import com.akash.kontactplus.core.telecom.CallAudioEndpoint

@Composable
fun ActiveCallScreen(
    uiState: ActiveCallUiState,
    onDisconnect: () -> Unit,
    onHold: () -> Unit,
    onUnhold: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleDtmf: () -> Unit,
    onShowAudioPicker: () -> Unit,
    onDtmfDigitPressed: (Char) -> Unit,
    onDtmfDigitReleased: () -> Unit,
    onAudioEndpointSelected: (CallAudioEndpoint) -> Unit,
    onDismissAudioPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val callInfo = uiState.callInfo

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 80.dp)
        ) {
            ContactAvatar(
                displayName = callInfo.displayName.ifBlank { callInfo.phoneNumber },
                size = ContactAvatarLarge
            )
            Spacer(modifier = Modifier.height(SpaceLarge))
            Text(
                text = callInfo.displayName.ifBlank { stringResource(R.string.recents_unknown_caller) },
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = callInfo.phoneNumber,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(SpaceMedium))
            Text(
                text = mapStateToLabel(callInfo.state),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            if (callInfo.state == ActiveCallState.Active || callInfo.state == ActiveCallState.OnHold) {
                Text(
                    text = uiState.durationText,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        if (uiState.isDtmfVisible) {
            DtmfKeypad(
                digits = uiState.dtmfDigits,
                onDigitPressed = onDtmfDigitPressed,
                onDigitReleased = onDtmfDigitReleased,
                onClose = onToggleDtmf
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ControlIconButton(
                        onClick = onToggleMute,
                        icon = if (callInfo.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        label = stringResource(if (callInfo.isMuted) R.string.active_call_unmute else R.string.active_call_mute),
                        enabled = callInfo.canMute,
                        selected = callInfo.isMuted
                    )

                    ControlIconButton(
                        onClick = onToggleDtmf,
                        icon = Icons.Default.Dialpad,
                        label = stringResource(R.string.active_call_keypad),
                        enabled = callInfo.canDtmf
                    )

                    ControlIconButton(
                        onClick = onShowAudioPicker,
                        icon = Icons.Default.VolumeUp,
                        label = stringResource(R.string.active_call_audio)
                    )

                    ControlIconButton(
                        onClick = { if (callInfo.state == ActiveCallState.OnHold) onUnhold() else onHold() },
                        icon = if (callInfo.state == ActiveCallState.OnHold) Icons.Default.PlayArrow else Icons.Default.Pause,
                        label = stringResource(if (callInfo.state == ActiveCallState.OnHold) R.string.active_call_resume else R.string.active_call_hold),
                        enabled = callInfo.canHold || callInfo.canUnhold,
                        selected = callInfo.state == ActiveCallState.OnHold
                    )
                }

                Spacer(modifier = Modifier.height(SpaceLarge))

                IconButton(
                    onClick = onDisconnect,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = stringResource(R.string.active_call_end),
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }

    if (uiState.isAudioRoutePickerVisible) {
        AudioRoutePicker(
            currentEndpoint = callInfo.currentEndpoint,
            availableEndpoints = callInfo.availableEndpoints,
            onEndpointSelected = onAudioEndpointSelected,
            onDismiss = onDismissAudioPicker
        )
    }
}

@Composable
private fun ControlIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    enabled: Boolean = true,
    selected: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else Color.Transparent
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun mapStateToLabel(state: ActiveCallState): String {
    return when (state) {
        ActiveCallState.Dialling -> stringResource(R.string.active_call_dialling)
        ActiveCallState.Connecting -> stringResource(R.string.active_call_connecting)
        ActiveCallState.Active -> stringResource(R.string.active_call_active)
        ActiveCallState.OnHold -> stringResource(R.string.active_call_on_hold)
        ActiveCallState.Disconnecting -> stringResource(R.string.active_call_disconnecting)
        ActiveCallState.Disconnected -> stringResource(R.string.active_call_ended)
        else -> ""
    }
}
