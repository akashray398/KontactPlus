package com.akash.kontactplus.feature.contacts.data.datasource

import com.akash.kontactplus.feature.contacts.domain.model.Contact

/**
 * Data source interface for fetching contacts from the device.
 */
interface ContactsDataSource {
    /**
     * Fetches all contacts with phone numbers from the device's Contacts Provider.
     * @return A list of [Contact] objects.
     */
    suspend fun getContacts(): List<Contact>

    /**
     * Fetches a single contact by its lookup key.
     * @param lookupKey The lookup key of the contact.
     * @return The [Contact] if found, null otherwise.
     */
    suspend fun getContact(lookupKey: String): Contact?
}
