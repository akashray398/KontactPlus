package com.akash.kontactplus.feature.relationship.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "relationship_reminders")
data class RelationshipReminderEntity(
    @PrimaryKey
    val id: String, // UUID
    val lookupKey: String,
    val title: String,
    val note: String,
    val scheduledAtEpochMillis: Long,
    val status: String,
    val workRequestId: String?,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val completedAtEpochMillis: Long?
)
