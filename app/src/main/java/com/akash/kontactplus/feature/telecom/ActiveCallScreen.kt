package com.akash.kontactplus.feature.telecom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.ContactAvatar
import com.akash.kontactplus.core.designsystem.theme.ContactAvatarLarge
import com.akash.kontactplus.core.designsystem.theme.SpaceLarge
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium
import com.akash.kontactplus.core.telecom.ActiveCallInfo
import com.akash.kontactplus.core.telecom.ActiveCallState

@Composable
fun ActiveCallScreen(
    callInfo: ActiveCallInfo,
    onDisconnect: () -> Unit,
    onHold: () -> Unit,
    onUnhold: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SpaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 100.dp)
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
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 64.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = { if (callInfo.state == ActiveCallState.OnHold) onUnhold() else onHold() },
                    enabled = callInfo.canHold || callInfo.canUnhold
                ) {
                    Icon(
                        imageVector = if (callInfo.state == ActiveCallState.OnHold) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = stringResource(
                            if (callInfo.state == ActiveCallState.OnHold) R.string.active_call_resume else R.string.active_call_hold
                        )
                    )
                }

                IconButton(onClick = {}, enabled = callInfo.canMute) {
                    Icon(
                        imageVector = if (callInfo.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = stringResource(
                            if (callInfo.isMuted) R.string.active_call_unmute else R.string.active_call_mute
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpaceLarge))

            IconButton(
                onClick = onDisconnect,
                modifier = Modifier.size(72.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = stringResource(R.string.active_call_end),
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun mapStateToLabel(state: ActiveCallState): String {
    return when (state) {
        ActiveCallState.Dialling -> stringResource(R.string.active_call_dialling)
        ActiveCallState.Connecting -> stringResource(R.string.active_call_connecting)
        ActiveCallState.Active -> stringResource(R.string.active_call_active)
        ActiveCallState.OnHold -> stringResource(R.string.active_call_on_hold)
        else -> ""
    }
}
