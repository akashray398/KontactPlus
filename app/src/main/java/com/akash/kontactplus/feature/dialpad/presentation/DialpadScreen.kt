package com.akash.kontactplus.feature.dialpad.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.ContactAvatar
import com.akash.kontactplus.core.designsystem.component.KontactCard
import com.akash.kontactplus.core.designsystem.component.KontactPrimaryButton
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme
import com.akash.kontactplus.core.designsystem.theme.SpaceLarge
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium
import com.akash.kontactplus.core.designsystem.theme.SpaceSmall
import com.akash.kontactplus.feature.dialpad.domain.model.DialpadKey
import com.akash.kontactplus.feature.dialpad.domain.model.DialpadSuggestion

@Composable
fun DialpadScreen(
    uiState: DialpadUiState,
    onKeyPressed: (DialpadKey) -> Unit,
    onZeroLongPressed: () -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    onPaste: () -> Unit,
    onSuggestionClick: (String) -> Unit,
    onCallClick: () -> Unit,
    onRequestRole: () -> Unit,
    onRequestCallPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onCreateContact: () -> Unit,
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
        
        Text(
            text = stringResource(R.string.dialpad_title),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.fillMaxWidth()
        )

        when (uiState.accessState) {
            DialpadAccessState.Checking -> {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            DialpadAccessState.RoleUnsupported -> {
                InfoState(
                    icon = Icons.Default.Phone,
                    title = stringResource(R.string.dialpad_no_telecom_title),
                    description = stringResource(R.string.dialpad_no_telecom_description),
                    modifier = Modifier.weight(1f)
                )
            }
            DialpadAccessState.RoleRequired -> {
                InfoState(
                    icon = Icons.Default.Phone,
                    title = stringResource(R.string.dialpad_role_title),
                    description = stringResource(R.string.dialpad_role_description),
                    buttonLabel = stringResource(R.string.dialpad_choose_default),
                    onButtonClick = onRequestRole,
                    modifier = Modifier.weight(1f)
                )
            }
            DialpadAccessState.CallPermissionNotRequested -> {
                InfoState(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.dialpad_permission_title),
                    description = stringResource(R.string.dialpad_permission_description),
                    buttonLabel = stringResource(R.string.dialpad_permission_allow),
                    onButtonClick = onRequestCallPermission,
                    modifier = Modifier.weight(1f)
                )
            }
            DialpadAccessState.CallPermissionDenied -> {
                InfoState(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.dialpad_permission_denied_title),
                    description = stringResource(R.string.dialpad_permission_denied_description),
                    buttonLabel = stringResource(R.string.dialpad_permission_allow),
                    onButtonClick = onRequestCallPermission,
                    modifier = Modifier.weight(1f)
                )
            }
            DialpadAccessState.CallPermissionPermanentlyDenied -> {
                InfoState(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.dialpad_permission_denied_title),
                    description = stringResource(R.string.dialpad_permission_denied_description),
                    buttonLabel = stringResource(R.string.dialpad_permission_settings),
                    onButtonClick = onOpenSettings,
                    modifier = Modifier.weight(1f)
                )
            }
            DialpadAccessState.Ready -> {
                ReadyDialpadContent(
                    uiState = uiState,
                    onKeyPressed = onKeyPressed,
                    onZeroLongPressed = onZeroLongPressed,
                    onDelete = onDelete,
                    onClear = onClear,
                    onPaste = onPaste,
                    onSuggestionClick = onSuggestionClick,
                    onCallClick = onCallClick,
                    onCreateContact = onCreateContact,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ReadyDialpadContent(
    uiState: DialpadUiState,
    onKeyPressed: (DialpadKey) -> Unit,
    onZeroLongPressed: () -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    onPaste: () -> Unit,
    onSuggestionClick: (String) -> Unit,
    onCallClick: () -> Unit,
    onCreateContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Number Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(vertical = SpaceMedium),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = uiState.formattedDisplayNumber.ifBlank { stringResource(R.string.dialpad_enter_number) },
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = if (uiState.dialableNumber.length > 10) 32.sp else 44.sp,
                        textAlign = TextAlign.Center
                    ),
                    color = if (uiState.dialableNumber.isEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
                
                if (uiState.dialableNumber.isNotEmpty()) {
                    TextButton(onClick = onCreateContact) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.padding(4.dp))
                            Text(text = stringResource(R.string.dialpad_create_contact))
                        }
                    }
                }
            }
        }

        // Suggestions
        Box(modifier = Modifier.height(100.dp)) {
            if (uiState.isLoadingSuggestions) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp).align(Alignment.Center))
            } else if (uiState.suggestions.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = SpaceSmall),
                    horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items = uiState.suggestions, key = { it.lookupKey + it.phoneNumber }) { suggestion ->
                        SuggestionItem(
                            suggestion = suggestion,
                            onClick = { onSuggestionClick(suggestion.phoneNumber) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Grid
        Column(
            modifier = Modifier.padding(bottom = SpaceLarge),
            verticalArrangement = Arrangement.spacedBy(SpaceMedium)
        ) {
            val keys = DialpadKey.keys
            for (i in 0 until 4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (j in 0 until 3) {
                        val key = keys[i * 3 + j]
                        DialpadButton(
                            primary = key.value,
                            letters = key.letters,
                            onClick = { onKeyPressed(key) },
                            onLongClick = if (key is DialpadKey.Zero) onZeroLongPressed else null
                        )
                    }
                }
            }
        }

        // Actions: Paste, Call, Delete
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = SpaceLarge),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CombinedIconButton(onClick = onPaste) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = stringResource(R.string.dialpad_paste)
                )
            }

            CombinedIconButton(
                onClick = onCallClick,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFF22C55E), // SuccessGreen
                    contentColor = Color.White
                ),
                enabled = uiState.dialableNumber.isNotBlank() && !uiState.isPlacingCall
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = stringResource(R.string.dialpad_call),
                    modifier = Modifier.size(32.dp)
                )
            }

            CombinedIconButton(
                onClick = onDelete,
                onLongClick = onClear
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = stringResource(R.string.dialpad_delete)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CombinedIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: androidx.compose.material3.IconButtonColors = IconButtonDefaults.iconButtonColors(),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (enabled) colors.containerColor else colors.disabledContainerColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                enabled = enabled
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun SuggestionItem(
    suggestion: DialpadSuggestion,
    onClick: () -> Unit
) {
    KontactCard(
        onClick = onClick,
        modifier = Modifier.width(160.dp)
    ) {
        Row(
            modifier = Modifier.padding(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(displayName = suggestion.displayName, size = 32.dp)
            Spacer(modifier = Modifier.padding(4.dp))
            Column {
                Text(
                    text = suggestion.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = suggestion.phoneNumber,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun InfoState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    buttonLabel: String? = null,
    onButtonClick: (() -> Unit)? = null,
    privacyNote: String? = null
) {
    Column(
        modifier = modifier.fillMaxSize(),
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

@Preview(showBackground = true)
@Composable
private fun DialpadScreenReadyPreview() {
    KontactPlusTheme {
        DialpadScreen(
            uiState = DialpadUiState(
                accessState = DialpadAccessState.Ready,
                dialableNumber = "123456789",
                formattedDisplayNumber = "123-456-789"
            ),
            onKeyPressed = {},
            onZeroLongPressed = {},
            onDelete = {},
            onClear = {},
            onPaste = {},
            onSuggestionClick = {},
            onCallClick = {},
            onRequestRole = {},
            onRequestCallPermission = {},
            onOpenSettings = {},
            onCreateContact = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DialpadScreenRoleRequiredPreview() {
    KontactPlusTheme(darkTheme = true) {
        DialpadScreen(
            uiState = DialpadUiState(accessState = DialpadAccessState.RoleRequired),
            onKeyPressed = {},
            onZeroLongPressed = {},
            onDelete = {},
            onClear = {},
            onPaste = {},
            onSuggestionClick = {},
            onCallClick = {},
            onRequestRole = {},
            onRequestCallPermission = {},
            onOpenSettings = {},
            onCreateContact = {}
        )
    }
}
