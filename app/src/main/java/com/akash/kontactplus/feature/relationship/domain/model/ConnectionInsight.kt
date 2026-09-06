package com.akash.kontactplus.feature.relationship.domain.model

data class ConnectionInsight(
    val id: String,
    val lookupKey: String,
    val type: ConnectionInsightType,
    val priority: ConnectionInsightPriority,
    val titleRes: Int,
    val explanation: String,
    val occurredAt: java.time.Instant?,
    val dueAt: java.time.Instant?,
    val availableActions: List<ConnectionInsightAction>,
    val source: ConnectionInsightSource
)

enum class ConnectionInsightType {
    FollowUpDue,
    FollowUpOverdue,
    UnreturnedMissedCall,
    ImportantDateApproaching,
    ReminderDue,
    ReminderOverdue
}

enum class ConnectionInsightPriority {
    Normal,
    Soon,
    Due,
    Overdue
}

enum class ConnectionInsightSource {
    UserCadence,
    MissedCall,
    ImportantDate,
    Reminder
}

enum class ConnectionInsightAction {
    Call,
    DraftMessage,
    MarkDone,
    Snooze,
    Dismiss,
    ViewContact
}
