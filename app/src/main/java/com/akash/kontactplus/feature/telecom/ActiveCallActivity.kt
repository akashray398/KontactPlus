package com.akash.kontactplus.feature.telecom

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme
import com.akash.kontactplus.core.telecom.ActiveCallState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActiveCallActivity : ComponentActivity() {

    private val viewModel: ActiveCallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setupLockScreenVisibility()

        setContent {
            KontactPlusTheme {
                val uiState by viewModel.uiState.collectAsState()
                
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (uiState.callInfo.state) {
                        ActiveCallState.Incoming -> {
                            IncomingCallScreen(
                                callInfo = uiState.callInfo,
                                onAnswer = { viewModel.answer() },
                                onDecline = { viewModel.reject() }
                            )
                        }
                        ActiveCallState.NoCall -> {
                            finish()
                        }
                        ActiveCallState.Disconnected -> {
                            ActiveCallScreen(
                                uiState = uiState,
                                onDisconnect = {},
                                onHold = {},
                                onUnhold = {},
                                onToggleMute = {},
                                onToggleDtmf = {},
                                onShowAudioPicker = {},
                                onDtmfDigitPressed = {},
                                onDtmfDigitReleased = {},
                                onAudioEndpointSelected = {},
                                onDismissAudioPicker = {}
                            )
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                finish()
                            }, 1000)
                        }
                        else -> {
                            ActiveCallScreen(
                                uiState = uiState,
                                onDisconnect = { viewModel.disconnect() },
                                onHold = { viewModel.hold() },
                                onUnhold = { viewModel.unhold() },
                                onToggleMute = { viewModel.toggleMute() },
                                onToggleDtmf = { viewModel.toggleDtmf() },
                                onShowAudioPicker = { viewModel.showAudioRoutePicker() },
                                onDtmfDigitPressed = { viewModel.onDtmfPressed(it) },
                                onDtmfDigitReleased = { viewModel.onDtmfReleased() },
                                onAudioEndpointSelected = { viewModel.setAudioRoute(it) },
                                onDismissAudioPicker = { viewModel.hideAudioRoutePicker() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setupLockScreenVisibility() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }
}
