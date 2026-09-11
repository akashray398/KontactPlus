package com.akash.kontactplus.feature.dialpad.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.*
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
            modifier = Modifier.fillMaxWidth().semantics { heading() }
        )

        when (uiState.accessState) {
            DialpadAccessState.Checking -> {
                KontactLoadingState()
            }
            DialpadAccessState.RoleUnsupported -> {
                KontactEmptyState(
                    icon = Icons.Default.Phone,
                    title = stringResource(R.string.dialpad_no_telecom_title),
                    description = stringResource(R.string.dialpad_no_telecom_description),
                    modifier = Modifier.weight(1f)
                )
            }
            DialpadAccessState.RoleRequired -> {
                KontactEmptyState(
                    icon = Icons.Default.Phone,
                    title = stringResource(R.string.dialpad_role_title),
                    description = stringResource(R.string.dialpad_role_description),
                    action = {
                        KontactPrimaryButton(onClick = onRequestRole, modifier = Modifier.fillMaxWidth()) {
                            Text(text = stringResource(R.string.dialpad_choose_default))
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            DialpadAccessState.CallPermissionNotRequested -> {
                KontactEmptyState(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.dialpad_permission_title),
                    description = stringResource(R.string.dialpad_permission_description),
                    action = {
                        KontactPrimaryButton(onClick = onRequestCallPermission, modifier = Modifier.fillMaxWidth()) {
                            Text(text = stringResource(R.string.dialpad_permission_allow))
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            DialpadAccessState.CallPermissionDenied -> {
                KontactEmptyState(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.dialpad_permission_denied_title),
                    description = stringResource(R.string.dialpad_permission_denied_description),
                    action = {
                        KontactPrimaryButton(onClick = onRequestCallPermission, modifier = Modifier.fillMaxWidth()) {
                            Text(text = stringResource(R.string.dialpad_permission_allow))
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            DialpadAccessState.CallPermissionPermanentlyDenied -> {
                KontactEmptyState(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.dialpad_permission_denied_title),
                    description = stringResource(R.string.dialpad_permission_denied_description),
                    action = {
                        KontactPrimaryButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
                            Text(text = stringResource(R.string.dialpad_permission_settings))
                        }
                    },
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
                .heightIn(min = 100.dp)
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
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.semantics {
                        contentDescription = if (uiState.dialableNumber.isEmpty()) {
                            "Number entry field, empty"
                        } else {
                            "Entered number: ${uiState.dialableNumber}"
                        }
                    }
                )
                
                if (uiState.dialableNumber.isNotEmpty()) {
                    TextButton(
                        onClick = onCreateContact,
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
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
                    modifier = Modifier.fillMaxWidth().semantics { 
                        contentDescription = "Contact suggestions"
                    }
                ) {
                    items(
                        items = uiState.suggestions, 
                        key = { it.lookupKey + it.phoneNumber }
                    ) { suggestion ->
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
            modifier = Modifier.padding(bottom = SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
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
            CombinedIconButton(
                onClick = onPaste,
                modifier = Modifier.semantics { contentDescription = "Paste number" }
            ) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = null
                )
            }

            CombinedIconButton(
                onClick = onCallClick,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .semantics { 
                        contentDescription = "Place call"
                        role = Role.Button
                    },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFF22C55E), // SuccessGreen
                    contentColor = Color.White
                ),
                enabled = uiState.dialableNumber.isNotBlank() && !uiState.isPlacingCall
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }

            CombinedIconButton(
                onClick = onDelete,
                onLongClick = onClear,
                modifier = Modifier.semantics { contentDescription = "Delete last digit" }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = null
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
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
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
            Spacer(modifier = Modifier.width(8.dp))
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
