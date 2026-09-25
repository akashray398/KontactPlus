package com.akash.kontactplus.feature.contacts.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akash.kontactplus.core.telecom.TelecomRoleManager
import com.akash.kontactplus.feature.ai.domain.repository.AiRepository

@Composable
fun ContactsRoute(
    telecomRoleManager: TelecomRoleManager,
    aiRepository: AiRepository,
    onContactClick: (String) -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToDialpad: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val aiDisclosureAccepted by aiRepository.hasAcceptedDisclosure().collectAsStateWithLifecycle(initialValue = false)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val activity = context.findActivity()
        val shouldShowRationale = activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.READ_CONTACTS)
        } ?: false
        viewModel.onPermissionResultReceived(isGranted, shouldShowRationale)
    }

    val checkAllStatus = {
        val isContactsGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        val isDialerHeld = telecomRoleManager.getDialerRoleState() == com.akash.kontactplus.core.telecom.DialerRoleState.Held
        val isCallLogGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
        val isNotificationsGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        val activity = context.findActivity()
        val shouldShowRationale = activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.READ_CONTACTS)
        } ?: false
        
        viewModel.onPermissionStatusChecked(isContactsGranted, shouldShowRationale)
        viewModel.updateSetupItems(
            isContactsGranted = isContactsGranted,
            isDialerHeld = isDialerHeld,
            isCallLogGranted = isCallLogGranted,
            isNotificationsGranted = isNotificationsGranted,
            isAiDisclosureAccepted = aiDisclosureAccepted
        )
    }

    LaunchedEffect(Unit) {
        checkAllStatus()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkAllStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ContactsScreen(
        uiState = uiState,
        onRequestPermission = {
            viewModel.onPermissionRequestStarted()
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        },
        onOpenSettings = {
            context.openSettings()
        },
        onRetryLoading = {
            viewModel.retryLoadingContacts()
        },
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onClearSearch = viewModel::onClearSearch,
        onSortOrderChanged = viewModel::onSortOrderChanged,
        onContactClick = onContactClick,
        onDismissSetupCard = viewModel::onDismissSetupCard,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToDialpad = onNavigateToDialpad,
        onNavigateToRecents = onNavigateToRecents,
        onNavigateToAi = onNavigateToAi,
        onNavigateToSettings = onNavigateToSettings,
        modifier = modifier
    )
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

private fun Context.openSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}
