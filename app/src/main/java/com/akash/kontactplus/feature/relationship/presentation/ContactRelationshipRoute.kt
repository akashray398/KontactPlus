package com.akash.kontactplus.feature.relationship.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ContactRelationshipRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactRelationshipViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ContactRelationshipScreen(
        uiState = uiState,
        onNoteChanged = viewModel::onNoteChanged,
        onSaveNote = viewModel::saveNote,
        onAddTag = viewModel::addTag,
        onRemoveTag = viewModel::removeTag,
        onAddDate = viewModel::addImportantDate,
        onDeleteDate = viewModel::deleteImportantDate,
        onAddReminder = viewModel::addReminder,
        onCompleteReminder = viewModel::completeReminder,
        onCancelReminder = viewModel::cancelReminder,
        onBackClick = onBackClick,
        modifier = modifier
    )
}
