package com.akash.kontactplus.feature.relationship.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contact_facts",
    indices = [Index(value = ["lookupKey"])]
)
data class ContactFactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lookupKey: String,
    val fact: String,
    val category: String, // Personal, Professional, Hobby, Preference, Conversation
    val sourceNoteId: String? = null,
    val createdAtEpochMillis: Long
)
