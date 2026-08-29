package com.akash.kontactplus.feature.recents.domain.model

/**
 * Domain-level representation of a call type.
 */
enum class RecentCallType {
    Incoming,
    Outgoing,
    Missed,
    Rejected,
    Blocked,
    Voicemail,
    Unknown
}
