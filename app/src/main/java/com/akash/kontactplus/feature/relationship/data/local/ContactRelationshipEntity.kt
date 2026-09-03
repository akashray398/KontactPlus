package com.akash.kontactplus.feature.relationship.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_relationships")
data class ContactRelationshipEntity(
    @PrimaryKey
    val lookupKey: String,
    val privateNote: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long
)
