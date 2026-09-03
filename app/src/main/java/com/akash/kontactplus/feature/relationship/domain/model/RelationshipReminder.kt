package com.akash.kontactplus.feature.relationship.domain.model

import java.time.Instant

data class RelationshipReminder(
    val id: String,
    val lookupKey: String,
    val title: String,
    val note: String,
    val scheduledAt: Instant,
    val status: ReminderStatus
)

enum class ReminderStatus {
    Scheduled,
    Completed,
    Cancelled,
    Overdue
}
