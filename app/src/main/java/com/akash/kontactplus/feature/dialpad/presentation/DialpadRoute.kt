package com.akash.kontactplus.feature.dialpad.presentation

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.telecom.TelecomManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.akash.kontactplus.core.telecom.DialerRoleState
import com.akash.kontactplus.core.telecom.TelecomRoleManager

@Composable
fun DialpadRoute(
    telecomRoleManager: TelecomRoleManager,
    modifier: Modifier = Modifier,
    viewModel: DialpadViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val checkStatus = {
        val isRoleHeld = telecomRoleManager.getDialerRoleState() == DialerRoleState.Held
        val isPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
        
        val activity = context as? androidx.activity.ComponentActivity
        val shouldShowRationale = activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CALL_PHONE)
        } ?: false
        
        viewModel.onAccessStatusChanged(
            isRoleHeld = isRoleHeld,
            isPermissionGranted = isPermissionGranted,
            shouldShowRationale = shouldShowRationale,
            isTelecomSupported = telecomRoleManager.isTelecomSupported()
        )
    }

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            checkStatus()
        }
    }

    DialpadScreen(
        uiState = uiState,
        onKeyPressed = viewModel::onKeyPressed,
        onZeroLongPressed = viewModel::onZeroLongPressed,
        onDelete = viewModel::onDelete,
        onClear = viewModel::onClear,
        onPaste = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
            viewModel.onPastedText(text)
        },
        onSuggestionClick = { number ->
            viewModel.onExternalNumberReceived(number)
        },
        onCallClick = {
            if (uiState.accessState == DialpadAccessState.Ready) {
                context.placeCall(uiState.dialableNumber)
            }
        },
        onRequestRole = {
            val intent = telecomRoleManager.createRoleRequestIntent()
            intent?.let { context.startActivity(it) }
        },
        onRequestCallPermission = {
            viewModel.onPermissionRequestStarted()
            val activity = context as? androidx.activity.ComponentActivity
            activity?.let {
                ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.CALL_PHONE), 0)
            }
        },
        onOpenSettings = {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        },
        onCreateContact = {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                type = ContactsContract.Contacts.CONTENT_TYPE
                putExtra(ContactsContract.Intents.Insert.PHONE, uiState.dialableNumber)
            }
            context.startActivity(intent)
        },
        modifier = modifier
    )
}

private fun Context.placeCall(number: String) {
    if (number.isBlank()) return
    val telecomManager = getSystemService(TelecomManager::class.java)
    val uri = Uri.fromParts("tel", number, null)
    try {
        telecomManager?.placeCall(uri, null)
    } catch (e: SecurityException) {
        // Handled by role/permission gating
    }
}
