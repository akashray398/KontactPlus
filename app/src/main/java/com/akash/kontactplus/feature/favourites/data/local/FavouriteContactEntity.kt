package com.akash.kontactplus.feature.favourites.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a favourite contact.
 * We store only the minimum metadata to resolve back to a device contact.
 */
@Entity(tableName = "favourite_contacts")
data class FavouriteContactEntity(
    @PrimaryKey
    val lookupKey: String,
    val addedAtEpochMillis: Long
)
