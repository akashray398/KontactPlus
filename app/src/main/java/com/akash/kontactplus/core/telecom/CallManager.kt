package com.akash.kontactplus.core.telecom

import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.DisconnectCause
import android.telecom.VideoProfile
import com.akash.kontactplus.core.telecom.notification.CallNotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages active Telecom calls and provides a unified state to the application.
 * Supports multiple concurrent calls (e.g., active + held).
 */
@Singleton
class CallManager @Inject constructor(
    private val notificationManager: CallNotificationManager
) {
    private val _activeCallInfo = MutableStateFlow(ActiveCallInfo())
    val activeCallInfo: StateFlow<ActiveCallInfo> = _activeCallInfo.asStateFlow()

    // Internal list to track all active calls managed by Telecom
    private val calls = mutableMapOf<String, Call>()
    
    // The "primary" call currently in focus for the UI
    private var primaryCallId: String? = null

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call?, state: Int) {
            updateAllCallStates()
        }

        override fun onDetailsChanged(call: Call?, details: Call.Details?) {
            updateAllCallStates()
        }
    }

    fun onCallAdded(call: Call) {
        val callId = call.hashCode().toString()
        calls[callId] = call
        call.registerCallback(callCallback)
        
        // If this is the first call, or it's ringing, make it primary
        if (primaryCallId == null || call.state == Call.STATE_RINGING) {
            primaryCallId = callId
        }
        
        updateAllCallStates()
    }

    fun onCallRemoved(call: Call) {
        val callId = call.hashCode().toString()
        call.unregisterCallback(callCallback)
        calls.remove(callId)
        
        if (primaryCallId == callId) {
            // Pick another call as primary if available, prioritize Active
            primaryCallId = calls.values.find { it.state == Call.STATE_ACTIVE }?.hashCode()?.toString()
                ?: calls.keys.firstOrNull()
        }

        if (calls.isEmpty()) {
            notificationManager.cancelNotification()
            _activeCallInfo.update { ActiveCallInfo() }
        } else {
            updateAllCallStates()
        }
    }

    fun onCallAudioStateChanged(audioState: CallAudioState) {
        _activeCallInfo.update {
            it.copy(
                isMuted = audioState.isMuted,
                currentEndpoint = mapAudioRoute(audioState.route),
                availableEndpoints = mapAvailableRoutes(audioState.supportedRouteMask)
            )
        }
    }

    fun answer() {
        getPrimaryCall()?.answer(VideoProfile.STATE_AUDIO_ONLY)
    }

    fun reject() {
        getPrimaryCall()?.reject(false, null)
    }

    fun disconnect() {
        getPrimaryCall()?.disconnect()
    }

    fun hold() {
        getPrimaryCall()?.hold()
    }

    fun unhold() {
        getPrimaryCall()?.unhold()
    }

    fun playDtmfTone(digit: Char) {
        getPrimaryCall()?.playDtmfTone(digit)
    }

    fun stopDtmfTone() {
        getPrimaryCall()?.stopDtmfTone()
    }

    private fun getPrimaryCall(): Call? = primaryCallId?.let { calls[it] }

    private fun updateAllCallStates() {
        val call = getPrimaryCall()
        if (call == null) {
            if (calls.isEmpty()) {
                _activeCallInfo.update { ActiveCallInfo() }
            } else {
                // If primary was null but list not empty, reset primary and try again
                primaryCallId = calls.keys.firstOrNull()
                updateAllCallStates()
            }
            return
        }

        val details = call.details
        val phoneNumber = details?.handle?.schemeSpecificPart ?: ""
        val displayName = details?.callerDisplayName ?: ""
        val state = mapTelecomState(call.state)
        val direction = if (call.state == Call.STATE_RINGING || call.state == Call.STATE_CONNECTING) {
            // Note: Connecting might be outgoing too, but ringing is definitely incoming
            if (call.state == Call.STATE_RINGING) CallDirection.Incoming else CallDirection.Outgoing
        } else {
            // Fallback to simple direction detection
            if (call.state == Call.STATE_DIALING) CallDirection.Outgoing else CallDirection.Incoming
        }
        
        val capabilities = details?.callCapabilities ?: 0
        val canHold = (capabilities and Call.Details.CAPABILITY_HOLD) != 0
        val canUnhold = (capabilities and Call.Details.CAPABILITY_SUPPORT_HOLD) != 0 && call.state == Call.STATE_HOLDING
        val canMute = (capabilities and Call.Details.CAPABILITY_MUTE) != 0
        val canDtmf = true

        val disconnectReason = mapDisconnectCause(details?.disconnectCause)

        val info = ActiveCallInfo(
            callId = call.hashCode().toString(),
            phoneNumber = phoneNumber,
            displayName = displayName,
            state = state,
            direction = direction,
            connectTimeMillis = details?.connectTimeMillis ?: 0L,
            canHold = canHold,
            canUnhold = canUnhold,
            canMute = canMute,
            canDtmf = canDtmf,
            disconnectReason = disconnectReason
        )

        _activeCallInfo.update { current ->
            info.copy(
                isMuted = current.isMuted,
                currentEndpoint = current.currentEndpoint,
                availableEndpoints = current.availableEndpoints
            )
        }

        updateNotification(info)
    }

    private fun updateNotification(info: ActiveCallInfo) {
        when (info.state) {
            ActiveCallState.Incoming -> notificationManager.showIncomingCallNotification(info)
            ActiveCallState.Active, ActiveCallState.Dialling, ActiveCallState.Connecting, ActiveCallState.OnHold -> {
                notificationManager.showOngoingCallNotification(info)
            }
            ActiveCallState.Disconnected, ActiveCallState.Disconnecting, ActiveCallState.NoCall -> {
                // Do not cancel immediately if it's a short transition, but usually InCallService handles removal
            }
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
            Call.STATE_DISCONNECTING -> ActiveCallState.Disconnecting
            else -> ActiveCallState.NoCall
        }
    }

    private fun mapDisconnectCause(cause: DisconnectCause?): CallDisconnectReason {
        return when (cause?.code) {
            DisconnectCause.LOCAL -> CallDisconnectReason.Local
            DisconnectCause.REMOTE -> CallDisconnectReason.Remote
            DisconnectCause.REJECTED -> CallDisconnectReason.Rejected
            DisconnectCause.MISSED -> CallDisconnectReason.Missed
            DisconnectCause.BUSY -> CallDisconnectReason.Busy
            DisconnectCause.ERROR -> CallDisconnectReason.Error
            else -> CallDisconnectReason.Other
        }
    }

    private fun mapAudioRoute(route: Int): CallAudioEndpoint {
        return when (route) {
            CallAudioState.ROUTE_EARPIECE -> CallAudioEndpoint.Earpiece
            CallAudioState.ROUTE_SPEAKER -> CallAudioEndpoint.Speaker
            CallAudioState.ROUTE_WIRED_HEADSET -> CallAudioEndpoint.WiredHeadset
            CallAudioState.ROUTE_BLUETOOTH -> CallAudioEndpoint.Bluetooth
            else -> CallAudioEndpoint.Unknown
        }
    }

    private fun mapAvailableRoutes(mask: Int): List<CallAudioEndpoint> {
        val routes = mutableListOf<CallAudioEndpoint>()
        if (mask and CallAudioState.ROUTE_EARPIECE != 0) routes.add(CallAudioEndpoint.Earpiece)
        if (mask and CallAudioState.ROUTE_SPEAKER != 0) routes.add(CallAudioEndpoint.Speaker)
        if (mask and CallAudioState.ROUTE_WIRED_HEADSET != 0) routes.add(CallAudioEndpoint.WiredHeadset)
        if (mask and CallAudioState.ROUTE_BLUETOOTH != 0) routes.add(CallAudioEndpoint.Bluetooth)
        return routes
    }
}
