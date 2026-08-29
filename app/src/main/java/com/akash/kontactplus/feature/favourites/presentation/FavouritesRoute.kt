package com.akash.kontactplus.feature.favourites.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FavouritesRoute(
    onContactClick: (String) -> Unit,
    onOpenContacts: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavouritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val hasPermission = context.checkContactsPermission()
        viewModel.onPermissionStatusChanged(hasPermission)
    }

    FavouritesScreen(
        uiState = uiState,
        onContactClick = onContactClick,
        onOpenContacts = onOpenContacts,
        onRetry = { viewModel.retry() },
        modifier = modifier
    )
}

private fun Context.checkContactsPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED
}
