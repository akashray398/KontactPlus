package com.akash.kontactplus.feature.contacts.data.datasource

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import com.akash.kontactplus.feature.contacts.domain.model.Contact
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Android-specific implementation of [ContactsDataSource] using [ContentResolver].
 */
class AndroidContactsDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : ContactsDataSource {

    override suspend fun getContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val resolver: ContentResolver = context.contentResolver
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )

        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} COLLATE NOCASE ASC"
        )

        cursor?.use { c ->
            val idIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val lookupIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)
            val nameIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
            val numberIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

            val contactMap = mutableMapOf<Long, ContactBuilder>()

            while (c.moveToNext()) {
                val id = c.getLong(idIndex)
                val lookupKey = c.getString(lookupIndex) ?: ""
                val name = c.getString(nameIndex) ?: ""
                val number = c.getString(numberIndex)?.trim() ?: ""
                val photoUri = c.getString(photoIndex)

                if (number.isNotBlank()) {
                    val builder = contactMap.getOrPut(id) {
                        ContactBuilder(id, lookupKey, name, photoUri)
                    }
                    if (builder.phoneNumbers.none { it == number }) {
                        builder.phoneNumbers.add(number)
                    }
                }
            }

            contactMap.values.map { it.build() }
                .sortedWith(compareBy({ it.displayName.isBlank() }, { it.displayName.lowercase() }))
        } ?: emptyList()
    }

    /**
     * Helper class to aggregate multiple phone numbers for a single contact.
     */
    private class ContactBuilder(
        val id: Long,
        val lookupKey: String,
        val displayName: String,
        val photoUri: String?
    ) {
        val phoneNumbers = mutableListOf<String>()

        fun build(): Contact = Contact(
            id = id,
            lookupKey = lookupKey,
            displayName = displayName,
            phoneNumbers = phoneNumbers.toList(),
            photoUri = photoUri
        )
    }
}
