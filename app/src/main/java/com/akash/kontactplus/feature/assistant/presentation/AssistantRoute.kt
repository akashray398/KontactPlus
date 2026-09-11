package com.akash.kontactplus.feature.assistant.presentation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AssistantRoute(
    onContactClick: (String) -> Unit,
    onAiToolsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onDraftMessage: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AssistantViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AssistantUiEvent.ShowUndo -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "Suggestion snoozed/dismissed",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onUndoAction(event.insight)
                    }
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AssistantScreen(
        uiState = uiState,
        onContactClick = onContactClick,
        onAiToolsClick = onAiToolsClick,
        onSettingsClick = onSettingsClick,
        onAcceptCallHistoryDisclosure = viewModel::onAcceptCallHistoryDisclosure,
        onSnoozeInsight = viewModel::onSnoozeInsight,
        onDismissInsight = viewModel::onDismissInsight,
        onMarkReminderDone = viewModel::onMarkReminderDone,
        onDraftGreeting = { insight ->
            // Map insight to AI action and instruction
            val action = "FollowUpDraft" 
            val instruction = "Draft a polite follow-up message."
            onDraftMessage(action, instruction)
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}
