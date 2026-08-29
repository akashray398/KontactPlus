package com.akash.kontactplus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.akash.kontactplus.app.KontactPlusApp
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme
import com.akash.kontactplus.core.telecom.DialIntentHandler
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
}
