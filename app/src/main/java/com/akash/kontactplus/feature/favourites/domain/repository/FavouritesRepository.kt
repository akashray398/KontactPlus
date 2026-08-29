package com.akash.kontactplus.feature.favourites.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing favourite contacts.
 */
interface FavouritesRepository {
    /**
     * Observes the list of lookup keys for all favourite contacts.
     */
    fun observeFavouriteLookupKeys(): Flow<List<String>>

    /**
     * Observes whether a contact with the given [lookupKey] is a favourite.
     */
    fun observeIsFavourite(lookupKey: String): Flow<Boolean>

    /**
     * Adds a contact with the given [lookupKey] to favourites.
     */
    suspend fun addFavourite(lookupKey: String)

    /**
     * Removes a contact with the given [lookupKey] from favourites.
     */
    suspend fun removeFavourite(lookupKey: String)
}
