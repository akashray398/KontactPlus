package com.akash.kontactplus.feature.relationship.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "connection_suggestion_actions",
    indices = [Index(value = ["lookupKey"])]
)
data class ConnectionSuggestionActionEntity(
    @PrimaryKey
    val suggestionKey: String,
    val lookupKey: String,
    val ruleType: String,
    val sourceTimestampMillis: Long?,
    val state: String,
    val snoozedUntilEpochMillis: Long?,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long
)
