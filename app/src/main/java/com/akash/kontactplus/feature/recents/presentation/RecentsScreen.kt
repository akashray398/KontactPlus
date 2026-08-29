package com.akash.kontactplus.feature.recents.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.ContactAvatar
import com.akash.kontactplus.core.designsystem.component.KontactCard
import com.akash.kontactplus.core.designsystem.component.KontactPrimaryButton
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme
import com.akash.kontactplus.core.designsystem.theme.SpaceLarge
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium
import com.akash.kontactplus.core.designsystem.theme.SpaceSmall
import com.akash.kontactplus.feature.contacts.presentation.ContactsSearchBar
import com.akash.kontactplus.feature.recents.domain.model.RecentCall
import com.akash.kontactplus.feature.recents.domain.model.RecentCallType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecentsScreen(
    uiState: RecentsUiState,
    onRequestRole: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onClearSearch: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onContactClick: (String) -> Unit,
    onRedial: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = SpaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(SpaceSmall))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.recents_title),
                style = MaterialTheme.typography.headlineLarge
            )
            if (uiState.accessState == RecentsAccessState.Ready) {
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.recents_refresh)
                    )
                }
            }
        }

        when (uiState.accessState) {
            RecentsAccessState.CheckingRole -> {
                FullScreenLoading()
            }
            RecentsAccessState.RoleUnsupported -> {
                InfoState(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.recents_role_unsupported_title),
                    description = stringResource(R.string.recents_role_unsupported_description)
                )
            }
            RecentsAccessState.RoleRequired -> {
                InfoState(
                    icon = Icons.Default.Phone,
                    title = stringResource(R.string.recents_default_role_title),
                    description = stringResource(R.string.recents_default_role_description),
                    privacyNote = stringResource(R.string.recents_default_role_privacy),
                    buttonLabel = stringResource(R.string.recents_choose_app),
                    onButtonClick = onRequestRole
                )
            }
            RecentsAccessState.CheckingPermission -> {
                FullScreenLoading()
            }
            RecentsAccessState.PermissionNotRequested -> {
                InfoState(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.recents_permission_title),
                    description = stringResource(R.string.recents_permission_description),
                    buttonLabel = stringResource(R.string.recents_permission_allow),
                    onButtonClick = onRequestPermission
                )
            }
            RecentsAccessState.PermissionDenied -> {
                InfoState(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.recents_permission_denied_title),
                    description = stringResource(R.string.recents_permission_denied_description),
                    buttonLabel = stringResource(R.string.recents_permission_retry),
                    onButtonClick = onRequestPermission
                )
            }
            RecentsAccessState.PermissionPermanentlyDenied -> {
                InfoState(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.recents_permission_denied_title),
                    description = stringResource(R.string.recents_permission_settings),
                    buttonLabel = stringResource(R.string.recents_permission_settings),
                    onButtonClick = onOpenSettings
                )
            }
            RecentsAccessState.Ready -> {
                ReadyContent(
                    uiState = uiState,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onClearSearch = onClearSearch,
                    onRetry = onRetry,
                    onContactClick = onContactClick,
                    onRedial = onRedial
                )
            }
        }
    }
}

@Composable
private fun ReadyContent(
    uiState: RecentsUiState,
    onSearchQueryChanged: (String) -> Unit,
    onClearSearch: () -> Unit,
    onRetry: () -> Unit,
    onContactClick: (String) -> Unit,
    onRedial: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ContactsSearchBar(
            query = uiState.searchQuery,
            onQueryChanged = onSearchQueryChanged,
            onClearQuery = onClearSearch
        )

        when {
            uiState.isLoading -> {
                FullScreenLoading(label = stringResource(R.string.recents_loading))
            }
            uiState.errorMessageRes != null -> {
                InfoState(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.recents_error_title),
                    description = stringResource(uiState.errorMessageRes),
                    buttonLabel = stringResource(R.string.contacts_retry),
                    onButtonClick = onRetry
                )
            }
            uiState.visibleCalls.isEmpty() && uiState.searchQuery.isNotBlank() -> {
                InfoState(
                    icon = Icons.Default.SearchOff,
                    title = stringResource(R.string.recents_no_results_title),
                    description = stringResource(R.string.recents_no_results_description),
                    buttonLabel = stringResource(R.string.recents_clear_search),
                    onButtonClick = onClearSearch
                )
            }
            uiState.visibleCalls.isEmpty() -> {
                InfoState(
                    icon = Icons.Default.History,
                    title = stringResource(R.string.recents_empty_title),
                    description = stringResource(R.string.recents_empty_description)
                )
            }
            else -> {
                RecentsList(
                    calls = uiState.visibleCalls,
                    onContactClick = onContactClick,
                    onRedial = onRedial
                )
            }
        }
    }
}

@Composable
private fun RecentsList(
    calls: List<RecentCall>,
    onContactClick: (String) -> Unit,
    onRedial: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = SpaceMedium),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall)
    ) {
        items(
            items = calls,
            key = { it.id }
        ) { call ->
            RecentCallItem(
                call = call,
                onClick = {
                    call.contactLookupKey?.let { onContactClick(it) }
                },
                onRedialClick = { onRedial(call.phoneNumber) }
            )
        }
    }
}

@Composable
private fun RecentCallItem(
    call: RecentCall,
    onClick: () -> Unit,
    onRedialClick: () -> Unit
) {
    KontactCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = if (call.contactLookupKey != null) onClick else null
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMedium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(
                displayName = call.resolvedDisplayName ?: call.cachedName ?: stringResource(R.string.recents_unknown_caller)
            )
            Spacer(modifier = Modifier.size(SpaceMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = call.resolvedDisplayName ?: call.cachedName ?: stringResource(R.string.recents_unknown_caller),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = mapCallTypeToIcon(call.type),
                        contentDescription = stringResource(mapCallTypeToString(call.type)),
                        modifier = Modifier.size(16.dp),
                        tint = if (call.type == RecentCallType.Missed || call.type == RecentCallType.Rejected) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = call.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.size(SpaceSmall))
                    Text(
                        text = formatTimestamp(call.timestampMillis),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            IconButton(onClick = onRedialClick) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = stringResource(R.string.recents_call_back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun mapCallTypeToIcon(type: RecentCallType): ImageVector {
    return when (type) {
        RecentCallType.Incoming -> Icons.Default.Phone
        RecentCallType.Outgoing -> Icons.Default.Call
        else -> Icons.Default.History
    }
}

private fun mapCallTypeToString(type: RecentCallType): Int {
    return when (type) {
        RecentCallType.Incoming -> R.string.call_incoming
        RecentCallType.Outgoing -> R.string.call_outgoing
        RecentCallType.Missed -> R.string.call_missed
        RecentCallType.Rejected -> R.string.call_rejected
        RecentCallType.Blocked -> R.string.call_blocked
        RecentCallType.Voicemail -> R.string.call_voicemail
        else -> R.string.call_unknown
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(date)
}

@Composable
private fun FullScreenLoading(label: String? = null) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
        if (label != null) {
            Spacer(modifier = Modifier.height(SpaceMedium))
            Text(text = label)
        }
    }
}

@Composable
private fun InfoState(
    icon: ImageVector,
    title: String,
    description: String,
    buttonLabel: String? = null,
    onButtonClick: (() -> Unit)? = null,
    privacyNote: String? = null
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(SpaceMedium))
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
        if (privacyNote != null) {
            Spacer(modifier = Modifier.height(SpaceSmall))
            Text(
                text = privacyNote,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline
            )
        }
        if (buttonLabel != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(SpaceLarge))
            KontactPrimaryButton(onClick = onButtonClick, modifier = Modifier.fillMaxWidth()) {
                Text(text = buttonLabel)
            }
        }
    }
}
