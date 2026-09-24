package com.akash.kontactplus.feature.contacts.domain.usecase

import com.akash.kontactplus.feature.contacts.domain.model.Contact
import javax.inject.Inject

data class DuplicateContactPair(
    val primaryContact: Contact,
    val duplicateContact: Contact,
    val matchReason: String
)

class ContactFusionEngine @Inject constructor() {

    fun detectDuplicates(contacts: List<Contact>): List<DuplicateContactPair> {
        val duplicatePairs = mutableListOf<DuplicateContactPair>()
        val visited = mutableSetOf<Long>()

        for (i in contacts.indices) {
            val contactA = contacts[i]
            if (visited.contains(contactA.id)) continue

            for (j in i + 1 until contacts.indices.endInclusive + 1) {
                val contactB = contacts[j]
                if (visited.contains(contactB.id)) continue

                val matchReason = findMatchReason(contactA, contactB)
                if (matchReason != null) {
                    duplicatePairs.add(
                        DuplicateContactPair(
                            primaryContact = contactA,
                            duplicateContact = contactB,
                            matchReason = matchReason
                        )
                    )
                    visited.add(contactB.id)
                }
            }
        }
        return duplicatePairs
    }

    private fun findMatchReason(a: Contact, b: Contact): String? {
        val normNameA = a.displayName.trim().lowercase()
        val normNameB = b.displayName.trim().lowercase()

        // Exact name match
        if (normNameA.isNotEmpty() && normNameA == normNameB) {
            return "Exact Name Match: '$normNameA'"
        }

        // Shared phone number
        val numbersA = a.phoneNumbers.map { it.replace(Regex("[^0-9+]"), "") }.filter { it.length >= 7 }
        val numbersB = b.phoneNumbers.map { it.replace(Regex("[^0-9+]"), "") }.filter { it.length >= 7 }
        val sharedNumber = numbersA.firstOrNull { numA -> numbersB.any { numB -> numA.endsWith(numB) || numB.endsWith(numA) } }

        if (sharedNumber != null) {
            return "Shared Phone Number ($sharedNumber)"
        }

        return null
    }
}
