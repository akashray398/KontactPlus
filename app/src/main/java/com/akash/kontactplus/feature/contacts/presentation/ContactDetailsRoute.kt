package com.akash.kontactplus.feature.contacts.presentation

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ContactDetailsRoute(
    onBackClick: () -> Unit,
    onManageRelationship: (String) -> Unit,
    onNavigateToDialpad: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ContactDetailsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onPhoneNumberClick = { phoneNumber ->
            onNavigateToDialpad(phoneNumber)
        },
        onFavouriteClick = {
            viewModel.onFavouriteClick()
        },
        onEditContactClick = {
            val intent = Intent(Intent.ACTION_INSERT_OR_EDIT).apply {
                type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
            }
            try {
                context.startActivity(intent)
            } catch (_: Exception) {
                // Safe fallback if no contacts editor available
            }
        },
        onManageRelationship = {
            if (uiState is ContactDetailsUiState.Success) {
                onManageRelationship((uiState as ContactDetailsUiState.Success).contact.lookupKey)
            }
        },
        onAddFact = { factText, category ->
            viewModel.onAddFact(factText, category)
        },
        onDeleteFact = { factId ->
            viewModel.onDeleteFact(factId)
        },
        onAnalyzeConversation = { notes ->
            viewModel.onAnalyzeConversation(notes)
        },
        onRetry = {
            viewModel.retry()
        },
        modifier = modifier
    )
}
