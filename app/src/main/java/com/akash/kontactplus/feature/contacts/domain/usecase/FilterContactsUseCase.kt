package com.akash.kontactplus.feature.contacts.domain.usecase

import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.model.ContactSortOrder
import java.text.Collator
import java.util.Locale
import javax.inject.Inject

/**
 * Use case to filter and sort contacts based on a search query and sort order.
 */
class FilterContactsUseCase @Inject constructor() {

    private val collator = Collator.getInstance(Locale.getDefault()).apply {
        strength = Collator.PRIMARY
    }

    /**
     * Filters and sorts the given [contacts] list.
     *
     * @param contacts The original list of contacts.
     * @param query The search query to filter by name or phone number.
     * @param sortOrder The order to sort the results.
     * @return A filtered and sorted list of [Contact]s.
     */
    operator fun invoke(
        contacts: List<Contact>,
        query: String,
        sortOrder: ContactSortOrder
    ): List<Contact> {
        val trimmedQuery = query.trim()
        
        val filtered = if (trimmedQuery.isBlank()) {
            contacts
        } else {
            val normalizedQuery = trimmedQuery.normalizePhoneNumber()
            contacts.filter { contact ->
                val nameMatch = contact.displayName.contains(trimmedQuery, ignoreCase = true)
                val phoneMatch = if (normalizedQuery.isNotEmpty()) {
                    contact.phoneNumbers.any { it.normalizePhoneNumber().contains(normalizedQuery) }
                } else {
                    false
                }
                nameMatch || phoneMatch
            }
        }

        return filtered.sortedWith { c1, c2 ->
            val res = when {
                c1.displayName.isBlank() && c2.displayName.isNotBlank() -> 1
                c1.displayName.isNotBlank() && c2.displayName.isBlank() -> -1
                c1.displayName.isBlank() && c2.displayName.isBlank() -> {
                    c1.id.compareTo(c2.id)
                }
                else -> {
                    val comparison = collator.compare(c1.displayName, c2.displayName)
                    if (comparison == 0) c1.lookupKey.compareTo(c2.lookupKey) else comparison
                }
            }
            if (sortOrder == ContactSortOrder.NameDescending && c1.displayName.isNotBlank() && c2.displayName.isNotBlank()) {
                -res
            } else {
                res
            }
        }
    }

    private fun String.normalizePhoneNumber(): String {
        return this.filter { it.isDigit() || it == '+' }
    }
}
