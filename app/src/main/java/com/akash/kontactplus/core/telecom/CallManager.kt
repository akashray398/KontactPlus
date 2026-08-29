package com.akash.kontactplus.core.telecom

import android.telecom.Call
import android.telecom.CallAudioState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton manager that interacts directly with Telecom Call objects.
 * Exposes domain-safe state flows.
 */
@Singleton
class CallManager @Inject constructor() {

    private val _activeCallInfo = MutableStateFlow(ActiveCallInfo())
    val activeCallInfo: StateFlow<ActiveCallInfo> = _activeCallInfo.asStateFlow()

    private var currentCall: Call? = null

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call?, state: Int) {
            updateCallInfo()
        }

        override fun onDetailsChanged(call: Call?, details: Call.Details?) {
            updateCallInfo()
        }
    }

    /**
     * Called when a new call is added to the InCallService.
     */
    fun onCallAdded(call: Call) {
        currentCall = call
        call.registerCallback(callCallback)
        updateCallInfo()
    }

    /**
     * Called when a call is removed from the InCallService.
     */
    fun onCallRemoved(call: Call) {
        if (currentCall == call) {
            call.unregisterCallback(callCallback)
            currentCall = null
            _activeCallInfo.update { ActiveCallInfo() }
        }
    }

    fun answer() {
        currentCall?.answer(android.telecom.VideoProfile.STATE_AUDIO_ONLY)
    }

    fun reject() {
        currentCall?.reject(false, null)
    }

    fun disconnect() {
        currentCall?.disconnect()
    }

    fun hold() {
        currentCall?.hold()
    }

    fun unhold() {
        currentCall?.unhold()
    }

    /**
     * Updates the domain-safe active-call state from the raw Telecom Call.
     */
    private fun updateCallInfo() {
        val call = currentCall ?: return
        val details = call.details
        
        val phoneNumber = details?.handle?.schemeSpecificPart ?: ""
        val displayName = details?.callerDisplayName ?: ""
        val state = mapTelecomState(call.state)
        
        val capabilities = details?.callCapabilities ?: 0
        val canHold = (capabilities and Call.Details.CAPABILITY_HOLD) != 0
        val canUnhold = (capabilities and Call.Details.CAPABILITY_SUPPORT_HOLD) != 0 // Simplified
        val canMute = (capabilities and Call.Details.CAPABILITY_MUTE) != 0

        _activeCallInfo.update {
            it.copy(
                phoneNumber = phoneNumber,
                displayName = displayName,
                state = state,
                canHold = canHold,
                canUnhold = canUnhold,
                canMute = canMute
            )
        }
    }

    private fun mapTelecomState(state: Int): ActiveCallState {
        return when (state) {
            Call.STATE_NEW -> ActiveCallState.Connecting
            Call.STATE_RINGING -> ActiveCallState.Incoming
            Call.STATE_DIALING -> ActiveCallState.Dialling
            Call.STATE_CONNECTING -> ActiveCallState.Connecting
            Call.STATE_ACTIVE -> ActiveCallState.Active
            Call.STATE_HOLDING -> ActiveCallState.OnHold
            Call.STATE_DISCONNECTED -> ActiveCallState.Disconnected
            Call.STATE_DISCONNECTING -> ActiveCallState.Disconnected
            else -> ActiveCallState.NoCall
        }
    }
}
