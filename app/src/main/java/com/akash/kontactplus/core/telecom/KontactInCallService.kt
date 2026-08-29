package com.akash.kontactplus.core.telecom

import android.telecom.Call
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

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        callManager.onCallAdded(call)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        callManager.onCallRemoved(call)
    }
}
