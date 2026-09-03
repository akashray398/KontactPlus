package com.akash.kontactplus.feature.telecom

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.core.telecom.ActiveCallState
import com.akash.kontactplus.core.telecom.CallAudioController
import com.akash.kontactplus.core.telecom.CallAudioEndpoint
import com.akash.kontactplus.core.telecom.CallManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveCallViewModel @Inject constructor(
    private val callManager: CallManager,
    private val audioController: CallAudioController
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActiveCallUiState())
    val uiState: StateFlow<ActiveCallUiState> = _uiState.asStateFlow()

    private var durationJob: Job? = null

    init {
        viewModelScope.launch {
            callManager.activeCallInfo.collectLatest { info ->
                _uiState.update { it.copy(callInfo = info) }
                
                if (info.state == ActiveCallState.Active) {
                    startDurationTicker()
                } else if (info.state == ActiveCallState.Disconnected || info.state == ActiveCallState.NoCall) {
                    stopDurationTicker()
                }
            }
        }
    }

    fun answer() = callManager.answer()
    fun reject() = callManager.reject()
    fun disconnect() = callManager.disconnect()
    fun hold() = callManager.hold()
    fun unhold() = callManager.unhold()
    
    fun toggleMute() {
        val newMuteState = !_uiState.value.callInfo.isMuted
        audioController.setMuted(newMuteState)
    }

    fun setAudioRoute(endpoint: CallAudioEndpoint) {
        audioController.setAudioRoute(endpoint)
        _uiState.update { it.copy(isAudioRoutePickerVisible = false) }
    }

    fun showAudioRoutePicker() {
        _uiState.update { it.copy(isAudioRoutePickerVisible = true) }
    }

    fun hideAudioRoutePicker() {
        _uiState.update { it.copy(isAudioRoutePickerVisible = false) }
    }

    fun toggleDtmf() {
        _uiState.update { it.copy(isDtmfVisible = !it.isDtmfVisible) }
    }

    fun onDtmfPressed(digit: Char) {
        callManager.playDtmfTone(digit)
        _uiState.update { it.copy(dtmfDigits = it.dtmfDigits + digit) }
    }

    fun onDtmfReleased() {
        callManager.stopDtmfTone()
    }

    private fun startDurationTicker() {
        if (durationJob != null) return
        
        val baseTime = SystemClock.elapsedRealtime()
        durationJob = viewModelScope.launch {
            while (true) {
                val elapsedSeconds = (SystemClock.elapsedRealtime() - baseTime) / 1000
                _uiState.update { it.copy(durationText = formatDuration(elapsedSeconds)) }
                delay(1000)
            }
        }
    }

    private fun stopDurationTicker() {
        durationJob?.cancel()
        durationJob = null
    }

    private fun formatDuration(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) {
            String.format("%d:%02d:%02d", h, m, s)
        } else {
            String.format("%02d:%02d", m, s)
        }
    }
}
