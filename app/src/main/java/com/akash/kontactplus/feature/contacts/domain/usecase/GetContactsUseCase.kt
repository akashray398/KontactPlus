package com.akash.kontactplus.feature.contacts.domain.usecase

import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
import javax.inject.Inject

/**
 * Use case to retrieve the list of contacts.
 */
class GetContactsUseCase @Inject constructor(
    private val repository: ContactsRepository
) {
    /**
     * Executes the use case.
     * @return A [Result] containing the list of [Contact]s.
     */
    suspend operator fun invoke(): Result<List<Contact>> {
        return repository.getContacts()
    }
}
