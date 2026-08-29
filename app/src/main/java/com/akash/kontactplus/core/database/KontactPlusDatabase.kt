package com.akash.kontactplus.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.akash.kontactplus.feature.favourites.data.local.FavouriteContactDao
import com.akash.kontactplus.feature.favourites.data.local.FavouriteContactEntity

/**
 * Main database for the KontactPlus application.
 */
@Database(
    entities = [FavouriteContactEntity::class],
    version = 1,
    exportSchema = true
)
abstract class KontactPlusDatabase : RoomDatabase() {
    abstract fun favouriteContactDao(): FavouriteContactDao
}
