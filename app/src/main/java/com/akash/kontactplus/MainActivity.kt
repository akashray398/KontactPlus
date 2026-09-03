package com.akash.kontactplus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.akash.kontactplus.app.KontactPlusApp
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme
import com.akash.kontactplus.core.telecom.DialIntentHandler
import com.akash.kontactplus.core.telecom.DialerRoleState
import com.akash.kontactplus.core.telecom.TelecomRoleManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var telecomRoleManager: TelecomRoleManager

    @Inject
    lateinit var dialIntentHandler: DialIntentHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)
        checkNotificationPermission()

        enableEdgeToEdge()

        setContent {
            KontactPlusTheme {
                KontactPlusApp(
                    telecomRoleManager = telecomRoleManager,
                    dialIntentHandler = dialIntentHandler
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_DIAL) {
            val number = intent.data?.schemeSpecificPart
            dialIntentHandler.onDialIntentReceived(number)
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val roleState = telecomRoleManager.getDialerRoleState()
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (roleState == DialerRoleState.Held && !hasPermission) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }
}
