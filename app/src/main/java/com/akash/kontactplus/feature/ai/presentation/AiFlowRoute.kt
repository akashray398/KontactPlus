package com.akash.kontactplus.feature.ai.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akash.kontactplus.feature.ai.domain.model.AiGenerationResult
import com.akash.kontactplus.feature.ai.presentation.components.AiActionPicker
import com.akash.kontactplus.feature.ai.presentation.components.AiPrivacyDisclosureDialog

@Composable
fun AiFlowRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AiViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showExitConfirmation by remember { mutableStateOf(false) }

    val handleBack = {
        if (uiState.step == AiFlowStep.Result && uiState.generationResult is AiGenerationResult.Success) {
            showExitConfirmation = true
        } else if (uiState.step != AiFlowStep.ActionPicker) {
            viewModel.reset()
        } else {
            onBackClick()
        }
    }

    BackHandler(onBack = handleBack)

    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = { showExitConfirmation = false },
            title = { Text("Discard Draft?") },
            text = { Text("Your generated AI draft will be lost.") },
            confirmButton = {
                TextButton(onClick = { 
                    showExitConfirmation = false
                    viewModel.reset()
                    onBackClick()
                }) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirmation = false }) {
                    Text("Keep")
                }
            }
        )
    }

    if (!uiState.hasAcceptedDisclosure) {
        AiPrivacyDisclosureDialog(
            onConfirm = { viewModel.onAcceptDisclosure() },
            onDismiss = onBackClick
        )
    }

    when (uiState.step) {
        AiFlowStep.ActionPicker -> {
            AiActionPicker(
                onActionSelected = { viewModel.onActionSelected(it) },
                modifier = modifier.padding(16.dp)
            )
        }
        AiFlowStep.Configure -> {
            AiConfigurationScreen(
                uiState = uiState,
                onToneSelected = viewModel::onToneSelected,
                onInstructionChanged = viewModel::onInstructionChanged,
                onPreviewPayload = viewModel::onPreviewPayload,
                onBackClick = handleBack
            )
        }
        AiFlowStep.Preview -> {
            uiState.draftContext?.let { draft ->
                AiPayloadPreviewScreen(
                    draftContext = draft,
                    onGenerate = viewModel::onGenerate,
                    onBackClick = handleBack
                )
            }
        }
        AiFlowStep.Result -> {
            uiState.generationResult?.let { result ->
                AiResultScreen(
                    result = result,
                    onBackClick = handleBack,
                    onCopyClick = { text -> context.copyToClipboard(text) },
                    onShareClick = { text -> context.shareText(text) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiConfigurationScreen(
    uiState: AiUiState,
    onToneSelected: (com.akash.kontactplus.feature.ai.domain.model.AiTone) -> Unit,
    onInstructionChanged: (String) -> Unit,
    onPreviewPayload: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configure AI") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Instruction", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = uiState.instructionInput,
                onValueChange = onInstructionChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Draft a short follow-up...") }
            )
            
            Text("Tone", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                com.akash.kontactplus.feature.ai.domain.model.AiTone.entries.forEach { tone ->
                    FilterChip(
                        selected = uiState.selectedTone == tone,
                        onClick = { onToneSelected(tone) },
                        label = { Text(tone.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onPreviewPayload,
                enabled = uiState.instructionInput.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Review Payload")
            }
        }
    }
}

private fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Kontact++ AI", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}

private fun Context.shareText(text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, "Share via"))
}
