package com.akash.kontactplus.feature.contacts.domain.usecase

import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
import javax.inject.Inject

/**
 * Use case to retrieve a single contact by its lookup key.
 */
class GetContactUseCase @Inject constructor(
    private val repository: ContactsRepository
) {
    /**
     * Executes the use case.
     * @param lookupKey The lookup key of the contact.
     * @return A [Result] containing the [Contact] if found, null if not found.
     */
    suspend operator fun invoke(lookupKey: String): Result<Contact?> {
        if (lookupKey.isBlank()) return Result.success(null)
        return repository.getContact(lookupKey)
    }
}
