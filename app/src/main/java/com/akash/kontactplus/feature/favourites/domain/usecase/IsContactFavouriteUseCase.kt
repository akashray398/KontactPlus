package com.akash.kontactplus.feature.favourites.domain.usecase

import com.akash.kontactplus.feature.favourites.domain.repository.FavouritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Use case to check if a specific contact is a favourite.
 */
class IsContactFavouriteUseCase @Inject constructor(
    private val repository: FavouritesRepository
) {
    operator fun invoke(lookupKey: String): Flow<Boolean> {
        if (lookupKey.isBlank()) return flowOf(false)
        return repository.observeIsFavourite(lookupKey)
    }
}
