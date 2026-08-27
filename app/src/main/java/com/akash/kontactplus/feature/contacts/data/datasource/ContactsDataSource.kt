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
}
