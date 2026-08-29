package com.akash.kontactplus.feature.contacts.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ContactDetailsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ContactDetailsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onPhoneNumberClick = { phoneNumber ->
            context.dialNumber(phoneNumber)
        },
        onFavouriteClick = {
            viewModel.onFavouriteClick()
        },
        onRetry = {
            viewModel.retry()
        },
        modifier = modifier
    )
}

private fun Context.dialNumber(phoneNumber: String) {
    if (phoneNumber.isBlank()) return
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:${Uri.encode(phoneNumber)}")
    }
    try {
        startActivity(intent)
    } catch (e: Exception) {
        // Fallback or log if dialer not found
    }
}
