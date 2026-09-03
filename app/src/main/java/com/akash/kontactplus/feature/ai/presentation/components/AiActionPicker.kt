package com.akash.kontactplus.feature.ai.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R
import com.akash.kontactplus.feature.ai.domain.model.AiActionType

@Composable
fun AiActionPicker(
    onActionSelected: (AiActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.ai_tools_title),
            style = MaterialTheme.typography.titleMedium
        )
        
        AiActionItem(
            title = stringResource(R.string.ai_draft_follow_up),
            icon = Icons.Default.Chat,
            onClick = { onActionSelected(AiActionType.FollowUpDraft) }
        )
        AiActionItem(
            title = stringResource(R.string.ai_conversation_starters),
            icon = Icons.Default.Lightbulb,
            onClick = { onActionSelected(AiActionType.ConversationStarters) }
        )
        AiActionItem(
            title = stringResource(R.string.ai_important_date_greeting),
            icon = Icons.Default.Cake,
            onClick = { onActionSelected(AiActionType.ImportantDateGreeting) }
        )
        AiActionItem(
            title = stringResource(R.string.ai_rewrite),
            icon = Icons.Default.Edit,
            onClick = { onActionSelected(AiActionType.RewriteTone) }
        )
    }
}

@Composable
private fun AiActionItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
