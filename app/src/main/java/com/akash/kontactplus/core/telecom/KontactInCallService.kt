package com.akash.kontactplus.core.telecom

import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Service that receives events for active calls.
 */
@AndroidEntryPoint
class KontactInCallService : InCallService() {

    @Inject
    lateinit var callManager: CallManager

    companion object {
        private var instance: KontactInCallService? = null
        
        fun setMuted(muted: Boolean) {
            instance?.setMuted(muted)
        }
        
        fun setAudioRoute(route: Int) {
            instance?.setAudioRoute(route)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        callManager.onCallAdded(call)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        callManager.onCallRemoved(call)
    }

    override fun onCallAudioStateChanged(audioState: CallAudioState) {
        super.onCallAudioStateChanged(audioState)
        callManager.onCallAudioStateChanged(audioState)
    }
}
