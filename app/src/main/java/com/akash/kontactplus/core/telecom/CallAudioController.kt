package com.akash.kontactplus.core.telecom

import android.telecom.CallAudioState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallAudioController @Inject constructor() {

    fun setMuted(muted: Boolean) {
        KontactInCallService.setMuted(muted)
    }

    fun setAudioRoute(endpoint: CallAudioEndpoint) {
        val route = when (endpoint) {
            CallAudioEndpoint.Earpiece -> CallAudioState.ROUTE_EARPIECE
            CallAudioEndpoint.Speaker -> CallAudioState.ROUTE_SPEAKER
            CallAudioEndpoint.WiredHeadset -> CallAudioState.ROUTE_WIRED_HEADSET
            CallAudioEndpoint.Bluetooth -> CallAudioState.ROUTE_BLUETOOTH
            else -> return
        }
        KontactInCallService.setAudioRoute(route)
    }
}
