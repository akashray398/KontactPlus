package com.akash.kontactplus.feature.favourites.domain.usecase

import com.akash.kontactplus.feature.favourites.domain.repository.FavouritesRepository
import javax.inject.Inject

/**
 * Use case to add or remove a contact from favourites.
 */
class ToggleFavouriteContactUseCase @Inject constructor(
    private val repository: FavouritesRepository
) {
    suspend operator fun invoke(lookupKey: String, isFavourite: Boolean): Result<Unit> {
        if (lookupKey.isBlank()) return Result.failure(IllegalArgumentException("Lookup key is blank"))
        
        return try {
            if (isFavourite) {
                repository.removeFavourite(lookupKey)
            } else {
                repository.addFavourite(lookupKey)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
