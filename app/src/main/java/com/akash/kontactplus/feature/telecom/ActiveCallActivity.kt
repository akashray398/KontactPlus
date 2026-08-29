package com.akash.kontactplus.feature.telecom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme
import com.akash.kontactplus.core.telecom.ActiveCallState
import com.akash.kontactplus.core.telecom.CallManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ActiveCallActivity : ComponentActivity() {

    @Inject
    lateinit var callManager: CallManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            KontactPlusTheme {
                val activeCallInfo by callManager.activeCallInfo.collectAsState()
                
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (activeCallInfo.state) {
                        ActiveCallState.Incoming -> {
                            IncomingCallScreen(
                                callInfo = activeCallInfo,
                                onAnswer = { callManager.answer() },
                                onDecline = { callManager.reject() }
                            )
                        }
                        ActiveCallState.NoCall -> {
                            finish()
                        }
                        ActiveCallState.Disconnected -> {
                            // Optionally show a disconnected state for a brief moment
                            finish()
                        }
                        else -> {
                            ActiveCallScreen(
                                callInfo = activeCallInfo,
                                onDisconnect = { callManager.disconnect() },
                                onHold = { callManager.hold() },
                                onUnhold = { callManager.unhold() }
                            )
                        }
                    }
                }
            }
        }
    }
}
