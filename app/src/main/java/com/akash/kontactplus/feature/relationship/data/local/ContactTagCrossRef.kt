package com.akash.kontactplus.feature.relationship.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "contact_tag_cross_ref",
    primaryKeys = ["lookupKey", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = RelationshipTagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tagId"])]
)
data class ContactTagCrossRef(
    val lookupKey: String,
    val tagId: Long
)
