package com.akash.kontactplus.feature.relationship.domain.model

import java.time.Instant

sealed class RelationshipTimelineItem {
    abstract val id: String
    abstract val timestamp: Instant
    abstract val title: String
    abstract val description: String

    data class CallEvent(
        override val id: String,
        override val timestamp: Instant,
        override val title: String,
        override val description: String,
        val callType: String // Incoming, Outgoing, Missed
    ) : RelationshipTimelineItem()

    data class FactEvent(
        override val id: String,
        override val timestamp: Instant,
        override val title: String,
        override val description: String,
        val category: FactCategory
    ) : RelationshipTimelineItem()

    data class DateEvent(
        override val id: String,
        override val timestamp: Instant,
        override val title: String,
        override val description: String,
        val repeatsYearly: Boolean
    ) : RelationshipTimelineItem()

    data class ReminderEvent(
        override val id: String,
        override val timestamp: Instant,
        override val title: String,
        override val description: String,
        val isCompleted: Boolean
    ) : RelationshipTimelineItem()

    data class NoteEvent(
        override val id: String,
        override val timestamp: Instant,
        override val title: String,
        override val description: String
    ) : RelationshipTimelineItem()
}
