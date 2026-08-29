package com.akash.kontactplus.feature.favourites.domain.usecase

import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
import com.akash.kontactplus.feature.favourites.domain.repository.FavouritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case to observe favourite contacts by merging local favourites with device contacts.
 */
class ObserveFavouriteContactsUseCase @Inject constructor(
    private val favouritesRepository: FavouritesRepository,
    private val contactsRepository: ContactsRepository
) {
    operator fun invoke(): Flow<List<Contact>> {
        return combine(
            favouritesRepository.observeFavouriteLookupKeys(),
            // We fetch the latest contacts list whenever favorites change.
            // This is efficient because our contacts repository result is in-memory for the session (ideally).
            // Actually, currently ContactsRepository.getContacts() queries the provider.
            // We'll call it each time favorites change, but we should be careful with performance.
            flow { emit(contactsRepository.getContacts().getOrDefault(emptyList())) }
        ) { favouriteKeys, allContacts ->
            if (favouriteKeys.isEmpty()) return@combine emptyList()
            
            // Map keys for O(1) lookup
            val contactMap = allContacts.associateBy { it.lookupKey }
            
            favouriteKeys.mapNotNull { key ->
                contactMap[key]
            }
        }
    }
}
