package com.akash.kontactplus.feature.dialpad.domain.usecase

import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
import com.akash.kontactplus.feature.dialpad.domain.model.DialpadSuggestion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case to find contact suggestions based on the entered number.
 */
class FindDialpadSuggestionsUseCase @Inject constructor(
    private val contactsRepository: ContactsRepository
) {
    suspend operator fun invoke(query: String, limit: Int = 5): List<DialpadSuggestion> {
        if (query.isBlank()) return emptyList()

        val normalizedQuery = query.filter { it.isDigit() || it == '+' }
        val contactsResult = contactsRepository.getContacts()
        val allContacts = contactsResult.getOrNull() ?: return emptyList()

        return allContacts.asSequence()
            .mapNotNull { contact ->
                val matchingNumber = contact.phoneNumbers.find { number ->
                    number.filter { it.isDigit() || it == '+' }.contains(normalizedQuery)
                }
                
                val nameMatches = contact.displayName.contains(query, ignoreCase = true)
                
                if (nameMatches || matchingNumber != null) {
                    DialpadSuggestion(
                        lookupKey = contact.lookupKey,
                        displayName = contact.displayName,
                        phoneNumber = matchingNumber ?: contact.phoneNumbers.firstOrNull() ?: "",
                        photoUri = contact.photoUri
                    )
                } else {
                    null
                }
            }
            .sortedWith(
                compareBy(
                    { !it.displayName.startsWith(query, ignoreCase = true) },
                    { !it.phoneNumber.filter { c -> c.isDigit() || c == '+' }.startsWith(normalizedQuery) },
                    { it.displayName.lowercase() }
                )
            )
            .take(limit)
            .toList()
    }
}
