package com.akash.kontactplus.feature.relationship.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_follow_up_preferences")
data class ContactFollowUpPreferenceEntity(
    @PrimaryKey
    val lookupKey: String,
    val cadenceType: String,
    val customIntervalDays: Int?,
    val enabled: Boolean,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long
)
