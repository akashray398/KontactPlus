package com.akash.kontactplus.feature.favourites.data.repository

import com.akash.kontactplus.feature.favourites.data.local.FavouriteContactDao
import com.akash.kontactplus.feature.favourites.data.local.FavouriteContactEntity
import com.akash.kontactplus.feature.favourites.domain.repository.FavouritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [FavouritesRepository] using Room DAO.
 */
class FavouritesRepositoryImpl @Inject constructor(
    private val favouriteContactDao: FavouriteContactDao
) : FavouritesRepository {

    override fun observeFavouriteLookupKeys(): Flow<List<String>> {
        return favouriteContactDao.observeFavouriteLookupKeys()
    }

    override fun observeIsFavourite(lookupKey: String): Flow<Boolean> {
        return favouriteContactDao.observeIsFavourite(lookupKey)
    }

    override suspend fun addFavourite(lookupKey: String) {
        if (lookupKey.isBlank()) return
        val entity = FavouriteContactEntity(
            lookupKey = lookupKey,
            addedAtEpochMillis = System.currentTimeMillis()
        )
        favouriteContactDao.insertFavourite(entity)
    }

    override suspend fun removeFavourite(lookupKey: String) {
        if (lookupKey.isBlank()) return
        favouriteContactDao.deleteFavourite(lookupKey)
    }
}
