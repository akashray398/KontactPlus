package com.akash.kontactplus.feature.relationship.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "relationship_tags",
    indices = [Index(value = ["normalizedName"], unique = true)]
)
data class RelationshipTagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val normalizedName: String,
    val colorKey: String,
    val createdAtEpochMillis: Long
)
