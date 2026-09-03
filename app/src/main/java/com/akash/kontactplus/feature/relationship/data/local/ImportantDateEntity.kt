package com.akash.kontactplus.feature.relationship.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "important_dates")
data class ImportantDateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lookupKey: String,
    val title: String,
    val dateEpochDay: Long,
    val type: String,
    val repeatsYearly: Boolean,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long
)
