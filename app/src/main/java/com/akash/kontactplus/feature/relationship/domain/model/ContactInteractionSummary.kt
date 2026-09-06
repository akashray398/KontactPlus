package com.akash.kontactplus.feature.relationship.domain.model

data class ContactInteractionSummary(
    val lookupKey: String,
    val lastInteractionAt: java.time.Instant?,
    val lastIncomingCallAt: java.time.Instant?,
    val lastOutgoingCallAt: java.time.Instant?,
    val lastMissedCallAt: java.time.Instant?,
    val callCountLast30Days: Int,
    val incomingCountLast30Days: Int,
    val outgoingCountLast30Days: Int,
    val missedCountLast30Days: Int,
    val totalDurationLast30DaysMillis: Long
)
