package com.akash.kontactplus.feature.favourites.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for favourite contacts operations.
 */
@Dao
interface FavouriteContactDao {

    @Query("SELECT lookupKey FROM favourite_contacts ORDER BY addedAtEpochMillis DESC")
    fun observeFavouriteLookupKeys(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_contacts WHERE lookupKey = :lookupKey)")
    fun observeIsFavourite(lookupKey: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(entity: FavouriteContactEntity)

    @Query("DELETE FROM favourite_contacts WHERE lookupKey = :lookupKey")
    suspend fun deleteFavourite(lookupKey: String)
}
