package com.akash.kontactplus.feature.relationship.domain.model

data class ContactRelationship(
    val lookupKey: String,
    val privateNote: String = "",
    val tags: List<RelationshipTag> = emptyList(),
    val importantDates: List<ImportantDate> = emptyList(),
    val reminders: List<RelationshipReminder> = emptyList(),
    val updatedAtEpochMillis: Long = 0
)
