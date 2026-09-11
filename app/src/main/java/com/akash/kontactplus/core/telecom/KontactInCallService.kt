package com.akash.kontactplus.core.telecom

import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Service that receives events for active calls.
 * Must be registered in AndroidManifest with BIND_INCALL_SERVICE permission.
 */
@AndroidEntryPoint
class KontactInCallService : InCallService() {

    @Inject
    lateinit var callManager: CallManager

    companion object {
        private var instance: KontactInCallService? = null
        
        /**
         * Sets the muted state for the active telecom session.
         */
        fun setMuted(muted: Boolean) {
            instance?.setMuted(muted)
        }
        
        /**
         * Sets the audio route for the active telecom session.
         */
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
        if (instance == this) {
            instance = null
        }
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
