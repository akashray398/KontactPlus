package com.akash.kontactplus.core.telecom

/**
 * Domain-level representation of an audio endpoint.
 */
enum class CallAudioEndpoint {
    Earpiece,
    Speaker,
    WiredHeadset,
    Bluetooth,
    Streaming,
    Unknown
}
