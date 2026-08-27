package com.akash.kontactplus.feature.contacts.domain.repository

import com.akash.kontactplus.feature.contacts.domain.model.Contact

/**
 * Domain-level repository interface for contact operations.
 */
interface ContactsRepository {
    /**
     * Retrieves all contacts from the system.
     * @return A [Result] containing a list of [Contact]s or an error.
     */
    suspend fun getContacts(): Result<List<Contact>>
}
