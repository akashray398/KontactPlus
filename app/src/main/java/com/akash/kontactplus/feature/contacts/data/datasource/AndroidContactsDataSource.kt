package com.akash.kontactplus.feature.contacts.data.datasource

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
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

    private val projection = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone.PHOTO_URI
    )

    override suspend fun getContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val resolver: ContentResolver = context.contentResolver
        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} COLLATE NOCASE ASC"
        )

        cursor?.use { mapCursorToContacts(it) } ?: emptyList()
    }

    override suspend fun getContact(lookupKey: String): Contact? = withContext(Dispatchers.IO) {
        if (lookupKey.isBlank()) return@withContext null
        
        val resolver: ContentResolver = context.contentResolver
        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            "${ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY} = ?",
            arrayOf(lookupKey),
            null
        )

        cursor?.use { mapCursorToContacts(it).firstOrNull() }
    }

    private fun mapCursorToContacts(cursor: Cursor): List<Contact> {
        val idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
        val lookupIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)
        val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
        val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val photoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

        val contactMap = mutableMapOf<Long, ContactBuilder>()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idIndex)
            val lookup = cursor.getString(lookupIndex) ?: ""
            val name = cursor.getString(nameIndex) ?: ""
            val number = cursor.getString(numberIndex)?.trim() ?: ""
            val photoUri = cursor.getString(photoIndex)

            if (number.isNotBlank()) {
                val builder = contactMap.getOrPut(id) {
                    ContactBuilder(id, lookup, name, photoUri)
                }
                if (builder.phoneNumbers.none { it == number }) {
                    builder.phoneNumbers.add(number)
                }
            }
        }

        return contactMap.values.map { it.build() }
            .sortedWith(compareBy({ it.displayName.isBlank() }, { it.displayName.lowercase() }))
    }

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
