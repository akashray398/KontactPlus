package com.akash.kontactplus.feature.recents.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telecom.TelecomManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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

@Composable
fun RecentsRoute(
    telecomRoleManager: TelecomRoleManager,
    onContactClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val roleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // Re-check role on result
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        // ViewModel handles state update on next check
    }

    val checkStatus = {
        val isRoleHeld = telecomRoleManager.getDialerRoleState() == com.akash.kontactplus.core.telecom.DialerRoleState.Held
        val isPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED
        
        val activity = context.findActivity()
        val shouldShowRationale = activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.READ_CALL_LOG)
        } ?: false
        
        viewModel.onAccessStatusChecked(
            isRoleHeld = isRoleHeld,
            isPermissionGranted = isPermissionGranted,
            shouldShowRationale = shouldShowRationale,
            isTelecomSupported = telecomRoleManager.isTelecomSupported()
        )
    }

    LaunchedEffect(Unit) {
        checkStatus()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    RecentsScreen(
        uiState = uiState,
        onRequestRole = {
            telecomRoleManager.createRoleRequestIntent()?.let { intent ->
                roleLauncher.launch(intent)
            }
        },
        onRequestPermission = {
            viewModel.onPermissionRequestStarted()
            permissionLauncher.launch(Manifest.permission.READ_CALL_LOG)
        },
        onOpenSettings = {
            context.openSettings()
        },
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onClearSearch = viewModel::onClearSearch,
        onRetry = viewModel::retry,
        onRefresh = viewModel::refresh,
        onContactClick = onContactClick,
        onRedial = { phoneNumber ->
            context.placeCall(phoneNumber)
        },
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
    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}

@SuppressLint("MissingPermission")
private fun Context.placeCall(phoneNumber: String) {
    if (phoneNumber.isBlank()) return
    val telecomManager = getSystemService(TelecomManager::class.java)
    val uri = Uri.parse("tel:${Uri.encode(phoneNumber)}")
    try {
        telecomManager?.placeCall(uri, null)
    } catch (e: SecurityException) {
        // Handled by role/permission gating
    }
}
