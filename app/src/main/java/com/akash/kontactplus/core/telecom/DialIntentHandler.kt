package com.akash.kontactplus.core.telecom

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles incoming DIAL intents and notifies the UI.
 */
@Singleton
class DialIntentHandler @Inject constructor() {

    private val _dialNumber = MutableSharedFlow<String?>(extraBufferCapacity = 1)
    val dialNumber: SharedFlow<String?> = _dialNumber.asSharedFlow()

    fun onDialIntentReceived(number: String?) {
        _dialNumber.tryEmit(number)
    }
}
