package com.akash.kontactplus.core.telecom.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.akash.kontactplus.core.telecom.CallManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CallActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var callManager: CallManager

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            CallNotificationManager.ACTION_ANSWER -> {
                callManager.answer()
            }
            CallNotificationManager.ACTION_DECLINE -> {
                callManager.reject()
            }
            CallNotificationManager.ACTION_HANG_UP -> {
                callManager.disconnect()
            }
        }
    }
}
