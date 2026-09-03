package com.akash.kontactplus.core.telecom

/**
 * Domain-level representation of a call disconnect reason.
 */
enum class CallDisconnectReason {
    Local,
    Remote,
    Rejected,
    Missed,
    Busy,
    Error,
    Other
}
