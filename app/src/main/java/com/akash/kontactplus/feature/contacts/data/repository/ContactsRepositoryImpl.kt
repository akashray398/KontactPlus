package com.akash.kontactplus.feature.contacts.data.repository

import com.akash.kontactplus.feature.contacts.data.datasource.ContactsDataSource
import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/**
 * Data-level implementation of [ContactsRepository].
 */
class ContactsRepositoryImpl @Inject constructor(
    private val dataSource: ContactsDataSource
) : ContactsRepository {

    override suspend fun getContacts(): Result<List<Contact>> {
        return try {
            val contacts = dataSource.getContacts()
            Result.success(contacts)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
