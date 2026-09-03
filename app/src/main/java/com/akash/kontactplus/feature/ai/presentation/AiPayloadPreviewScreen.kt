package com.akash.kontactplus.feature.ai.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.KontactPrimaryButton
import com.akash.kontactplus.feature.ai.domain.model.AiDraftContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiPayloadPreviewScreen(
    draftContext: AiDraftContext,
    onGenerate: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ai_payload_preview_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.ai_payload_preview_description),
                style = MaterialTheme.typography.bodyMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Transmission Data", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    DataField(label = "Action", value = draftContext.actionType.name)
                    DataField(label = "Tone", value = draftContext.tone.name)
                    DataField(label = "Instruction", value = draftContext.userInstruction)
                    DataField(label = "Alias", value = draftContext.contactAlias)
                    
                    if (draftContext.selectedText.isNotBlank()) {
                        DataField(label = "Reference Text", value = draftContext.selectedText)
                    }
                    if (draftContext.relationshipContext != null) {
                        DataField(label = "Context", value = draftContext.relationshipContext)
                    }
                }
            }

            Text(
                text = stringResource(R.string.ai_only_text_shown_sent),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.weight(1f))

            KontactPrimaryButton(
                onClick = onGenerate,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.ai_generate))
            }
        }
    }
}

@Composable
private fun DataField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
